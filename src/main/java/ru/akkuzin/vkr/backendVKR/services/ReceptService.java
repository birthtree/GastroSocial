package ru.akkuzin.vkr.backendVKR.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import ru.akkuzin.vkr.backendVKR.dto.*;
import ru.akkuzin.vkr.backendVKR.model.*;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;
import ru.akkuzin.vkr.backendVKR.repositories.ReceptIngredientRepository;
import ru.akkuzin.vkr.backendVKR.repositories.ReceptRepository;
import ru.akkuzin.vkr.backendVKR.util.PersonNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptSpecifications;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReceptService {
    private final ReceptRepository receptRepository;
    private final IngredientService ingredientService;
    private final FilterService filterService;
    private final PeopleRepository personRepository;
    private final PeopleService peopleService;
    private final ReceptIngredientRepository receptIngredientRepository;
    @Autowired
    public ReceptService(ReceptRepository receptRepository, IngredientService ingredientService, FilterService filterService, PeopleRepository personRepository, PeopleService peopleService, ReceptIngredientRepository receptIngredientRepository) {
        this.receptRepository = receptRepository;
        this.ingredientService = ingredientService;
        this.filterService = filterService;
        this.personRepository = personRepository;
        this.peopleService = peopleService;
        this.receptIngredientRepository = receptIngredientRepository;
    }

    public List<Recept> findAll() {
        return receptRepository.findAll();
    }

    public Recept findById(int id) {
           Optional<Recept> recept = receptRepository.findById(id);
        return recept.orElseThrow(ReceptNotFoundException::new);
    }



    @Transactional
    public void updateIngredients(Recept existing, List<String> ingredientNames, List<String> ingredientQuantities) {
        existing.getReceptIngredients().clear();

        for (int i = 0; i < ingredientNames.size(); i++) {
            String ingredientName = ingredientNames.get(i);
            Ingredient ingredient = ingredientService.getOrCreateIngredientByName(ingredientName);

            ReceptIngredient receptIngredient = new ReceptIngredient();
            receptIngredient.setRecept(existing);
            receptIngredient.setIngredient(ingredient);

            // Установить количество ингредиента, если передано
            if (ingredientQuantities != null && i < ingredientQuantities.size()) {
                receptIngredient.setQuantity(ingredientQuantities.get(i));
            }

            existing.getReceptIngredients().add(receptIngredient);
        }
    }

    @Transactional
    public void updateFilters(Recept existing, List<String> filterNames) {
        existing.getFilters().clear();
        for (String name : filterNames) {
            Filters filter = filterService.getOrCreateFilterByName(name);
            existing.getFilters().add(filter);
        }
    }



    @Transactional
    public Recept update(int id, Recept updatedRecept) {
        Recept recept = findById(id);

        // Обновляем основные поля
        recept.setName(updatedRecept.getName());
        recept.setDiscription(updatedRecept.getDiscription());
        recept.setDuration(updatedRecept.getDuration());
        recept.setPrivate(updatedRecept.isPrivate());
        recept.setImageUrl(updatedRecept.getImageUrl());
        recept.setCookingSteps(updatedRecept.getCookingSteps());

        // Удаляем старые ингредиенты (минимальное изменение)
        if (recept.getReceptIngredients() != null) {
            // Сначала удаляем из базы
            receptIngredientRepository.deleteAllByReceptId(recept.getId());
            // Затем очищаем коллекцию
            recept.getReceptIngredients().clear();
        }

        // Создаем новые связи с количествами (остаётся без изменений)
        if (updatedRecept.getIngredientNames() != null && updatedRecept.getIngredientQuantities() != null) {
            for (int i = 0; i < updatedRecept.getIngredientNames().size(); i++) {
                Ingredient ingredient = ingredientService.getOrCreateIngredientByName(
                        updatedRecept.getIngredientNames().get(i).trim());

                ReceptIngredient ri = new ReceptIngredient();
                ri.setRecept(recept);
                ri.setIngredient(ingredient);
                ri.setQuantity(updatedRecept.getIngredientQuantities().get(i));
                recept.getReceptIngredients().add(ri);
            }
        }

        // Обработка фильтров (остаётся без изменений)
        if (updatedRecept.getFilterNames() != null) {
            Set<Filters> filters = updatedRecept.getFilterNames().stream()
                    .map(name -> filterService.getOrCreateFilterByName(name.trim()))
                    .collect(Collectors.toSet());
            recept.setFilters(filters);
        }

        return receptRepository.save(recept);
    }


    @Transactional
    public Recept save(Recept recept) {

        // Обработка владельца по email
        if (recept.getOwner().getEmail()!= null) {
            Person owner = personRepository.findByEmail(recept.getOwner().getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
            recept.setOwner(owner);
        }
        peopleService.checkIfUserActive(recept.getOwner().getEmail());
        // Обработка ингредиентов
        // Обработка ингредиентов с quantity
        if (recept.getIngredientNames() != null && recept.getIngredientQuantities() != null) {
            // Предполагаем, что recept теперь имеет List<ReceptIngredientDTO> или Map<Ingredient, String>
            Set<ReceptIngredient> receptIngredients = new HashSet<>();
            for (int i = 0; i < recept.getIngredientNames().size(); i++) {
                Ingredient ingredient = ingredientService.getOrCreateIngredientByName(
                        recept.getIngredientNames().get(i).trim());

                ReceptIngredient ri = new ReceptIngredient();
                ri.setRecept(recept);
                ri.setIngredient(ingredient);
                ri.setQuantity(recept.getIngredientQuantities().get(i));
                receptIngredients.add(ri);
            }
            recept.setReceptIngredients(receptIngredients);
        }

        if (recept.getFilterNames() != null) {
            Set<Filters> filters = recept.getFilterNames().stream()
                    .map(name -> filterService.getOrCreateFilterByName(name.trim()))
                    .collect(Collectors.toSet());
            recept.setFilters(filters);
        }

        return receptRepository.save(recept);
    }

    /*  @Transactional
    public Recept update(int id, Recept updatedRecept, List<String> ingredientNames, List<String> filterNames) {
        Recept recept = findById(id);
        peopleService.checkIfUserActive(recept.getOwner().getEmail());
        // Обновляем основные поля
        recept.setName(updatedRecept.getName());
        recept.setDiscription(updatedRecept.getDiscription());
        recept.setDuration(updatedRecept.getDuration());
        recept.setPrivate(updatedRecept.isPrivate());

        // Обновляем ингредиенты по названиям
        if (ingredientNames != null) {
            Set<Ingredient> ingredients = ingredientNames.stream()
                    .map(name -> ingredientService.getOrCreateIngredientByName(name.trim()))
                    .collect(Collectors.toSet());
            recept.setIngredients(ingredients);
        }

        // Обновляем фильтры по названиям
        if (filterNames != null) {
            Set<Filters> filters = filterNames.stream()
                    .map(name -> filterService.getOrCreateFilterByName(name.trim()))
                    .collect(Collectors.toSet());
            recept.setFilters(filters);
        }

        return receptRepository.save(recept);
    }*/

    public List<Recept> findByOwner(Person owner) {
        return receptRepository.findByOwner(owner);
    }

    public List<Recept> getRecipesByOwnerEmail(String email) {
        Person owner = peopleService.findByEmail(email);
        return receptRepository.findByOwner(owner); // предполагая, что у вас есть такой метод в репозитории
    }


    @Transactional
    public void deleteById(int id, String currentUserEmail) {
        // 1. Получаем рецепт без инициализации коллекций
        Recept recept = receptRepository.findById(id)
                .orElseThrow(() -> new ReceptNotFoundException());

        // 2. Получаем текущего пользователя
        Person currentUser = personRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 3. Проверяем права
        validateDeletePermissions(recept, currentUser);

        // 4. Удаляем связи в правильном порядке
        try {
            // Сначала удаляем из избранного
            removeFromFavorites(recept.getId()); // Передаем только ID

            // Затем удаляем связи с ингредиентами
            receptIngredientRepository.deleteByReceptId(recept.getId());

            // Очищаем связи с фильтрами (если нужно)
            if (recept.getFilters() != null) {
                recept.getFilters().clear();
            }

            // И только потом удаляем сам рецепт
            receptRepository.delete(recept);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting recipe", e);
        }
    }

    private void validateDeletePermissions(Recept recept, Person currentUser) {
        // Проверяем, активен ли пользователь
        if (!currentUser.isActive()) {
            throw new DisabledException("User is blocked");
        }

        // Администратор может удалять любые рецепты
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            return;
        }

        // Обычный пользователь может удалять только свои рецепты
        if (!recept.getOwner().getEmail().equals(currentUser.getEmail())) {
            throw new SecurityException("You can only delete your own recipes");
        }
    }

    @Transactional
    private void removeFromFavorites(int receptId) {
        List<Person> users = personRepository.findUsersWithFavoriteRecipe(receptId);
        if (users != null && !users.isEmpty()) {
            users.forEach(user -> {
                user.getFavoriteRecepts().removeIf(r -> r.getId() == receptId);
            });
            personRepository.saveAll(users);
        }
    }

    private void deleteReceptIngredients(Recept recept) {
        // Явное удаление через репозиторий
        receptIngredientRepository.deleteAll(recept.getReceptIngredients());
        recept.getReceptIngredients().clear();
    }

    private void deleteReceptFilters(Recept recept) {
        // Для ManyToMany просто очищаем коллекцию
        recept.getFilters().clear();
    }

    public List<Recept> findByNameContainingRaw(String name) {
        return receptRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Recept> findByIngredientNamesRaw(Set<String> ingredientNames) {
        return receptRepository.findByIngredientsNameIn(ingredientNames);
    }

    public List<Recept> findByFilterNamesRaw(Set<String> filterNames) {
        return receptRepository.findByFiltersNameOfFilterIn(filterNames);
    }

    public List<ReceptResponseDTO> combinedSearch(String name, Set<String> ingredientNames, Set<String> filterNames) {
        List<Recept> recepts;

        if (name != null && ingredientNames != null && filterNames != null) {
            recepts = receptRepository.findByNameContainingIgnoreCaseAndIngredientsNameInAndFiltersNameOfFilterIn(
                    name, ingredientNames, filterNames);
        } else if (name != null && ingredientNames != null) {
            recepts = receptRepository.findByNameContainingIgnoreCaseAndIngredientsNameIn(name, ingredientNames);
        } else if (name != null && filterNames != null) {
            recepts = receptRepository.findByNameContainingIgnoreCaseAndFiltersNameOfFilterIn(name, filterNames);
        } else if (ingredientNames != null && filterNames != null) {
            recepts = receptRepository.findByIngredientsNameInAndFiltersNameOfFilterIn(ingredientNames, filterNames);
        } else if (name != null) {
            recepts = findByNameContainingRaw(name); // Используем Raw-версию
        } else if (ingredientNames != null) {
            recepts = findByIngredientNamesRaw(ingredientNames); // Используем Raw-версию
        } else if (filterNames != null) {
            recepts = findByFilterNamesRaw(filterNames); // Используем Raw-версию
        } else {
            recepts = receptRepository.findAll();
        }

        // Преобразуем в DTO только в конце
        return recepts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }



    public List<ReceptResponseDTO> findByNameContaining(String name) {
        return findByNameContainingRaw(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReceptResponseDTO> findByIngredientNames(Set<String> ingredientNames) {
        return findByIngredientNamesRaw(ingredientNames).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReceptResponseDTO> findByFilterNames(Set<String> filterNames) {
        return findByFilterNamesRaw(filterNames).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }






    public List<ReceptResponseDTO> findByDuration(String maxDuration, String minDuration) {
        List<Recept> recepts;

        if (maxDuration != null && minDuration != null) {
            recepts = receptRepository.findByDurationBetween(
                    Time.valueOf(minDuration + ":00"),
                    Time.valueOf(maxDuration + ":00"));
        } else if (maxDuration != null) {
            recepts = receptRepository.findByDurationLessThanEqual(
                    Time.valueOf(maxDuration + ":00"));
        } else if (minDuration != null) {
            recepts = receptRepository.findByDurationGreaterThanEqual(
                    Time.valueOf(minDuration + ":00"));
        } else {
            recepts = Collections.emptyList();
        }

        return recepts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReceptResponseDTO> advancedSearch(String name,
                                                  Set<String> ingredientNames,
                                                  Set<String> filterNames,
                                                  String maxDuration,
                                                  String minDuration) {

        Specification<Recept> spec = Specification.where(null);

        if (name != null && !name.isBlank()) {
            spec = spec.and(ReceptSpecifications.nameContains(name));
        }
        if (ingredientNames != null && !ingredientNames.isEmpty()) {
            spec = spec.and(ReceptSpecifications.hasIngredients(ingredientNames));
        }
        if (filterNames != null && !filterNames.isEmpty()) {
            spec = spec.and(ReceptSpecifications.hasFilters(filterNames));
        }

        try {
            if (maxDuration != null && !maxDuration.isBlank()) {
                spec = spec.and(ReceptSpecifications.durationLessThanOrEqual(
                        parseTimeString(maxDuration)));
            }
            if (minDuration != null && !minDuration.isBlank()) {
                spec = spec.and(ReceptSpecifications.durationGreaterThanOrEqual(
                        parseTimeString(minDuration)));
            }
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid time format. Use HH:mm or HH:mm:ss");
        }

        return receptRepository.findAll(spec).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private Time parseTimeString(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return null;
        }

        // Нормализуем строку времени
        timeStr = timeStr.trim();

        // Добавляем секунды, если их нет
        if (timeStr.matches("^\\d{1,2}:\\d{2}$")) {
            timeStr += ":00";
        } else if (!timeStr.matches("^\\d{1,2}:\\d{2}:\\d{2}$")) {
            throw new IllegalArgumentException("Invalid time format");
        }

        try {
            return Time.valueOf(timeStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid time value");
        }
    }

    private ReceptResponseDTO convertToResponseDTO(Recept recept) {
        ReceptResponseDTO dto = new ReceptResponseDTO();
        dto.setId(recept.getId());
        dto.setName(recept.getName());
        dto.setDescription(recept.getDiscription());
        dto.setDuration(recept.getDuration());
        dto.setPrivate(recept.isPrivate());
        dto.setImageUrl(recept.getImageUrl());

        if (recept.getCookingStepsJson() != null && !recept.getCookingStepsJson().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<String> steps = mapper.readValue(
                        recept.getCookingStepsJson(),
                        new TypeReference<List<String>>(){}
                );
                dto.setCookingSteps(steps);
            } catch (Exception e) {
                dto.setCookingSteps(Collections.emptyList());
            }
        } else {
            dto.setCookingSteps(Collections.emptyList());
        }

        // Преобразование владельца
        if (recept.getOwner() != null) {
            OwnerDTO ownerDto = new OwnerDTO();
            ownerDto.setId(recept.getOwner().getId());
            ownerDto.setEmail(recept.getOwner().getEmail());
            ownerDto.setName(recept.getOwner().getName());
            ownerDto.setSecondName(recept.getOwner().getSecondName());
            ownerDto.setPatronymic(recept.getOwner().getPatronymic());
            dto.setOwner(ownerDto);
        }

        // Преобразование ингредиентов через ReceptIngredient
        if (recept.getReceptIngredients() != null) {
            List<ReceptIngredientDTO> ingredients = recept.getReceptIngredients().stream()
                    .map(ri -> new ReceptIngredientDTO(
                            ri.getIngredient().getId(),
                            ri.getIngredient().getName(),
                            ri.getQuantity() // Добавляем количество
                    ))
                    .collect(Collectors.toList());
            dto.setIngredients(ingredients);
        }

        // Преобразование фильтров
        if (recept.getFilters() != null) {
            List<FilterDTO> filters = recept.getFilters().stream()
                    .map(f -> new FilterDTO(f.getId(), f.getNameOfFilter(), f.getTypeOfFilter()))
                    .collect(Collectors.toList());
            dto.setFilters(filters);
        }

        return dto;
    }

    @Transactional
    public void addToFavorites(String userEmail, int receptId) {
        Person person = personRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Recept recept = receptRepository.findById(receptId)
                .orElseThrow(() -> new EntityNotFoundException("Recept not found"));

        if (recept.isPrivate() && !recept.getOwner().getEmail().equals(userEmail)) {
            throw new SecurityException("Cannot add private recept to favorites");
        }

        person.getFavoriteRecepts().add(recept);
        personRepository.save(person);
    }

    @Transactional
    public void removeFromFavorites(String userEmail, int receptId) {

        Person person = personRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        person.getFavoriteRecepts().removeIf(r -> r.getId() == receptId);
        personRepository.save(person);
    }

    @Transactional(readOnly = true)
    public List<Recept> getUserFavorites(String userEmail) {
        return personRepository.findByEmail(userEmail)
                .map(p -> new ArrayList<>(p.getFavoriteRecepts()))
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String userEmail, int receptId) {
        return personRepository.findByEmail(userEmail)
                .map(p -> p.getFavoriteRecepts().stream()
                        .anyMatch(r -> r.getId() == receptId))
                .orElse(false);
    }



    public Optional<ReceptResponseDTO> getRandomRecept(
            String name,
            Set<String> ingredientNames,
            Set<String> filterNames,
            String maxDuration,
            String minDuration
    ) {
        Specification<Recept> spec = buildRandomSpecification(
                name, ingredientNames, filterNames, maxDuration, minDuration
        );

        List<Recept> allRecepts = receptRepository.findAll(spec);

        return allRecepts.isEmpty()
                ? Optional.empty()
                : Optional.of(convertToResponseDTO(getRandomElement(allRecepts)));
    }

    private Specification<Recept> buildRandomSpecification(
            String name,
            Set<String> ingredientNames,
            Set<String> filterNames,
            String maxDuration,
            String minDuration
    ) {
        return Specification.where(name != null ? ReceptSpecifications.nameContains(name) : null)
                .and(!CollectionUtils.isEmpty(ingredientNames) ?
                        ReceptSpecifications.hasIngredients(ingredientNames) : null)
                .and(!CollectionUtils.isEmpty(filterNames) ?
                        ReceptSpecifications.hasFilters(filterNames) : null)
                .and(minDuration != null ?
                        ReceptSpecifications.durationGreaterThanOrEqual(parseTimeString(minDuration)) : null)
                .and(maxDuration != null ?
                        ReceptSpecifications.durationLessThanOrEqual(parseTimeString(maxDuration)) : null);
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(new Random().nextInt(list.size()));
    }





}

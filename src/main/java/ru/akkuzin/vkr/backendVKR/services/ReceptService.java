package ru.akkuzin.vkr.backendVKR.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.dto.*;
import ru.akkuzin.vkr.backendVKR.model.*;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;
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
    @Autowired
    public ReceptService(ReceptRepository receptRepository, IngredientService ingredientService, FilterService filterService, PeopleRepository personRepository) {
        this.receptRepository = receptRepository;
        this.ingredientService = ingredientService;
        this.filterService = filterService;
        this.personRepository = personRepository;
    }

    public List<Recept> findAll() {
        return receptRepository.findAll();
    }

    public Recept findById(int id) {
           Optional<Recept> recept = receptRepository.findById(id);
        return recept.orElseThrow(ReceptNotFoundException::new);
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

        // Обработка ингредиентов с количествами
        if (updatedRecept.getIngredientNames() != null &&
                updatedRecept.getIngredientQuantities() != null) {

            // Удаляем старые связи
            recept.getReceptIngredients().clear();

            // Создаем новые связи с количествами
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

        // Обработка фильтров
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

    @Transactional
    public Recept update(int id, Recept updatedRecept, List<String> ingredientNames, List<String> filterNames) {
        Recept recept = findById(id);

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
    }
    @Transactional
    public void deleteById(int id) {
        if (!receptRepository.existsById(id)) {
            throw new ReceptNotFoundException();
        }
        receptRepository.deleteById(id);
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

        if (name != null) {
            spec = spec.and(ReceptSpecifications.nameContains(name));
        }
        if (ingredientNames != null && !ingredientNames.isEmpty()) {
            spec = spec.and(ReceptSpecifications.hasIngredients(ingredientNames));
        }
        if (filterNames != null && !filterNames.isEmpty()) {
            spec = spec.and(ReceptSpecifications.hasFilters(filterNames));
        }
        if (maxDuration != null) {
            spec = spec.and(ReceptSpecifications.durationLessThanOrEqual(
                    Time.valueOf(maxDuration + ":00")));
        }
        if (minDuration != null) {
            spec = spec.and(ReceptSpecifications.durationGreaterThanOrEqual(
                    Time.valueOf(minDuration + ":00")));
        }

        List<Recept> recepts = receptRepository.findAll(spec);

        return recepts.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
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

        // Преобразование ингредиентов
        if (recept.getReceptIngredients() != null) {
            List<ReceptIngredientDTO> ingredients = recept.getReceptIngredients().stream()
                    .map(ri -> new ReceptIngredientDTO(
                            ri.getIngredient().getId(),
                            ri.getIngredient().getName(),
                            ri.getQuantity() // Добавляем количество
                    ))
                    .collect(Collectors.toList());
            dto.setIngredients(ingredients);
        } else if (recept.getIngredients() != null) { // Для обратной совместимости
            List<ReceptIngredientDTO> ingredients = recept.getIngredients().stream()
                    .map(ing -> new ReceptIngredientDTO(
                            ing.getId(),
                            ing.getName(),
                            "по вкусу" // Значение по умолчанию
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


}

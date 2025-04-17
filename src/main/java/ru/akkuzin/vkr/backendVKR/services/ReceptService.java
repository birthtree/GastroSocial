package ru.akkuzin.vkr.backendVKR.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.dto.FilterDTO;
import ru.akkuzin.vkr.backendVKR.dto.IngredientDTO;
import ru.akkuzin.vkr.backendVKR.dto.OwnerDTO;
import ru.akkuzin.vkr.backendVKR.dto.ReceptResponseDTO;
import ru.akkuzin.vkr.backendVKR.model.Filters;
import ru.akkuzin.vkr.backendVKR.model.Ingredient;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;
import ru.akkuzin.vkr.backendVKR.repositories.ReceptRepository;
import ru.akkuzin.vkr.backendVKR.util.PersonNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptNotFoundException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptSpecifications;

import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    public Recept save(Recept recept) {
        // Обработка владельца по email
        if (recept.getOwner().getEmail()!= null) {
            Person owner = personRepository.findByEmail(recept.getOwner().getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
            recept.setOwner(owner);
        }

        // Обработка ингредиентов
        if (recept.getIngredientNames() != null) {
            Set<Ingredient> ingredients = recept.getIngredientNames().stream()
                    .map(name -> ingredientService.getOrCreateIngredientByName(name.trim()))
                    .collect(Collectors.toSet());
            recept.setIngredients(ingredients);
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
        if (recept.getIngredients() != null) {
            List<IngredientDTO> ingredients = recept.getIngredients().stream()
                    .map(ing -> new IngredientDTO(ing.getId(), ing.getName()))
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

}

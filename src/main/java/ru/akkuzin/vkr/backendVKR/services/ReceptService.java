package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Ingredient;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
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
    @Autowired
    public ReceptService(ReceptRepository receptRepository, IngredientService ingredientService) {
        this.receptRepository = receptRepository;
        this.ingredientService = ingredientService;
    }

    public List<Recept> findAll() {
        return receptRepository.findAll();
    }

    public Recept findById(int id) {
           Optional<Recept> recept = receptRepository.findById(id);
        return recept.orElseThrow(ReceptNotFoundException::new);
    }



    @Transactional
    public void save(Recept recept) {
        if (recept.getIngredientNames() != null) {
            Set<Ingredient> ingredients = recept.getIngredientNames().stream()
                    .map(name -> ingredientService.getOrCreateIngredientByName(name.trim()))
                    .collect(Collectors.toSet()); // <-- важно: TO SET

            recept.setIngredients(ingredients);
        }

        receptRepository.save(recept);
    }
    @Transactional
    public void update(int id, Recept updatedRecept) {
        Recept receptFromDB = findById(id); // Используем существующий метод поиска

        // Обновляем основные поля
        receptFromDB.setName(updatedRecept.getName());
        receptFromDB.setDiscription(updatedRecept.getDiscription());
        receptFromDB.setDuration(updatedRecept.getDuration());
        receptFromDB.setPrivate(updatedRecept.isPrivate());
        receptFromDB.setOwner(updatedRecept.getOwner());

        // Обработка ингредиентов (аналогично save)
        if (updatedRecept.getIngredientNames() != null) {
            Set<Ingredient> ingredients = updatedRecept.getIngredientNames().stream()
                    .map(name -> ingredientService.getOrCreateIngredientByName(name.trim()))
                    .collect(Collectors.toSet());

            // Очищаем старые и устанавливаем новые ингредиенты
            receptFromDB.getIngredients().clear();
            receptFromDB.getIngredients().addAll(ingredients);
        }

        receptRepository.save(receptFromDB);
    }
    @Transactional
    public void deleteById(int id) {
        if (!receptRepository.existsById(id)) {
            throw new ReceptNotFoundException();
        }
        receptRepository.deleteById(id);
    }
    public List<Recept> findByNameContaining(String name) {
        return receptRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Recept> findByIngredientNames(Set<String> ingredientNames) {
        return receptRepository.findByIngredientsNameIn(ingredientNames);
    }

    public List<Recept> findByFilterNames(Set<String> filterNames) {
        return receptRepository.findByFiltersNameOfFilterIn(filterNames);
    }

    public List<Recept> combinedSearch(String name, Set<String> ingredientNames, Set<String> filterNames) {
        if (name != null && ingredientNames != null && filterNames != null) {
            return receptRepository.findByNameContainingIgnoreCaseAndIngredientsNameInAndFiltersNameOfFilterIn(
                    name, ingredientNames, filterNames);
        } else if (name != null && ingredientNames != null) {
            return receptRepository.findByNameContainingIgnoreCaseAndIngredientsNameIn(name, ingredientNames);
        } else if (name != null && filterNames != null) {
            return receptRepository.findByNameContainingIgnoreCaseAndFiltersNameOfFilterIn(name, filterNames);
        } else if (ingredientNames != null && filterNames != null) {
            return receptRepository.findByIngredientsNameInAndFiltersNameOfFilterIn(ingredientNames, filterNames);
        } else if (name != null) {
            return findByNameContaining(name);
        } else if (ingredientNames != null) {
            return findByIngredientNames(ingredientNames);
        } else if (filterNames != null) {
            return findByFilterNames(filterNames);
        } else {
            return receptRepository.findAll();
        }
    }
    public List<Recept> findByDuration(String maxDuration, String minDuration) {
        if (maxDuration != null && minDuration != null) {
            return receptRepository.findByDurationBetween(
                    Time.valueOf(minDuration + ":00"),
                    Time.valueOf(maxDuration + ":00"));
        } else if (maxDuration != null) {
            return receptRepository.findByDurationLessThanEqual(
                    Time.valueOf(maxDuration + ":00"));
        } else if (minDuration != null) {
            return receptRepository.findByDurationGreaterThanEqual(
                    Time.valueOf(minDuration + ":00"));
        }
        return Collections.emptyList();
    }

    public List<Recept> advancedSearch(String name, Set<String> ingredientNames,
                                       Set<String> filterNames, String maxDuration,
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

        return receptRepository.findAll(spec);
    }

}

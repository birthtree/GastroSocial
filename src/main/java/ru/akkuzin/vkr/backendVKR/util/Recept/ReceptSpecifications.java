package ru.akkuzin.vkr.backendVKR.util.Recept;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import ru.akkuzin.vkr.backendVKR.model.Filters;
import ru.akkuzin.vkr.backendVKR.model.Ingredient;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.model.ReceptIngredient;

import java.sql.Time;
import java.util.Set;

public class ReceptSpecifications {

    public static Specification<Recept> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Recept> hasIngredients(Set<String> ingredientNames) {
        return (root, query, cb) -> {
            if (ingredientNames == null || ingredientNames.isEmpty()) {
                return cb.conjunction(); // возвращаем "true" если нет параметров
            }

            // Создаем join с промежуточной таблицей и затем с ингредиентами
            Join<Recept, ReceptIngredient> receptIngredientJoin = root.join("receptIngredients");
            Join<ReceptIngredient, Ingredient> ingredientJoin = receptIngredientJoin.join("ingredient");

            return ingredientJoin.get("name").in(ingredientNames);
        };
    }

    public static Specification<Recept> hasFilters(Set<String> filterNames) {
        return (root, query, cb) -> {
            Join<Recept, Filters> filtersJoin = root.join("filters");
            return filtersJoin.get("nameOfFilter").in(filterNames);
        };
    }

    public static Specification<Recept> durationLessThanOrEqual(Time maxTime) {
        return (root, query, cb) ->
                maxTime == null ?
                        cb.conjunction() :
                        cb.lessThanOrEqualTo(root.get("duration"), maxTime);
    }

    public static Specification<Recept> durationGreaterThanOrEqual(Time minTime) {
        return (root, query, cb) ->
                minTime == null ?
                        cb.conjunction() :
                        cb.greaterThanOrEqualTo(root.get("duration"), minTime);
    }
}
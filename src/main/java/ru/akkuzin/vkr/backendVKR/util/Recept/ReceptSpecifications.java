package ru.akkuzin.vkr.backendVKR.util.Recept;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import ru.akkuzin.vkr.backendVKR.model.Filters;
import ru.akkuzin.vkr.backendVKR.model.Ingredient;
import ru.akkuzin.vkr.backendVKR.model.Recept;

import java.sql.Time;
import java.util.Set;

public class ReceptSpecifications {

    public static Specification<Recept> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Recept> hasIngredients(Set<String> ingredientNames) {
        return (root, query, cb) -> {
            Join<Recept, Ingredient> ingredientsJoin = root.join("ingredients");
            return ingredientsJoin.get("name").in(ingredientNames);
        };
    }

    public static Specification<Recept> hasFilters(Set<String> filterNames) {
        return (root, query, cb) -> {
            Join<Recept, Filters> filtersJoin = root.join("filters");
            return filtersJoin.get("nameOfFilter").in(filterNames);
        };
    }

    public static Specification<Recept> durationLessThanOrEqual(Time maxDuration) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("duration"), maxDuration);
    }

    public static Specification<Recept> durationGreaterThanOrEqual(Time minDuration) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("duration"), minDuration);
    }
}
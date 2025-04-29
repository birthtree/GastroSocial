package ru.akkuzin.vkr.backendVKR.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.akkuzin.vkr.backendVKR.dto.IngredientDTO;
import ru.akkuzin.vkr.backendVKR.dto.ShoppingListDTO;
import ru.akkuzin.vkr.backendVKR.model.Ingredient;
import ru.akkuzin.vkr.backendVKR.model.Mealplan;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.ReceptIngredient;
import ru.akkuzin.vkr.backendVKR.repositories.MealPlanRepository;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Service
public class ShoppingListService {
    @Autowired
    private MealPlanRepository mealPlanRepository;

    @Autowired
    private PeopleRepository personRepository;

    public ShoppingListDTO generateShoppingList(LocalDate date, String email) {
        // Находим пользователя по email
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        // Явное приведение к Long
        Long personId = (long) person.getId();

        // Получаем планы меню
        List<Mealplan> mealPlans = mealPlanRepository.findByDateAndPersonId(date, personId);

        Set<Ingredient> ingredients = new HashSet<>();
        for (Mealplan mealPlan : mealPlans) {
            // Извлекаем ингредиенты через ReceptIngredient
            for (ReceptIngredient receptIngredient : mealPlan.getRecept().getReceptIngredients()) {
                ingredients.add(receptIngredient.getIngredient());  // Получаем ингредиент из ReceptIngredient
            }
        }

        return new ShoppingListDTO(
                date,
                ingredients.stream()
                        .map(i -> new IngredientDTO(i.getId(), i.getName()))
                        .collect(Collectors.toList())
        );
    }
}

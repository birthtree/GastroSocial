package ru.akkuzin.vkr.backendVKR.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.dto.ReceptIngredientDTO;
import ru.akkuzin.vkr.backendVKR.dto.ShoppingListDTO;
import ru.akkuzin.vkr.backendVKR.model.*;
import ru.akkuzin.vkr.backendVKR.repositories.MealPlanRepository;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShoppingListService {

    private final MealPlanRepository mealPlanRepository;
    private final PeopleRepository personRepository;

    @Autowired
    public ShoppingListService(MealPlanRepository mealPlanRepository,
                               PeopleRepository personRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.personRepository = personRepository;
    }

    public ShoppingListDTO generateShoppingList(LocalDate date, String email) {
        // Находим пользователя по email
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        // Получаем планы меню для пользователя на указанную дату
        List<Mealplan> mealPlans = mealPlanRepository.findByDateAndPersonId(date, person.getId());

        // Собираем все ингредиенты с их количествами (без объединения дубликатов)
        List<ReceptIngredientDTO> ingredients = mealPlans.stream()
                .flatMap(mealPlan -> mealPlan.getRecept().getReceptIngredients().stream())
                .map(ri -> new ReceptIngredientDTO(
                        ri.getIngredient().getId(),
                        ri.getIngredient().getName(),
                        ri.getQuantity()))
                .collect(Collectors.toList());

        return new ShoppingListDTO(date, ingredients);
    }
}
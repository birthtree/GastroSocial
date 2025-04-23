package ru.akkuzin.vkr.backendVKR.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.dto.MealPlanDTO;
import ru.akkuzin.vkr.backendVKR.dto.ShoppingListDTO;
import ru.akkuzin.vkr.backendVKR.services.MealPlanService;
import ru.akkuzin.vkr.backendVKR.services.ShoppingListService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MealPlanController {
    @Autowired
    private MealPlanService mealPlanService;

    @Autowired
    private ShoppingListService shoppingListService;

    @PostMapping("/plan")
    public ResponseEntity<?> addToMealPlan(@RequestBody MealPlanDTO mealPlanDTO) {
        mealPlanService.addToMealPlan(mealPlanDTO);
        return ResponseEntity.ok("Рецепт добавлен в план меню");
    }

    @GetMapping("/plan")
    public ResponseEntity<List<MealPlanDTO>> getMealPlanByDate(
            @RequestParam LocalDate date,
            @RequestParam String email) {
        return ResponseEntity.ok(mealPlanService.getMealPlanByDate(date, email));
    }

    @DeleteMapping("/plan/{id}")
    public ResponseEntity<?> deleteFromMealPlan(@PathVariable Long id) {
        mealPlanService.deleteFromMealPlan(id);
        return ResponseEntity.ok("Рецепт удалён из плана меню");
    }

    @GetMapping("/shopping-list")
    public ResponseEntity<ShoppingListDTO> getShoppingList(
            @RequestParam LocalDate date,
            @RequestParam String email) {

        return ResponseEntity.ok(shoppingListService.generateShoppingList(date, email));
    }
}
package ru.akkuzin.vkr.backendVKR.dto;

import java.time.LocalDate;
import java.util.List;

public class ShoppingListDTO {
    private LocalDate date;
    private List<ReceptIngredientDTO> ingredients;

    // Конструкторы
    public ShoppingListDTO() {}

    public ShoppingListDTO(LocalDate date, List<ReceptIngredientDTO> ingredients) {
        this.date = date;
        this.ingredients = ingredients;
    }

    // Геттеры и сеттеры
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<ReceptIngredientDTO> getIngredients() { return ingredients; }
    public void setIngredients(List<ReceptIngredientDTO> ingredients) { this.ingredients = ingredients; }
}
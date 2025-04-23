package ru.akkuzin.vkr.backendVKR.dto;

import java.time.LocalDate;
import java.util.List;

public class ShoppingListDTO {
    private LocalDate date;
    private List<IngredientDTO> ingredients;

    public ShoppingListDTO() {
    }

    public ShoppingListDTO(LocalDate date, List<IngredientDTO> ingredients) {
        this.date = date;
        this.ingredients = ingredients;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }
}

package ru.akkuzin.vkr.backendVKR.model;

import java.io.Serializable;
import java.util.Objects;

public class ReceptIngredientId implements Serializable {
    private int recept;
    private int ingredient;

    // Геттеры, сеттеры, equals, hashCode
    public int getRecept() { return recept; }
    public void setRecept(int recept) { this.recept = recept; }

    public int getIngredient() { return ingredient; }
    public void setIngredient(int ingredient) { this.ingredient = ingredient; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceptIngredientId that = (ReceptIngredientId) o;
        return recept == that.recept && ingredient == that.ingredient;
    }

    @Override
    public int hashCode() {
        return Objects.hash(recept, ingredient);
    }
}
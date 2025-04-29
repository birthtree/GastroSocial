package ru.akkuzin.vkr.backendVKR.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Recept_ingredients")
@IdClass(ReceptIngredientId.class)
public class ReceptIngredient {
    @Id
    @ManyToOne
    @JoinColumn(name = "recept_id")
    private Recept recept;

    @Id
    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(name = "quantity")
    private String quantity;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceptIngredient that = (ReceptIngredient) o;
        return Objects.equals(recept, that.recept) &&
                Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recept, ingredient);
    }

    // Геттеры и сеттеры
    public Recept getRecept() { return recept; }
    public void setRecept(Recept recept) { this.recept = recept; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}
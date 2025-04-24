package ru.akkuzin.vkr.backendVKR.model;

import jakarta.persistence.*;

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

    // Геттеры и сеттеры
    public Recept getRecept() { return recept; }
    public void setRecept(Recept recept) { this.recept = recept; }

    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}
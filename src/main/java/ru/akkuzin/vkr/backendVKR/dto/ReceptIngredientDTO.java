package ru.akkuzin.vkr.backendVKR.dto;

public class ReceptIngredientDTO {
    private int ingredientId;
    private String name;
    private String quantity;

    // Конструкторы
    public ReceptIngredientDTO() {}

    public ReceptIngredientDTO(int ingredientId, String name, String quantity) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.quantity = quantity;
    }

    // Геттеры и сеттеры
    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}
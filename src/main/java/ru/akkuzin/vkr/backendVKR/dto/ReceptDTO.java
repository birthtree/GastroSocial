
package ru.akkuzin.vkr.backendVKR.dto;

import java.sql.Time;
import java.util.List;
import java.util.Set;

public class ReceptDTO {
    private int id;
    private String name;
    private String description;
    private Time duration;
    private boolean isPrivate;
    private Integer ownerId; // Только ID владельца вместо всей сущности
    private Set<Integer> ingredientIds; // Только ID ингредиентов
    private Set<Integer> filterIds; // Только ID фильтров
    private List<String> ingredientNames; // Транзиентное поле из сущности
    private String imageUrl;
    private List<String> cookingSteps;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getCookingSteps() {
        return cookingSteps;
    }

    public void setCookingSteps(List<String> cookingSteps) {
        this.cookingSteps = cookingSteps;
    }

    // Конструкторы
    public ReceptDTO() {
    }

    public ReceptDTO(int id, String name, String description, Time duration,
                     boolean isPrivate, Integer ownerId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.isPrivate = isPrivate;
        this.ownerId = ownerId;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Time getDuration() {
        return duration;
    }

    public void setDuration(Time duration) {
        this.duration = duration;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public Set<Integer> getIngredientIds() {
        return ingredientIds;
    }

    public void setIngredientIds(Set<Integer> ingredientIds) {
        this.ingredientIds = ingredientIds;
    }

    public Set<Integer> getFilterIds() {
        return filterIds;
    }

    public void setFilterIds(Set<Integer> filterIds) {
        this.filterIds = filterIds;
    }

    public List<String> getIngredientNames() {
        return ingredientNames;
    }

    public void setIngredientNames(List<String> ingredientNames) {
        this.ingredientNames = ingredientNames;
    }
}
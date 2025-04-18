package ru.akkuzin.vkr.backendVKR.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Time;
import java.util.List;

public class ReceptCreateDTO {
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;


    private String imageUrl;
    private List<String> cookingSteps;

    @NotNull(message = "Длительность не может быть пустой")
    private Time duration;

    @NotNull(message = "Укажите приватность рецепта")
    private Boolean isPrivate;

    @NotBlank(message = "Email владельца обязателен")
    private String ownerEmail;

    private List<String> ingredientNames;
    private List<Integer> ingredientIds;
    private List<Integer> filterIds;

    private List<@NotBlank String> filterNames;


    public @NotNull(message = "Укажите приватность рецепта") Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(@NotNull(message = "Укажите приватность рецепта") Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public List<String> getFilterNames() {
        return filterNames;
    }

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

    public void setFilterNames(List<String> filterNames) {
        this.filterNames = filterNames;
    }


    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Time getDuration() { return duration; }
    public void setDuration(Time duration) { this.duration = duration; }
    public Boolean getIsPrivate() { return isPrivate; }
    public void setIsPrivate(Boolean isPrivate) { this.isPrivate = isPrivate; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public List<String> getIngredientNames() { return ingredientNames; }
    public void setIngredientNames(List<String> ingredientNames) { this.ingredientNames = ingredientNames; }
    public List<Integer> getIngredientIds() { return ingredientIds; }
    public void setIngredientIds(List<Integer> ingredientIds) { this.ingredientIds = ingredientIds; }
    public List<Integer> getFilterIds() { return filterIds; }
    public void setFilterIds(List<Integer> filterIds) { this.filterIds = filterIds; }
}
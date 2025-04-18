package ru.akkuzin.vkr.backendVKR.dto;

import ru.akkuzin.vkr.backendVKR.model.Recept;

import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

public class ReceptResponseDTO {
    private int id;
    private String name;
    private String description;
    private Time duration;
    private boolean isPrivate;
    private int ownerId;
    private OwnerDTO owner;
    private List<String> ingredientNames;
    private List<IngredientDTO> ingredients;
    private List<FilterDTO> filters;
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

    public ReceptResponseDTO convertToResponseDTO(Recept recept) {
        ReceptResponseDTO dto = new ReceptResponseDTO();
        dto.setId(recept.getId());
        dto.setName(recept.getName());
        dto.setDescription(recept.getDiscription());
        dto.setDuration(recept.getDuration());
        dto.setPrivate(recept.isPrivate());

        // Преобразование владельца
        if (recept.getOwner() != null) {
            OwnerDTO ownerDto = new OwnerDTO();
            ownerDto.setId(recept.getOwner().getId());
            ownerDto.setEmail(recept.getOwner().getEmail());
            ownerDto.setName(recept.getOwner().getName());
            ownerDto.setSecondName(recept.getOwner().getSecondName());
            ownerDto.setPatronymic(recept.getOwner().getPatronymic());
            dto.setOwner(ownerDto);
        }

        // Преобразование ингредиентов
        if (recept.getIngredients() != null) {
            List<IngredientDTO> ingredients = recept.getIngredients().stream()
                    .map(ing -> new IngredientDTO(ing.getId(), ing.getName()))
                    .collect(Collectors.toList());
            dto.setIngredients(ingredients);
        }

        // Преобразование фильтров
        if (recept.getFilters() != null) {
            List<FilterDTO> filters = recept.getFilters().stream()
                    .map(f -> new FilterDTO(f.getId(), f.getNameOfFilter(), f.getTypeOfFilter()))
                    .collect(Collectors.toList());
            dto.setFilters(filters);
        }

        return dto;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Time getDuration() { return duration; }
    public void setDuration(Time duration) { this.duration = duration; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }
    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public OwnerDTO getOwner() {
        return owner;
    }

    public void setOwner(OwnerDTO owner) {
        this.owner = owner;
    }

    public List<String> getIngredientNames() { return ingredientNames; }
    public void setIngredientNames(List<String> ingredientNames) { this.ingredientNames = ingredientNames; }

    public List<IngredientDTO> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDTO> ingredients) {
        this.ingredients = ingredients;
    }

    public List<FilterDTO> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDTO> filters) {
        this.filters = filters;
    }

}
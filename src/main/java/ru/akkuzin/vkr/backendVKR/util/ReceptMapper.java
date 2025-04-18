package ru.akkuzin.vkr.backendVKR.util;

import ru.akkuzin.vkr.backendVKR.dto.FilterDTO;
import ru.akkuzin.vkr.backendVKR.dto.IngredientDTO;
import ru.akkuzin.vkr.backendVKR.dto.OwnerDTO;
import ru.akkuzin.vkr.backendVKR.dto.ReceptResponseDTO;
import ru.akkuzin.vkr.backendVKR.model.Recept;

import java.util.stream.Collectors;

public class ReceptMapper {
    public static ReceptResponseDTO toResponseDTO(Recept recept) {
        ReceptResponseDTO dto = new ReceptResponseDTO();
        dto.setId(recept.getId());
        dto.setName(recept.getName());
        dto.setDescription(recept.getDiscription());
        dto.setDuration(recept.getDuration());
        dto.setPrivate(recept.isPrivate());
        dto.setCookingSteps(recept.getCookingSteps());
        dto.setImageUrl(recept.getImageUrl());
        // Маппинг владельца
        if (recept.getOwner() != null) {
            OwnerDTO ownerDTO = new OwnerDTO();
            ownerDTO.setId(recept.getOwner().getId());
            ownerDTO.setEmail(recept.getOwner().getEmail());
            ownerDTO.setName(recept.getOwner().getName());
            ownerDTO.setSecondName(recept.getOwner().getSecondName());
            ownerDTO.setPatronymic(recept.getOwner().getPatronymic());
            dto.setOwner(ownerDTO);
        }

        if (recept.getIngredients() != null) {
            dto.setIngredients(recept.getIngredients().stream()
                    .map(ingredient -> {
                        IngredientDTO ingredientDTO = new IngredientDTO();
                        ingredientDTO.setId(ingredient.getId());
                        ingredientDTO.setName(ingredient.getName());
                        return ingredientDTO;
                    })
                    .collect(Collectors.toList()));
        }

        // Маппинг фильтров
        if (recept.getFilters() != null) {
            dto.setFilters(recept.getFilters().stream()
                    .map(filter -> {
                        FilterDTO filterDTO = new FilterDTO();
                        filterDTO.setId(filter.getId());
                        filterDTO.setName(filter.getNameOfFilter());
                        filterDTO.setType(filter.getTypeOfFilter());
                        return filterDTO;
                    })
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
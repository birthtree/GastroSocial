package ru.akkuzin.vkr.backendVKR.util;

import ru.akkuzin.vkr.backendVKR.dto.*;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.model.ReceptIngredient;

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
            dto.setOwner(mapOwner(recept.getOwner()));
        }

        // Маппинг ингредиентов с количеством
        if (recept.getReceptIngredients() != null) {
            dto.setIngredients(recept.getReceptIngredients().stream()
                    .map(ReceptMapper::mapIngredient)
                    .collect(Collectors.toList()));
        }

        // Маппинг фильтров
        if (recept.getFilters() != null) {
            dto.setFilters(recept.getFilters().stream()
                    .map(ReceptMapper::mapFilter)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private static OwnerDTO mapOwner(ru.akkuzin.vkr.backendVKR.model.Person owner) {
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setId(owner.getId());
        ownerDTO.setEmail(owner.getEmail());
        ownerDTO.setName(owner.getName());
        ownerDTO.setSecondName(owner.getSecondName());
        ownerDTO.setPatronymic(owner.getPatronymic());
        return ownerDTO;
    }

    private static ReceptIngredientDTO mapIngredient(ReceptIngredient ri) {
        ReceptIngredientDTO dto = new ReceptIngredientDTO();
        dto.setIngredientId(ri.getIngredient().getId());
        dto.setName(ri.getIngredient().getName());
        dto.setQuantity(ri.getQuantity());
        return dto;
    }

    private static FilterDTO mapFilter(ru.akkuzin.vkr.backendVKR.model.Filters filter) {
        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setId(filter.getId());
        filterDTO.setName(filter.getNameOfFilter());
        filterDTO.setType(filter.getTypeOfFilter());
        return filterDTO;
    }
}
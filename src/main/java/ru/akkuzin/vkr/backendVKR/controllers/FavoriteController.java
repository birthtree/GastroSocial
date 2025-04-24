package ru.akkuzin.vkr.backendVKR.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.dto.FilterDTO;
import ru.akkuzin.vkr.backendVKR.dto.OwnerDTO;
import ru.akkuzin.vkr.backendVKR.dto.ReceptIngredientDTO;
import ru.akkuzin.vkr.backendVKR.dto.ReceptResponseDTO;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;
import ru.akkuzin.vkr.backendVKR.services.ReceptService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final ReceptService receptService;
    private final PeopleService peopleService;
    @Autowired
    public FavoriteController(ReceptService receptService, PeopleService peopleService) {
        this.receptService = receptService;
        this.peopleService = peopleService;
    }

    @PostMapping("/{receptId}")
    public ResponseEntity<?> addFavorite(
            @RequestHeader("User-Email") String userEmail,
            @PathVariable int receptId) {
        receptService.addToFavorites(userEmail, receptId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{receptId}")
    public ResponseEntity<?> removeFavorite(
            @RequestHeader("User-Email") String userEmail,
            @PathVariable int receptId) {
        receptService.removeFromFavorites(userEmail, receptId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReceptResponseDTO>> getFavorites(
            @RequestHeader("User-Email") String userEmail) {
        List<ReceptResponseDTO> favorites = receptService.getUserFavorites(userEmail)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/check/{receptId}")
    public ResponseEntity<Boolean> checkFavorite(
            @RequestHeader("User-Email") String userEmail,
            @PathVariable int receptId) {
        return ResponseEntity.ok(receptService.isFavorite(userEmail, receptId));
    }
    private ReceptResponseDTO convertToDto(Recept recept) {
        ReceptResponseDTO dto = new ReceptResponseDTO();
        dto.setId(recept.getId());
        dto.setName(recept.getName());
        dto.setDescription(recept.getDiscription());
        dto.setDuration(recept.getDuration());
        dto.setPrivate(recept.isPrivate());
        dto.setImageUrl(recept.getImageUrl());
        dto.setCookingSteps(recept.getCookingSteps());

        if (recept.getOwner() != null) {
            OwnerDTO ownerDto = new OwnerDTO();
            ownerDto.setId(recept.getOwner().getId());
            ownerDto.setEmail(recept.getOwner().getEmail());
            ownerDto.setName(recept.getOwner().getName());
            ownerDto.setSecondName(recept.getOwner().getSecondName());
            ownerDto.setPatronymic(recept.getOwner().getPatronymic());
            dto.setOwner(ownerDto);
        }

        if (recept.getReceptIngredients() != null) {
            dto.setIngredients(recept.getReceptIngredients().stream()
                    .map(ri -> new ReceptIngredientDTO(
                            ri.getIngredient().getId(),
                            ri.getIngredient().getName(),
                            ri.getQuantity()
                    ))
                    .collect(Collectors.toList()));
        }

        if (recept.getFilters() != null) {
            dto.setFilters(recept.getFilters().stream()
                    .map(f -> new FilterDTO(
                            f.getId(),
                            f.getNameOfFilter(),
                            f.getTypeOfFilter()
                    ))
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
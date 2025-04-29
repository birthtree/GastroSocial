package ru.akkuzin.vkr.backendVKR.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.dto.ReceptResponseDTO;
import ru.akkuzin.vkr.backendVKR.services.ReceptService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/find")
public class SearchController {
    private final ReceptService receptService;

    @Autowired
    public SearchController(ReceptService receptService) {
        this.receptService = receptService;
    }

    // Поиск по имени рецепта (частичное совпадение)
    @GetMapping("/by-name")
    public ResponseEntity<List<ReceptResponseDTO>> findByName(@RequestParam String name) {
        return ResponseEntity.ok(receptService.findByNameContaining(name));
    }

    // Поиск по одному или нескольким ингредиентам
    @GetMapping("/by-ingredients")
    public ResponseEntity<List<ReceptResponseDTO>> findByIngredients(
            @RequestParam Set<String> ingredientNames) {
        return ResponseEntity.ok(receptService.findByIngredientNames(ingredientNames));
    }

    // Поиск по одному или нескольким фильтрам
    @GetMapping("/by-filters")
    public ResponseEntity<List<ReceptResponseDTO>> findByFilters(
            @RequestParam Set<String> filterNames) {
        return ResponseEntity.ok(receptService.findByFilterNames(filterNames));
    }

    // Комбинированный поиск
    @GetMapping("/combined")
    public ResponseEntity<List<ReceptResponseDTO>> combinedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> ingredientNames,
            @RequestParam(required = false) Set<String> filterNames) {
        return ResponseEntity.ok(receptService.combinedSearch(name, ingredientNames, filterNames));
    }

    // Поиск по времени приготовления
    @GetMapping("/by-duration")
    public ResponseEntity<List<ReceptResponseDTO>> findByDuration(
            @RequestParam(required = false) String maxDuration,
            @RequestParam(required = false) String minDuration) {
        return ResponseEntity.ok(receptService.findByDuration(maxDuration, minDuration));
    }

    // Расширенный поиск
    @GetMapping("/advanced")
    public ResponseEntity<List<ReceptResponseDTO>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> ingredientNames,
            @RequestParam(required = false) Set<String> filterNames,
            @RequestParam(required = false) String maxDuration,
            @RequestParam(required = false) String minDuration) {
        return ResponseEntity.ok(receptService.advancedSearch(
                name, ingredientNames, filterNames, maxDuration, minDuration));
    }
}
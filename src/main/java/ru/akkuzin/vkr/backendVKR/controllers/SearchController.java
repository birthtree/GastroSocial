package ru.akkuzin.vkr.backendVKR.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.services.ReceptService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/find")
public class SearchController {
    private final ReceptService receptService;

    @Autowired
    public SearchController(ReceptService receptService) {
        this.receptService = receptService;
    }

    // Поиск по имени рецепта (частичное совпадение)
    @GetMapping("/by-name")
    public List<Recept> findByName(@RequestParam String name) {
        return receptService.findByNameContaining(name);
    }

    // Поиск по одному или нескольким ингредиентам
    @GetMapping("/by-ingredients")
    public List<Recept> findByIngredients(@RequestParam Set<String> ingredientNames) {
        return receptService.findByIngredientNames(ingredientNames);
    }

    // Поиск по одному или нескольким фильтрам
    @GetMapping("/by-filters")
    public List<Recept> findByFilters(@RequestParam Set<String> filterNames) {
        return receptService.findByFilterNames(filterNames);
    }

    // Комбинированный поиск
    @GetMapping("/combined")
    public List<Recept> combinedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> ingredientNames,
            @RequestParam(required = false) Set<String> filterNames) {
        return receptService.combinedSearch(name, ingredientNames, filterNames);
    }
    @GetMapping("/by-duration")
    public List<Recept> findByDuration(
            @RequestParam(required = false) String maxDuration,
            @RequestParam(required = false) String minDuration) {
        return receptService.findByDuration(maxDuration, minDuration);
    }

    @GetMapping("/advanced")
    public List<Recept> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> ingredientNames,
            @RequestParam(required = false) Set<String> filterNames,
            @RequestParam(required = false) String maxDuration,
            @RequestParam(required = false) String minDuration) {
        return receptService.advancedSearch(
                name, ingredientNames, filterNames, maxDuration, minDuration);
    }
}

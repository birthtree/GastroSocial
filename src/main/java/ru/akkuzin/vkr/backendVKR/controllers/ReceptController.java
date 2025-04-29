package ru.akkuzin.vkr.backendVKR.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.dto.*;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;
import ru.akkuzin.vkr.backendVKR.services.ReceptService;
import ru.akkuzin.vkr.backendVKR.util.PersonNotCreatedException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptNotCreatedException;
import ru.akkuzin.vkr.backendVKR.util.ReceptMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recepts")
public class ReceptController {
    private final ReceptService receptService;
    private final PeopleService peopleService;

    @Autowired
    public ReceptController(ReceptService receptService, PeopleService peopleService) {
        this.receptService = receptService;
        this.peopleService = peopleService;
    }

    @GetMapping("/all")
    public List<ReceptResponseDTO> getAllRecepts() {
        return receptService.findAll().stream()
                .map(ReceptMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReceptResponseDTO getRecept(@PathVariable int id) {
        return ReceptMapper.toResponseDTO(receptService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecept(
            @PathVariable int id,
            @RequestHeader("User-Email") String userEmail) {
        try {
            receptService.deleteById(id, userEmail);
            return ResponseEntity.noContent().build();
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ReceptResponseDTO> update(
            @PathVariable int id,
            @RequestBody @Valid ReceptCreateDTO receptDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ReceptNotCreatedException(getErrorMessage(bindingResult));
        }

        Recept updatedRecept = new Recept();
        updatedRecept.setName(receptDTO.getName());
        updatedRecept.setDiscription(receptDTO.getDescription());
        updatedRecept.setDuration(receptDTO.getDuration());
        updatedRecept.setPrivate(receptDTO.getIsPrivate());
        updatedRecept.setImageUrl(receptDTO.getImageUrl());
        updatedRecept.setCookingSteps(receptDTO.getCookingSteps());
        updatedRecept.setIngredientNames(receptDTO.getIngredientNames());
        updatedRecept.setIngredientQuantities(receptDTO.getIngredientQuantities());
        updatedRecept.setFilterNames(receptDTO.getFilterNames());

        // Найти владельца
        Person owner = peopleService.findByEmail(receptDTO.getOwnerEmail());
        updatedRecept.setOwner(owner);

        // Важно: вызвать service.update через твой метод
        Recept savedRecept = receptService.update(id, updatedRecept);

        return ResponseEntity.ok(convertToResponseDTO(savedRecept));
    }

    @PostMapping
    public ResponseEntity<ReceptResponseDTO> create(
            @RequestBody @Valid ReceptCreateDTO receptDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ReceptNotCreatedException(getErrorMessage(bindingResult));
        }

        // Создаем объект Recept из DTO
        Recept recept = new Recept();
        recept.setName(receptDTO.getName());
        recept.setDiscription(receptDTO.getDescription());
        recept.setDuration(receptDTO.getDuration());
        recept.setPrivate(receptDTO.getIsPrivate());
        recept.setImageUrl(receptDTO.getImageUrl());
        recept.setCookingSteps(receptDTO.getCookingSteps());

        // Устанавливаем списки для обработки в сервисе
        recept.setIngredientNames(receptDTO.getIngredientNames());
        recept.setIngredientQuantities(receptDTO.getIngredientQuantities());
        recept.setFilterNames(receptDTO.getFilterNames());

        // Устанавливаем владельца
        Person owner = peopleService.findByEmail(receptDTO.getOwnerEmail());
        recept.setOwner(owner);

        // Сохраняем через метод save()
        Recept savedRecept = receptService.save(recept);

        // Конвертируем в DTO для ответа
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToResponseDTO(savedRecept));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReceptResponseDTO>> getMyRecipes(
            @RequestHeader("User-Email") String userEmail) {

        // Получаем пользователя по email
        Person owner = peopleService.findByEmail(userEmail);

        // Получаем все рецепты этого пользователя
        List<Recept> myRecipes = receptService.findByOwner(owner);

        // Конвертируем в DTO
        List<ReceptResponseDTO> response = myRecipes.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }


    private ReceptResponseDTO convertToResponseDTO(Recept recept) {
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

    private String getErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }
}
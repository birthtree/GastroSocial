package ru.akkuzin.vkr.backendVKR.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.dto.ReceptCreateDTO;
import ru.akkuzin.vkr.backendVKR.dto.ReceptResponseDTO;
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
@RequestMapping("/recepts")
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
    public ResponseEntity<HttpStatus> deleteRecept(@PathVariable int id) {
        receptService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceptResponseDTO> update(
            @PathVariable int id,
            @RequestBody @Valid ReceptCreateDTO receptDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ReceptNotCreatedException(getErrorMessage(bindingResult));
        }

        // Создаем объект Recept из DTO (без ингредиентов и фильтров)
        Recept recept = new Recept();
        recept.setName(receptDTO.getName());
        recept.setDiscription(receptDTO.getDescription());
        recept.setDuration(receptDTO.getDuration());
        recept.setPrivate(receptDTO.getIsPrivate());

        // Устанавливаем владельца
        Person owner = peopleService.findByEmail(receptDTO.getOwnerEmail());
        recept.setOwner(owner);

        // Вызываем service с передачей списков названий
        Recept updatedRecept = receptService.update(
                id,
                recept,
                receptDTO.getIngredientNames(),  // List<String>
                receptDTO.getFilterNames()       // List<String>
        );

        return ResponseEntity.ok(ReceptMapper.toResponseDTO(updatedRecept));
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
        recept.setIngredientNames(receptDTO.getIngredientNames());
        recept.setFilterNames(receptDTO.getFilterNames());

        // Устанавливаем владельца
        Person owner = peopleService.findByEmail(receptDTO.getOwnerEmail());
        recept.setOwner(owner);

        // Сохраняем и получаем сохраненную сущность
        Recept savedRecept = receptService.save(recept);

        // Преобразуем в DTO и возвращаем
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReceptMapper.toResponseDTO(savedRecept));
    }

    private String getErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + " - " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
    }
}
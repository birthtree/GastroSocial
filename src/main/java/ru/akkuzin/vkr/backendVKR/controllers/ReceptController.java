package ru.akkuzin.vkr.backendVKR.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;
import ru.akkuzin.vkr.backendVKR.services.ReceptService;
import ru.akkuzin.vkr.backendVKR.util.PersonNotCreatedException;
import ru.akkuzin.vkr.backendVKR.util.Recept.ReceptNotCreatedException;

import java.util.List;

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
    public List<Recept> getPeople() {
        return receptService.findAll();
    }

    @GetMapping("/{id}")
    public Recept getRecept(@PathVariable int id) {
        return receptService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteRecept(@PathVariable int id) {
        receptService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") int id, @RequestBody @Valid Recept updatedRecept, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new ReceptNotCreatedException(errorMsg.toString());
        }
        receptService.update(id, updatedRecept);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create( @RequestBody @Valid Recept recept, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new ReceptNotCreatedException(errorMsg.toString());
        }
        Person owner = peopleService.findOne(recept.getOwner().getId());  // Находим владельца по ID
        recept.setOwner(owner);  // Устанавливаем владельца рецепта
        receptService.save(recept);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}

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
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") int id,
                                             @RequestBody @Valid Recept updatedRecept,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ReceptNotCreatedException(getErrorMessage(bindingResult));
        }

        // Обработка владельца
        if (updatedRecept.getOwner() != null && updatedRecept.getOwner().getEmail() != null) {
            Person owner = peopleService.findByEmail(updatedRecept.getOwner().getEmail());
            updatedRecept.setOwner(owner);
        }

        receptService.update(id, updatedRecept);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Recept recept,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ReceptNotCreatedException(getErrorMessage(bindingResult));
        }

        // Получаем email из объекта owner
        if (recept.getOwner() == null || recept.getOwner().getEmail() == null) {
            throw new PersonNotCreatedException("Owner email must be specified");
        }

        Person owner = peopleService.findByEmail(recept.getOwner().getEmail());


        recept.setOwner(owner);
        receptService.save(recept);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private String getErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors) {
            errorMsg.append(error.getField())
                    .append(" - ").append(error.getDefaultMessage())
                    .append("; ");
        }
        return errorMsg.toString();
    }
}
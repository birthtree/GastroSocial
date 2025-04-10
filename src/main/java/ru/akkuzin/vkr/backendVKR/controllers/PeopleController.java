package ru.akkuzin.vkr.backendVKR.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;
import ru.akkuzin.vkr.backendVKR.util.PersonNotCreatedException;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/user")
public class PeopleController {
    private final PeopleService peopleService;


    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping("/all")
    public List<Person> getPeople() {
        return peopleService.findAll();
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable("id") int id) {
        return peopleService.findOne(id);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Person person,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for(FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            throw new PersonNotCreatedException(errorMsg.toString());
        }
        peopleService.save(person);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@PathVariable("id") int id,
                                             @RequestBody @Valid Person updatedPerson,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append("; ");
            }
            throw new PersonNotCreatedException(errorMsg.toString());
        }

        peopleService.update(id, updatedPerson);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    @GetMapping("/email")
    public ResponseEntity<Person> getByEmail(@RequestParam("email") String email) {
        Person person = peopleService.findByEmail(email);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }
}

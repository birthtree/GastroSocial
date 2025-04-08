package ru.akkuzin.vkr.backendVKR.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;

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
}

package ru.akkuzin.vkr.backendVKR.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.dto.ProfileDTO;
import ru.akkuzin.vkr.backendVKR.dto.ReceptResponseDTO;
import ru.akkuzin.vkr.backendVKR.dto.UserDTO;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;
import ru.akkuzin.vkr.backendVKR.services.ReceptService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ReceptService receptService;
    private final PeopleService peopleService;

    @Autowired
    public AdminController(ReceptService receptService, PeopleService peopleService) {
        this.receptService = receptService;
        this.peopleService = peopleService;
    }

    // Получить список всех пользователей
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = peopleService.findAll().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    // Получить информацию о конкретном пользователе по email
    @GetMapping("/users/by-email")
    public ResponseEntity<UserDTO> getUserByEmail(@RequestParam String email) {
        Person person = peopleService.findByEmail(email);
        return ResponseEntity.ok(convertToUserDTO(person));
    }

    // Удалить пользователя по email
    @DeleteMapping("/users/by-email")
    public ResponseEntity<?> deleteUserByEmail(@RequestParam String email) {
        peopleService.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    // Заблокировать пользователя по email
    @PostMapping("/users/block-by-email")
    public ResponseEntity<?> blockUserByEmail(@RequestParam String email) {
        peopleService.blockUserByEmail(email);
        return ResponseEntity.ok().build();
    }

    // Разблокировать пользователя по email
    @PostMapping("/users/unblock-by-email")
    public ResponseEntity<?> unblockUserByEmail(@RequestParam String email) {
        peopleService.unblockUserByEmail(email);
        return ResponseEntity.ok().build();
    }

    // Изменить роль пользователя по email
    @PutMapping("/users/change-role")
    public ResponseEntity<?> changeUserRoleByEmail(
            @RequestParam String email,
            @RequestParam String newRole) {
        peopleService.changeUserRoleByEmail(email, newRole);
        return ResponseEntity.ok().build();
    }

    // Поиск рецептов для администратора
    @GetMapping("/recepts/search")
    public ResponseEntity<List<ReceptResponseDTO>> searchRecepts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Set<String> ingredientNames,
            @RequestParam(required = false) Set<String> filterNames,
            @RequestParam(required = false) String maxDuration,
            @RequestParam(required = false) String minDuration) {

        List<ReceptResponseDTO> results = receptService.advancedSearch(
                name, ingredientNames, filterNames, maxDuration, minDuration);

        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/recepts/{id}")
    public ResponseEntity<?> deleteRecept(
            @PathVariable int id,
            @RequestHeader("User-Email") String userEmail) {

        receptService.deleteById(id, userEmail);
        return ResponseEntity.noContent().build();
    }

    private UserDTO convertToUserDTO(Person person) {
        UserDTO dto = new UserDTO();
        dto.setId(person.getId());
        dto.setEmail(person.getEmail());
        dto.setName(person.getName());
        dto.setSecondName(person.getSecondName());
        dto.setPatronymic(person.getPatronymic());
        dto.setRole(person.getRole());
        dto.setActive(person.isActive());
        return dto;
    }

    @GetMapping("/users/profile/{id}")
    public ResponseEntity<ProfileDTO> getUserProfile(@PathVariable int id) {
        ProfileDTO profile = peopleService.getProfileInfo(id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/users/profile/by-email")
    public ResponseEntity<ProfileDTO> getUserProfileByEmail(@RequestParam String email) {
        ProfileDTO profile = peopleService.getProfileInfoByEmail(email);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/users/profile/{id}")
    public ResponseEntity<?> updateUserProfile(
            @PathVariable int id,
            @RequestBody ProfileDTO profileDTO) {
        peopleService.updateProfileInfo(id, profileDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/profile/by-email")
    public ResponseEntity<?> updateUserProfileByEmail(
            @RequestParam String email,
            @RequestBody ProfileDTO profileDTO) {
        peopleService.updateProfileInfoByEmail(email, profileDTO);
        return ResponseEntity.ok().build();
    }


}
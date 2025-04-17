package ru.akkuzin.vkr.backendVKR.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.akkuzin.vkr.backendVKR.dto.AuthDTO;
import ru.akkuzin.vkr.backendVKR.dto.AuthResponseDTO;
import ru.akkuzin.vkr.backendVKR.dto.RegisterDTO;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.util.JWTUtil;
import ru.akkuzin.vkr.backendVKR.security.PersonDetailService;
import ru.akkuzin.vkr.backendVKR.services.RegistrationService;
import ru.akkuzin.vkr.backendVKR.util.PersonErrorResponse;
import ru.akkuzin.vkr.backendVKR.util.PersonNotCreatedException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PersonDetailService personDetailService;

    @Autowired
    public AuthController(RegistrationService registrationService,
                          JWTUtil jwtUtil,
                          AuthenticationManager authenticationManager,
                          PersonDetailService personDetailService) {
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.personDetailService = personDetailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            Person person = new Person();
            person.setEmail(registerDTO.getEmail());
            person.setPassword(registerDTO.getPassword());
            person.setName(registerDTO.getName());
            person.setSecondName(registerDTO.getSecondName());
            person.setPatronymic(registerDTO.getPatronymic());
            person.setRole("ROLE_USER"); // Устанавливаем роль по умолчанию

            registrationService.register(person);

            return ResponseEntity.ok("Пользователь успешно зарегистрирован");
        } catch (PersonNotCreatedException e) {
            return ResponseEntity.badRequest().body(new PersonErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDTO authDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDTO.getEmail(),
                            authDTO.getPassword()
                    )
            );

            UserDetails userDetails = personDetailService.loadUserByUsername(authDTO.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponseDTO(
                    authDTO.getEmail(),
                    token,
                    userDetails.getAuthorities().iterator().next().getAuthority()
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new PersonErrorResponse("Неправильный email или пароль"));
        }
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        return new ResponseEntity<>(
                new PersonErrorResponse(e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
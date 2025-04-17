package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;

@Service
public class RegistrationService {
    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository,
                               PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Person register(Person person) {
        // Проверяем, не занят ли email
        if (peopleRepository.existsByEmail(person.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }

        // Устанавливаем роль по умолчанию (если не указана)
        if (person.getRole() == null || person.getRole().isEmpty()) {
            person.setRole("ROLE_USER");
        }

        // Хешируем пароль
        String encodedPassword = passwordEncoder.encode(person.getPassword());
        person.setPassword(encodedPassword);

        // Сохраняем пользователя
        return peopleRepository.save(person);
    }
}
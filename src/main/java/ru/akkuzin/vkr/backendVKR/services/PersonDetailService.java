package ru.akkuzin.vkr.backendVKR.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;

@Service
public class PersonDetailService implements UserDetailsService {
    private final PeopleService peopleService;

    @Autowired
    public PersonDetailService(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Ищем пользователя по email (в вашем случае email = username)
        Person person = peopleService.findByEmail(email);

        // Если пользователь не найден — выбрасываем исключение
        if (person == null) {
            throw new UsernameNotFoundException("Пользователь с email " + email + " не найден");
        }
        if (!person.isActive()) {
            throw new DisabledException("User is blocked");
        }
        // Возвращаем UserDetails (реализацию PersonDetails)
        return new PersonDetails(person);
    }
}
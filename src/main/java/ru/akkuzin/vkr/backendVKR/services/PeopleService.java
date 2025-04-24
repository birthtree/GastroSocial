package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.repositories.PeopleRepository;
import ru.akkuzin.vkr.backendVKR.util.PersonNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        return peopleRepository.findById(id)
                .orElseThrow(PersonNotFoundException::new);
    }

    public Person findByEmail(String email) {
        return peopleRepository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException());
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person updatedPerson) {
        Person personFromDb = findOne(id);
        personFromDb.setName(updatedPerson.getName());
        personFromDb.setSecondName(updatedPerson.getSecondName());
        personFromDb.setPatronymic(updatedPerson.getPatronymic());
        personFromDb.setEmail(updatedPerson.getEmail());
        personFromDb.setPassword(updatedPerson.getPassword());
        personFromDb.setRole(updatedPerson.getRole());
        peopleRepository.save(personFromDb);
    }

    @Transactional
    public void updateByEmail(String email, Person updatedPerson) {
        Person personFromDb = findByEmail(email);
        personFromDb.setName(updatedPerson.getName());
        personFromDb.setSecondName(updatedPerson.getSecondName());
        personFromDb.setPatronymic(updatedPerson.getPatronymic());
        personFromDb.setPassword(updatedPerson.getPassword());
        personFromDb.setRole(updatedPerson.getRole());
        peopleRepository.save(personFromDb);
    }

    public void checkIfUserActive(String email) {
        Person person = findByEmail(email);
        if (!person.isActive()) {
            throw new DisabledException("User is blocked");
        }
    }

    @Transactional
    public void blockUser(int id) {
        Person person = findOne(id);
        person.setActive(false);
        peopleRepository.save(person);
    }

    @Transactional
    public void blockUserByEmail(String email) {
        Person person = findByEmail(email);
        person.setActive(false);
        peopleRepository.save(person);
    }

    @Transactional
    public void unblockUser(int id) {
        Person person = findOne(id);
        person.setActive(true);
        peopleRepository.save(person);
    }

    @Transactional
    public void unblockUserByEmail(String email) {
        Person person = findByEmail(email);
        person.setActive(true);
        peopleRepository.save(person);
    }

    @Transactional
    public void deleteById(int id) {
        if (!peopleRepository.existsById(id)) {
            throw new PersonNotFoundException();
        }
        peopleRepository.deleteById(id);
    }

    @Transactional
    public void deleteByEmail(String email) {
        Person person = findByEmail(email);
        peopleRepository.deleteById(person.getId());
    }

    @Transactional
    public void changeUserRole(int id, String newRole) {
        Person person = findOne(id);
        person.setRole(newRole);
        peopleRepository.save(person);
    }

    @Transactional
    public void changeUserRoleByEmail(String email, String newRole) {
        Person person = findByEmail(email);
        person.setRole(newRole);
        peopleRepository.save(person);
    }
}
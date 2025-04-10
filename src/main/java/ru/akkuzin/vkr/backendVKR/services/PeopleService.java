package ru.akkuzin.vkr.backendVKR.services;

import org.springframework.beans.factory.annotation.Autowired;
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
        Optional<Person> person = peopleRepository.findById(id);
        return person.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(Person person) {
       // enrichPerson(person);
        peopleRepository.save(person);
    }
    @Transactional
    public void update(int id, Person updatedPerson) {
        Person personFromDb = peopleRepository.findById(id)
                    .orElseThrow(() -> new PersonNotFoundException());
        personFromDb.setName(updatedPerson.getName());
        personFromDb.setSecondName(updatedPerson.getSecondName());
        personFromDb.setPatronymic(updatedPerson.getPatronymic());
        personFromDb.setEmail(updatedPerson.getEmail());
        personFromDb.setPassword(updatedPerson.getPassword());
        personFromDb.setAdmin(updatedPerson.isAdmin());

        peopleRepository.save(personFromDb);
    }

    public Person findByEmail(String email) {
        return peopleRepository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException());
    }
    @Transactional
    public void deleteById(int id) {
        if (!peopleRepository.existsById(id)) {
            throw new PersonNotFoundException();
        }
        peopleRepository.deleteById(id);
    }

}

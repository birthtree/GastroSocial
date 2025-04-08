package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akkuzin.vkr.backendVKR.model.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

}

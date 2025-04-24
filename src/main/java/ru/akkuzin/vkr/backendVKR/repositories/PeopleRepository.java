package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    Optional<Person> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT p FROM Person p JOIN p.favoriteRecepts r WHERE r.id = :receptId")
    List<Person> findUsersWithFavoriteRecipe(@Param("receptId") int receptId);
}

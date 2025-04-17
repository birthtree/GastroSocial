package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.akkuzin.vkr.backendVKR.model.Filters;

import java.util.Optional;

@Repository
public interface FilterRepository extends JpaRepository<Filters, Integer> {

    @Query("SELECT MAX(f.typeOfFilter) FROM Filters f")
    Integer findMaxTypeOfFilter();
    Optional<Filters> findByNameOfFilterIgnoreCase(String name);
}

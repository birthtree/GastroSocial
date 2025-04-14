package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akkuzin.vkr.backendVKR.model.Filters;

@Repository
public interface FilterRepository extends JpaRepository<Filters, Integer> {

}

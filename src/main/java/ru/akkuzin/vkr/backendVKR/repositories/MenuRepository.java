package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.akkuzin.vkr.backendVKR.model.Menu;
@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
}

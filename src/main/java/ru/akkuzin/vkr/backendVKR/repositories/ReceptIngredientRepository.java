package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.akkuzin.vkr.backendVKR.model.ReceptIngredient;
import ru.akkuzin.vkr.backendVKR.model.ReceptIngredientId;

public interface ReceptIngredientRepository extends JpaRepository<ReceptIngredient, ReceptIngredientId> {


    @Modifying
    @Query("DELETE FROM ReceptIngredient ri WHERE ri.recept.id = :receptId")
    void deleteByReceptId(@Param("receptId") int receptId);


    @Modifying
    @Query("DELETE FROM ReceptIngredient ri WHERE ri.recept.id = :receptId")
    void deleteAllByReceptId(@Param("receptId") int receptId);

}
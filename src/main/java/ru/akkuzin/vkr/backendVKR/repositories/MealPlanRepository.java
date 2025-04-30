package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.akkuzin.vkr.backendVKR.model.Mealplan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealPlanRepository extends JpaRepository<Mealplan, Integer> {
    List<Mealplan> findByDateAndPersonId(LocalDate date, int personId);
    List<Mealplan> findByDateBetweenAndPersonId(LocalDate startDate, LocalDate endDate, int personId);
    Optional<Mealplan> findByDateAndMealTypeAndPersonId(
            LocalDate date,
            String mealType,
            int personId
    );
    @Query("SELECT mp FROM Mealplan mp JOIN FETCH mp.recept JOIN FETCH mp.person WHERE mp.id = :id")
    Optional<Mealplan> findByIdWithAssociations(@Param("id") Integer id);
}

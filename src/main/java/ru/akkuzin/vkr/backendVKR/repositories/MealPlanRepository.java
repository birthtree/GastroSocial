package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.akkuzin.vkr.backendVKR.model.Mealplan;

import java.time.LocalDate;
import java.util.List;

public interface MealPlanRepository extends JpaRepository<Mealplan, Long> {
    List<Mealplan> findByDateAndPersonId(LocalDate date, Long personId);
    List<Mealplan> findByDateBetweenAndPersonId(LocalDate startDate, LocalDate endDate, Long personId);
}

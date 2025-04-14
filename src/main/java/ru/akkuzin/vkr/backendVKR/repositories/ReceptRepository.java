package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.util.List;
import java.util.Set;

@Repository
public interface ReceptRepository extends JpaRepository<Recept, Integer>, JpaSpecificationExecutor<Recept> {
    List<Recept> findByNameContainingIgnoreCase(String name);

    @Query("SELECT DISTINCT r FROM Recept r JOIN r.ingredients i WHERE i.name IN :ingredientNames")
    List<Recept> findByIngredientsNameIn(@Param("ingredientNames") Set<String> ingredientNames);

    @Query("SELECT DISTINCT r FROM Recept r JOIN r.filters f WHERE f.nameOfFilter IN :filterNames")
    List<Recept> findByFiltersNameOfFilterIn(@Param("filterNames") Set<String> filterNames);

    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND EXISTS (SELECT 1 FROM r.ingredients i WHERE i.name IN :ingredientNames) " +
            "AND EXISTS (SELECT 1 FROM r.filters f WHERE f.nameOfFilter IN :filterNames)")
    List<Recept> findByNameContainingIgnoreCaseAndIngredientsNameInAndFiltersNameOfFilterIn(
            @Param("name") String name,
            @Param("ingredientNames") Set<String> ingredientNames,
            @Param("filterNames") Set<String> filterNames);

    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND EXISTS (SELECT 1 FROM r.ingredients i WHERE i.name IN :ingredientNames)")
    List<Recept> findByNameContainingIgnoreCaseAndIngredientsNameIn(
            @Param("name") String name,
            @Param("ingredientNames") Set<String> ingredientNames);

    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND EXISTS (SELECT 1 FROM r.filters f WHERE f.nameOfFilter IN :filterNames)")
    List<Recept> findByNameContainingIgnoreCaseAndFiltersNameOfFilterIn(
            @Param("name") String name,
            @Param("filterNames") Set<String> filterNames);

    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE EXISTS (SELECT 1 FROM r.ingredients i WHERE i.name IN :ingredientNames) " +
            "AND EXISTS (SELECT 1 FROM r.filters f WHERE f.nameOfFilter IN :filterNames)")
    List<Recept> findByIngredientsNameInAndFiltersNameOfFilterIn(
            @Param("ingredientNames") Set<String> ingredientNames,
            @Param("filterNames") Set<String> filterNames);

    List<Recept> findByDurationBetween(Time time, Time time1);

    List<Recept> findByDurationLessThanEqual(Time time);

    List<Recept> findByDurationGreaterThanEqual(Time time);
}

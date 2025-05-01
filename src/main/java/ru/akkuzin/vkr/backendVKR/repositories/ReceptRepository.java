package ru.akkuzin.vkr.backendVKR.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.akkuzin.vkr.backendVKR.model.Person;
import ru.akkuzin.vkr.backendVKR.model.Recept;
import org.springframework.data.repository.query.Param;

import java.sql.Time;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReceptRepository extends JpaRepository<Recept, Integer>, JpaSpecificationExecutor<Recept> {

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    Optional<Recept> findFullById(Integer id);

    List<Recept> findByOwner(Person owner);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    List<Recept> findByNameContainingIgnoreCase(String name);


    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    @Query("SELECT DISTINCT r FROM Recept r JOIN r.receptIngredients ri JOIN ri.ingredient i WHERE i.name IN :ingredientNames")
    List<Recept> findByIngredientsNameIn(@Param("ingredientNames") Set<String> ingredientNames);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    @Query("SELECT DISTINCT r FROM Recept r JOIN r.filters f WHERE f.nameOfFilter IN :filterNames")
    List<Recept> findByFiltersNameOfFilterIn(@Param("filterNames") Set<String> filterNames);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND EXISTS (SELECT 1 FROM r.receptIngredients ri JOIN ri.ingredient i WHERE i.name IN :ingredientNames) " +
            "AND EXISTS (SELECT 1 FROM r.filters f WHERE f.nameOfFilter IN :filterNames)")
    List<Recept> findByNameContainingIgnoreCaseAndIngredientsNameInAndFiltersNameOfFilterIn(
            @Param("name") String name,
            @Param("ingredientNames") Set<String> ingredientNames,
            @Param("filterNames") Set<String> filterNames);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND EXISTS (SELECT 1 FROM r.receptIngredients ri JOIN ri.ingredient i WHERE i.name IN :ingredientNames)")
    List<Recept> findByNameContainingIgnoreCaseAndIngredientsNameIn(
            @Param("name") String name,
            @Param("ingredientNames") Set<String> ingredientNames);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "AND EXISTS (SELECT 1 FROM r.filters f WHERE f.nameOfFilter IN :filterNames)")
    List<Recept> findByNameContainingIgnoreCaseAndFiltersNameOfFilterIn(
            @Param("name") String name,
            @Param("filterNames") Set<String> filterNames);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    @Query("SELECT DISTINCT r FROM Recept r " +
            "WHERE EXISTS (SELECT 1 FROM r.receptIngredients ri JOIN ri.ingredient i WHERE i.name IN :ingredientNames) " +
            "AND EXISTS (SELECT 1 FROM r.filters f WHERE f.nameOfFilter IN :filterNames)")
    List<Recept> findByIngredientsNameInAndFiltersNameOfFilterIn(
            @Param("ingredientNames") Set<String> ingredientNames,
            @Param("filterNames") Set<String> filterNames);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    List<Recept> findByDurationBetween(Time min, Time max);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    List<Recept> findByDurationLessThanEqual(Time max);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    List<Recept> findByDurationGreaterThanEqual(Time min);

    @EntityGraph(attributePaths = {"receptIngredients.ingredient", "filters", "owner"})
    @Override
    List<Recept> findAll();



}

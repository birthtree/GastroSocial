package ru.akkuzin.vkr.backendVKR.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Recept")
public class Recept {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "discription")
    @NotNull
    private String discription;

    @OneToMany(mappedBy = "recept", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mealplan> mealPlans;

    public List<Mealplan> getMealPlans() {
        return mealPlans;
    }

    public void setMealPlans(List<Mealplan> mealPlans) {
        this.mealPlans = mealPlans;
    }

    @Column(name = "duration")
    @NotNull
    private Time duration;

    @Column(name = "isprivate")
    @NotNull
    private boolean isPrivate;

    @ManyToOne
    @JoinColumn(name="person_id", referencedColumnName = "id")//Соотносим REFERENCES поле из таблицы с тем на какое оно указывает из другой таблцы
    @JsonBackReference
    private Person owner;

    @Transient
    private List<String> ingredientNames;

    @Transient
    private List<String> filterNames;


    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "cooking_steps", columnDefinition = "TEXT")
    private String cookingStepsJson;

    @Transient
    private List<String> cookingSteps;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCookingStepsJson() {
        return cookingStepsJson;
    }

    public void setCookingStepsJson(String cookingStepsJson) {
        this.cookingStepsJson = cookingStepsJson;
    }

    public List<String> getCookingSteps() {
        if (cookingSteps == null && cookingStepsJson != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                cookingSteps = mapper.readValue(cookingStepsJson,
                        new TypeReference<List<String>>(){});
            } catch (Exception e) {
                cookingSteps = Collections.emptyList();
            }
        }
        return cookingSteps;
    }

    public void setCookingSteps(List<String> cookingSteps) {
        this.cookingSteps = cookingSteps;
        try {
            this.cookingStepsJson = new ObjectMapper().writeValueAsString(cookingSteps);
        } catch (Exception e) {
            this.cookingStepsJson = "[]";
        }
    }


    public List<String> getFilterNames() {
        return filterNames;
    }

    public void setFilterNames(List<String> filterNames) {
        this.filterNames = filterNames;
    }

    public List<String> getIngredientNames() {
        return ingredientNames;
    }

    public void setIngredientNames(List<String> ingredientNames) {
        this.ingredientNames = ingredientNames;
    }

    @ManyToMany
    @JoinTable(
            name = "Recept_ingredients",
            joinColumns = @JoinColumn(name = "recept_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @JsonBackReference
    private Set<Ingredient> ingredients;

    @ManyToMany
    @JoinTable(
            name = "Recept_filters",
            joinColumns = @JoinColumn(name = "recept_id"),
            inverseJoinColumns = @JoinColumn(name = "filter_id")
    )
    private Set<Filters> filters;


     @ManyToMany
     @JoinTable(name = "menu_recepts", joinColumns = @JoinColumn(name="recept_id"), inverseJoinColumns = @JoinColumn(name="menu_id"))
     @JsonBackReference
     private Set<Menu> menus;


    public Set<Menu> getMenus() {
        return menus;
    }

    public void setMenus(Set<Menu> menus) {
        this.menus = menus;
    }

    public Set<Filters> getFilters() {
        return filters;
    }

    public void setFilters(Set<Filters> filters) {
        this.filters = filters;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Recept() {
    }

    public Recept(String name, String discription, Time duration, boolean isPrivate, Person owner) {
        this.name = name;
        this.discription = discription;
        this.duration = duration;
        this.isPrivate = isPrivate;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public @NotNull String getDiscription() {
        return discription;
    }

    public void setDiscription(@NotNull String discription) {
        this.discription = discription;
    }

    public @NotNull Time getDuration() {
        return duration;
    }

    public void setDuration(@NotNull Time duration) {
        this.duration = duration;
    }

    @NotNull
    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(@NotNull boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}

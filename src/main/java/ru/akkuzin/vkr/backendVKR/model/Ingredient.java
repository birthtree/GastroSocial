package ru.akkuzin.vkr.backendVKR.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    public Ingredient(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Column(name="name")
    private String name;

    @OneToMany(mappedBy = "ingredient")
    private Set<ReceptIngredient> receptIngredients = new HashSet<>();

    /* @ManyToMany(mappedBy = "ingredients")
    @JsonBackReference
    private Set<Recept> recepts;*/

    public Set<ReceptIngredient> getReceptIngredients() {
        return receptIngredients;
    }

    public void setReceptIngredients(Set<ReceptIngredient> receptIngredients) {
        this.receptIngredients = receptIngredients;
    }

    public Ingredient() {
    }

    public Ingredient(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

 /*   public Set<Recept> getRecepts() {
        return recepts;
    }

    public void setRecepts(Set<Recept> recepts) {
        this.recepts = recepts;
    }*/


}

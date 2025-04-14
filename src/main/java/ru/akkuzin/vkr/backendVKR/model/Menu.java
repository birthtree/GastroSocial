package ru.akkuzin.vkr.backendVKR.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "Menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name="person_id", referencedColumnName = "id")//Соотносим REFERENCES поле из таблицы с тем на какое оно указывает из другой таблцы
    @JsonBackReference
    private Person owner;


    @ManyToMany(mappedBy = "menus")
    @JsonBackReference
    private Set<Recept> recepts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public Set<Recept> getRecepts() {
        return recepts;
    }

    public void setRecepts(Set<Recept> recepts) {
        this.recepts = recepts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Menu() {
    }

    public Menu(String name) {
        this.name = name;
    }

    public Menu(String name, Person owner) {
        this.name = name;
        this.owner = owner;
    }
}

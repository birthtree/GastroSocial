package ru.akkuzin.vkr.backendVKR.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Time;

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
}

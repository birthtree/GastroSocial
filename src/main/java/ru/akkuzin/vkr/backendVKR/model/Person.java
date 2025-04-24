package ru.akkuzin.vkr.backendVKR.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="Person")
public class Person {
    @Id
    @Column(name ="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int id;


    @ManyToMany
    @JoinTable(
            name = "Person_favorites_recepts",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "recept_id")
    )
    private Set<Recept> favoriteRecepts = new HashSet<>();

    public Set<Recept> getFavoriteRecepts() {
        return favoriteRecepts;
    }

    public void setFavoriteRecepts(Set<Recept> favoriteRecepts) {
        this.favoriteRecepts = favoriteRecepts;
    }

    @Column(name ="email")
    @NotNull
    private String email;

    @Column(name ="password")
    @NotNull
    private String password;

    @Column(name ="name")
    @Size(min = 2, max = 30, message = "Name shoud be 2 and 30 char")
    private String name;

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    @Column(name ="second_name")
    private String secondName;

    @Column(name ="patronymic")
    private String patronymic;

    @Column(name="role")
    private String role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)//Указываем что связь один ко многим с полем owner из класса Item
    @JsonBackReference
    private List<Recept> recepts;//Т.к. у человека может быть не один предмет а много создаём список, чтобы хранить в нём все предметы человека

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mealplan> mealPlans;

    public List<Mealplan> getMealPlans() {
        return mealPlans;
    }

    public void setMealPlans(List<Mealplan> mealPlans) {
        this.mealPlans = mealPlans;
    }

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)//Указываем что связь один ко многим с полем owner из класса Item
      @JsonBackReference
     private List<Menu> menus;//Т.к. у человека может быть не один предмет а много создаём список, чтобы хранить в нём все предметы человека

    public List<Menu> getMenus() {
        return menus;
    }

    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

    public List<Recept> getRecepts() {
        return recepts;
    }

    public void setRecepts(List<Recept> recepts) {
        this.recepts = recepts;
    }

    public Person() {
    }

    public Person(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public @NotNull String getEmail() {
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    public @Size(min = 2, max = 30, message = "Name shoud be 2 and 30 char") String getName() {
        return name;
    }

    public void setName(@Size(min = 2, max = 30, message = "Name shoud be 2 and 30 char") String name) {
        this.name = name;
    }


    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }
}

package ru.akkuzin.vkr.backendVKR.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name="Person")
public class Person {
    @Id
    @Column(name ="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    @Column(name ="isadmin")
    private boolean isAdmin;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)//Указываем что связь один ко многим с полем owner из класса Item
    @JsonBackReference
    private List<Recept> recepts;//Т.к. у человека может быть не один предмет а много создаём список, чтобы хранить в нём все предметы человека

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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public @NotNull String getPassword() {
        return password;
    }

    public void setPassword(@NotNull String password) {
        this.password = password;
    }
}

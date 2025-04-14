package ru.akkuzin.vkr.backendVKR.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name="filters")
public class Filters {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "type_of_filter")
    private int typeOfFilter;

    @Column(name = "name_of_filter")
    private String nameOfFilter;

    @Column(name = "filterscol")
    private String filterscol;

    @ManyToMany(mappedBy = "filters")
    @JsonBackReference
    private Set<Recept> recepts;

    public Filters() {
    }

    public Filters(int typeOfFilter, String nameOfFilter) {
        this.typeOfFilter = typeOfFilter;
        this.nameOfFilter = nameOfFilter;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTypeOfFilter() {
        return typeOfFilter;
    }

    public void setTypeOfFilter(int typeOfFilter) {
        this.typeOfFilter = typeOfFilter;
    }

    public String getNameOfFilter() {
        return nameOfFilter;
    }

    public void setNameOfFilter(String nameOfFilter) {
        this.nameOfFilter = nameOfFilter;
    }

    public String getFilterscol() {
        return filterscol;
    }

    public void setFilterscol(String filterscol) {
        this.filterscol = filterscol;
    }

    public Set<Recept> getRecepts() {
        return recepts;
    }

    public void setRecepts(Set<Recept> recepts) {
        this.recepts = recepts;
    }
}

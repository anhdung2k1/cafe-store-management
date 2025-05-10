package com.store.mycoffeestore.model;

public class CategoryModel {

    private String title;
    private int id;

    // Default constructor
    public CategoryModel() {
        this.title = "";
        this.id = 0;
    }

    // Parameterized constructor
    public CategoryModel(String title, int id) {
        this.title = title;
        this.id = id;
    }

    // Getter and Setter for title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Getter and Setter for id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

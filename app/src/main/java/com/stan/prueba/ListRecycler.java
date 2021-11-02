package com.stan.prueba;

import android.graphics.drawable.Drawable;

public class ListRecycler {

    private String id;
    private String name;
    private String age;
    private String cell;
    private String url;

    public ListRecycler(String id, String name, String age, String cell, String url) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.cell = cell;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

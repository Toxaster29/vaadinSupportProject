package com.packagename.myapp.spring.entity.ufps;

public class UfpsEntity {

    String id;
    String name;
    String description;
    String descriptionEISK;
    String index;

    public UfpsEntity(String id, String name, String description, String descriptionEISK, String index) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.descriptionEISK = descriptionEISK;
        this.index = index;
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

    public String getDescription() {
        return description;
    }

    public Integer getIntId() {
        return Integer.parseInt(id);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}

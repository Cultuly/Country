package org.example;


public class Subregion {
    private int id;
    private String name;
    private int regionId;

    public Subregion(int id, String name, int regionId) {
        this.id = id;
        this.name = name;
        this.regionId = regionId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRegionId() {
        return regionId;
    }
}
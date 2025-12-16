package org.example;


public class Country {
    private int id;
    private String name;
    private int subregionId;
    private long internetUsers;
    private long population;

    public Country(String name, int subregionId, long internetUsers, long population) {
        this.name = name;
        this.subregionId = subregionId;
        this.internetUsers = internetUsers;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public int getSubregionId() {
        return subregionId;
    }

    public long getInternetUsers() {
        return internetUsers;
    }

    public long getPopulation() {
        return population;
    }
}
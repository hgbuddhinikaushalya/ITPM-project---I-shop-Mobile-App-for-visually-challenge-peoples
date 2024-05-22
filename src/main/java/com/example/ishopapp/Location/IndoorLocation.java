package com.example.ishopapp.Location;

public class IndoorLocation {
    private String name;
    private double x;
    private double y;

    public IndoorLocation(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getName() {
        return name;
    }
}
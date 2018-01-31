package ru.pavlov.firstms;

import lombok.Data;

@Data
public class Car {
    private String model;
    private String fuel;
    private int year;

    public Car(String model, String fuel, int year) {
        this.model = model;
        this.fuel = fuel;
        this.year = year;
    }

}

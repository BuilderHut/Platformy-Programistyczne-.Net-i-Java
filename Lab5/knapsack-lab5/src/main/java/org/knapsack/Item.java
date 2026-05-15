package org.knapsack;

public class Item {
    private final int id;
    private final int value;
    private final int weight;

    public Item(int id, int value, int weight) {
        this.id = id;
        this.value = value;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    public double getRatio() {
        return (double) value / weight;
    }

    @Override
    public String toString() {
        return "No: " + id + " v: " + value + " w: " + weight;
    }
}
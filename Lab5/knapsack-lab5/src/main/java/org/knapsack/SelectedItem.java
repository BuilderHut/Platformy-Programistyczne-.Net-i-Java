package org.knapsack;

public class SelectedItem {
    private final Item item;
    private int quantity;

    public SelectedItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public int getTotalValue() {
        return item.getValue() * quantity;
    }

    public int getTotalWeight() {
        return item.getWeight() * quantity;
    }

    @Override
    public String toString() {
        return "No: " + item.getId()
                + " v: " + item.getValue()
                + " w: " + item.getWeight()
                + " quantity: " + quantity;
    }
}
package org.knapsack;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private final List<SelectedItem> selectedItems;
    private int totalValue;
    private int totalWeight;

    public Result() {
        this.selectedItems = new ArrayList<>();
        this.totalValue = 0;
        this.totalWeight = 0;
    }

    public void addItem(Item item) {
        for (SelectedItem selectedItem : selectedItems) {
            if (selectedItem.getItem().getId() == item.getId()) {
                selectedItem.increaseQuantity();
                totalValue += item.getValue();
                totalWeight += item.getWeight();
                return;
            }
        }

        selectedItems.add(new SelectedItem(item, 1));
        totalValue += item.getValue();
        totalWeight += item.getWeight();
    }

    public List<SelectedItem> getSelectedItems() {
        return selectedItems;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public boolean isEmpty() {
        return selectedItems.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (selectedItems.isEmpty()) {
            sb.append("No items selected.").append(System.lineSeparator());
        } else {
            for (SelectedItem selectedItem : selectedItems) {
                sb.append(selectedItem).append(System.lineSeparator());
            }
        }

        sb.append("Weight: ").append(totalWeight).append(System.lineSeparator());
        sb.append("Value: ").append(totalValue);

        return sb.toString();
    }
}
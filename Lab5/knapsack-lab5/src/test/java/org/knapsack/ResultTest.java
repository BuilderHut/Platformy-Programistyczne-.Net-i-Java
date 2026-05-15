package org.knapsack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void addItemAccumulatesTotalsAndQuantityForSameItem() {
        Result result = new Result();
        Item item = new Item(7, 12, 4);

        result.addItem(item);
        result.addItem(item);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getSelectedItems().size());
        assertEquals(2, result.getSelectedItems().get(0).getQuantity());
        assertEquals(24, result.getTotalValue());
        assertEquals(8, result.getTotalWeight());
    }

    @Test
    void toStringShowsEmptyStateWhenNoItemsSelected() {
        Result result = new Result();

        String text = result.toString();

        assertTrue(text.contains("No items selected."));
        assertTrue(text.contains("Weight: 0"));
        assertTrue(text.contains("Value: 0"));
    }
}

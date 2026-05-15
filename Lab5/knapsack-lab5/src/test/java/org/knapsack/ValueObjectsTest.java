package org.knapsack;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Test
    void itemCalculatesRatioAndFormatsText() {
        Item item = new Item(3, 9, 3);

        assertEquals(3.0, item.getRatio());
        assertEquals("No: 3 v: 9 w: 3", item.toString());
    }

    @Test
    void selectedItemExposesTotalsAndText() {
        Item item = new Item(4, 5, 2);
        SelectedItem selectedItem = new SelectedItem(item, 3);

        assertEquals(15, selectedItem.getTotalValue());
        assertEquals(6, selectedItem.getTotalWeight());
        assertEquals("No: 4 v: 5 w: 2 quantity: 3", selectedItem.toString());
    }
}

package org.knapsack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProblemTest {

    @Test
    void generateItemsCreatesExpectedAmountWithinBounds() {
        Problem problem = new Problem(5, 1234, 1, 10);

        assertEquals(5, problem.getItems().size());
        for (Item item : problem.getItems()) {
            assertTrue(item.getValue() >= 1);
            assertTrue(item.getValue() <= 10);
            assertTrue(item.getWeight() >= 1);
            assertTrue(item.getWeight() <= 10);
        }
    }

    @Test
    void solveReturnsEmptyResultForNonPositiveCapacity() {
        Problem problem = new Problem(0, 1, 1, 10);

        Result result = problem.solve(0);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalValue());
        assertEquals(0, result.getTotalWeight());
    }

    @Test
    void solveSelectsItemsByRatioAndCanReuseSameItem() {
        Problem problem = new Problem(0, 1, 1, 10);
        List<Item> items = problem.getItems();
        items.add(new Item(1, 8, 4)); // ratio 2.0
        items.add(new Item(2, 3, 2)); // ratio 1.5
        items.add(new Item(3, 7, 5)); // ratio 1.4

        Result result = problem.solve(6);

        assertFalse(result.isEmpty());
        assertEquals(2, result.getSelectedItems().size());
        assertEquals(11, result.getTotalValue());
        assertEquals(6, result.getTotalWeight());
        assertEquals(1, result.getSelectedItems().get(0).getQuantity());
        assertEquals(1, result.getSelectedItems().get(0).getItem().getId());
        assertEquals(1, result.getSelectedItems().get(1).getQuantity());
        assertEquals(2, result.getSelectedItems().get(1).getItem().getId());
    }

    @Test
    void solveSkipsItemsThatDoNotFit() {
        Problem problem = new Problem(0, 1, 1, 10);
        problem.getItems().add(new Item(1, 100, 20));

        Result result = problem.solve(10);

        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalValue());
        assertEquals(0, result.getTotalWeight());
    }

}

package org.knapsack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Problem {
    private final int numberOfItems;
    private final int seed;
    private final int lowerBound;
    private final int upperBound;
    private final List<Item> items;

    public Problem(int numberOfItems, int seed, int lowerBound, int upperBound) {
        this.numberOfItems = numberOfItems;
        this.seed = seed;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.items = new ArrayList<>();

        generateItems();
    }

    private void generateItems() {
        Random random = new Random(seed);

        for (int i = 0; i < numberOfItems; i++) {
            int value = random.nextInt(upperBound - lowerBound + 1) + lowerBound;
            int weight = random.nextInt(upperBound - lowerBound + 1) + lowerBound;

            items.add(new Item(i, value, weight));
        }
    }

    public Result solve(int capacity) {
        Result result = new Result();

        if (capacity <= 0) {
            return result;
        }

        List<Item> sortedItems = new ArrayList<>(items);

        sortedItems.sort(
                Comparator.comparingDouble(Item::getRatio).reversed()
        );

        int remainingCapacity = capacity;

        for (Item item : sortedItems) {
            while (item.getWeight() <= remainingCapacity) {
                result.addItem(item);
                remainingCapacity -= item.getWeight();
            }

            if (remainingCapacity == 0) {
                break;
            }
        }

        return result;
    }

    public Result Solve(int capacity) {
        return solve(capacity);
    }

    public List<Item> getItems() {
        return items;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public int getSeed() {
        return seed;
    }

    public int getLowerBound() {
        return lowerBound;
    }

    public int getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Item item : items) {
            sb.append(item).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
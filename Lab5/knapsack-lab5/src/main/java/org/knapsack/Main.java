package org.knapsack;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Give number of items:");
        int numberOfItems = scanner.nextInt();

        System.out.println("Give seed:");
        int seed = scanner.nextInt();

        System.out.println("Give knapsack capacity:");
        int capacity = scanner.nextInt();

        Problem problem = new Problem(numberOfItems, seed, 1, 10);

        System.out.println();
        System.out.println("Generated problem:");
        System.out.println(problem);

        Result result = problem.Solve(capacity);

        System.out.println("Solution:");
        System.out.println(result);

        scanner.close();
    }
}
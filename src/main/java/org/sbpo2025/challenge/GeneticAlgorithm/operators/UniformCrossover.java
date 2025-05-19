package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import java.util.Random;

public class UniformCrossover implements CrossoverOperator {
    private final Random random;

    public UniformCrossover(Random random) {
        this.random = random;
    }

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        boolean[] p1Order = parent1.getOrderGenes();
        boolean[] p2Order = parent2.getOrderGenes();
        boolean[] p1Aisle = parent1.getAisleGenes();
        boolean[] p2Aisle = parent2.getAisleGenes();

        boolean[] child1Order = new boolean[p1Order.length];
        boolean[] child2Order = new boolean[p1Order.length];
        boolean[] child1Aisle = new boolean[p1Aisle.length];
        boolean[] child2Aisle = new boolean[p1Aisle.length];

        for (int i = 0; i < p1Order.length; i++) {
            if (random.nextBoolean()) {
                child1Order[i] = p1Order[i];
                child2Order[i] = p2Order[i];
            } else {
                child1Order[i] = p2Order[i];
                child2Order[i] = p1Order[i];
            }
        }
        for (int i = 0; i < p1Aisle.length; i++) {
            if (random.nextBoolean()) {
                child1Aisle[i] = p1Aisle[i];
                child2Aisle[i] = p2Aisle[i];
            } else {
                child1Aisle[i] = p2Aisle[i];
                child2Aisle[i] = p1Aisle[i];
            }
        }
        Individual child1 = parent1.clone();
        Individual child2 = parent2.clone();
        child1.setOrderGenes(child1Order);
        child1.setAisleGenes(child1Aisle);
        child2.setOrderGenes(child2Order);
        child2.setAisleGenes(child2Aisle);
        return new Individual[]{child1, child2};
    }
}

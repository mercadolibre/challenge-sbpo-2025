package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.Random;

public class UniformCrossoverOperator implements CrossoverOperator {
    private final Random random;
    public UniformCrossoverOperator(Random random) {
        this.random = random;
    }
    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        int numGenes = parent1.getGenes().length;
        boolean[] childGenes1 = new boolean[numGenes];
        boolean[] childGenes2 = new boolean[numGenes];
        for (int i = 0; i < numGenes; i++) {
            if (random.nextBoolean()) {
                childGenes1[i] = parent1.getGenes()[i];
                childGenes2[i] = parent2.getGenes()[i];
            } else {
                childGenes1[i] = parent2.getGenes()[i];
                childGenes2[i] = parent1.getGenes()[i];
            }
        }
        return new Individual[]{new Individual(childGenes1), new Individual(childGenes2)};
    }
}

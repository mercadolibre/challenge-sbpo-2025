package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.concurrent.ThreadLocalRandom;

public class OnePointCrossoverOperator implements CrossoverOperator {
    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        int numGenes = Math.min(parent1.getGenes().length, parent2.getGenes().length);
        boolean[] childGenes1 = new boolean[numGenes];
        boolean[] childGenes2 = new boolean[numGenes];
        int crossoverPoint = ThreadLocalRandom.current().nextInt(numGenes);
        for (int i = 0; i < numGenes; i++) {
            if (i < crossoverPoint) {
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

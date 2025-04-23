package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.Random;

public class TwoPointCrossoverOperator implements CrossoverOperator {
    private final Random random;
    public TwoPointCrossoverOperator(Random random) {
        this.random = random;
    }
    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        int numGenes = parent1.getGenes().length;
        boolean[] childGenes1 = new boolean[numGenes];
        boolean[] childGenes2 = new boolean[numGenes];
        int point1 = random.nextInt(numGenes);
        int point2 = random.nextInt(numGenes);
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }
        for (int i = 0; i < numGenes; i++) {
            if (i < point1 || i >= point2) {
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

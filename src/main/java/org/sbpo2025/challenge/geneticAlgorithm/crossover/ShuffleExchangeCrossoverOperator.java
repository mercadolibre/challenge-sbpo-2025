package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Operador Shuffle-Exchange Crossover.
 * Embaralha a ordem dos genes antes de aplicar crossover de ponto único, depois reverte a ordem.
 */
public class ShuffleExchangeCrossoverOperator implements CrossoverOperator {
    private final Random random;

    public ShuffleExchangeCrossoverOperator(Random random) {
        this.random = random;
    }

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        boolean[] genes1 = parent1.getGenes();
        boolean[] genes2 = parent2.getGenes();
        int numGenes = genes1.length;

        // Gera uma permutação aleatória dos índices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < numGenes; i++) indices.add(i);
        Collections.shuffle(indices, random);

        // Embaralha os genes dos pais
        boolean[] shuffled1 = new boolean[numGenes];
        boolean[] shuffled2 = new boolean[numGenes];
        for (int i = 0; i < numGenes; i++) {
            shuffled1[i] = genes1[indices.get(i)];
            shuffled2[i] = genes2[indices.get(i)];
        }

        // Crossover de ponto único
        int point = 1 + random.nextInt(numGenes - 1);
        boolean[] childShuffled1 = new boolean[numGenes];
        boolean[] childShuffled2 = new boolean[numGenes];
        for (int i = 0; i < point; i++) {
            childShuffled1[i] = shuffled1[i];
            childShuffled2[i] = shuffled2[i];
        }
        for (int i = point; i < numGenes; i++) {
            childShuffled1[i] = shuffled2[i];
            childShuffled2[i] = shuffled1[i];
        }

        // Reverte o embaralhamento para a ordem original
        boolean[] child1 = new boolean[numGenes];
        boolean[] child2 = new boolean[numGenes];
        for (int i = 0; i < numGenes; i++) {
            int origIdx = indices.get(i);
            child1[origIdx] = childShuffled1[i];
            child2[origIdx] = childShuffled2[i];
        }

        return new Individual[] { new Individual(child1), new Individual(child2) };
    }
}

package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Operador Half-Uniform Crossover (HUX).
 * Troca exatamente metade dos bits diferentes entre os pais.
 */
public class HUXCrossoverOperator implements CrossoverOperator {
    private final Random random;

    public HUXCrossoverOperator(Random random) {
        this.random = random;
    }

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        boolean[] genes1 = parent1.getGenes();
        boolean[] genes2 = parent2.getGenes();
        int numGenes = Math.min(genes1.length, genes2.length); // usa o menor tamanho
        boolean[] child1 = new boolean[numGenes];
        boolean[] child2 = new boolean[numGenes];

        // Copia os genes até numGenes
        for (int i = 0; i < numGenes; i++) {
            child1[i] = genes1[i];
            child2[i] = genes2[i];
        }

        // Identifica posições onde os genes diferem
        List<Integer> diffIndices = new ArrayList<>();
        for (int i = 0; i < numGenes; i++) {
            if (genes1[i] != genes2[i]) {
                diffIndices.add(i);
            }
        }

        // Seleciona aleatoriamente metade das posições diferentes
        Collections.shuffle(diffIndices, random);
        int half = diffIndices.size() / 2;
        for (int i = 0; i < half; i++) {
            int idx = diffIndices.get(i);
            // Troca os bits
            child1[idx] = genes2[idx];
            child2[idx] = genes1[idx];
        }

        return new Individual[] { new Individual(child1), new Individual(child2) };
    }
}

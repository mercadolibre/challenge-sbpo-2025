package org.sbpo2025.challenge.geneticAlgorithm.mutation.binary;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.MutationOperator;

import java.util.Random;

/**
 * Operador de mutação Swap Mutation.
 * Seleciona dois genes aleatórios e troca seus valores.
 */
public class SwapMutationOperator implements MutationOperator {
    private final Random random;

    public SwapMutationOperator(Random random) {
        this.random = random;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        boolean[] genes = individual.getGenes();
        int n = genes.length;
        if (n < 2) return;
        // Para cada possível swap, aplica com probabilidade mutationRate
        if (random.nextDouble() < mutationRate) {
            int i = random.nextInt(n);
            int j = random.nextInt(n - 1);
            if (j >= i) j++;
            // Troca os valores
            boolean temp = genes[i];
            genes[i] = genes[j];
            genes[j] = temp;
            individual.setGene(i, genes[i]);
            individual.setGene(j, genes[j]);
        }
    }
}

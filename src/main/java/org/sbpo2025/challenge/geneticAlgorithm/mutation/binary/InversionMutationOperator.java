package org.sbpo2025.challenge.geneticAlgorithm.mutation.binary;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import org.sbpo2025.challenge.geneticAlgorithm.mutation.MutationOperator;

import java.util.Random;

/**
 * Operador de mutação Inversion (Reverse Sequence) Mutation.
 * Seleciona um segmento e inverte sua sequência de genes.
 */
public class InversionMutationOperator implements MutationOperator {
    private final Random random;

    public InversionMutationOperator(Random random) {
        this.random = random;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        boolean[] genes = individual.getGenes();
        int n = genes.length;
        if (n < 2) return;
        if (random.nextDouble() < mutationRate) {
            int start = random.nextInt(n - 1);
            int end = start + 1 + random.nextInt(n - start);
            // Inverte o segmento [start, end)
            int left = start;
            int right = end - 1;
            while (left < right) {
                boolean temp = genes[left];
                genes[left] = genes[right];
                genes[right] = temp;
                individual.setGene(left, genes[left]);
                individual.setGene(right, genes[right]);
                left++;
                right--;
            }
        }
    }
}

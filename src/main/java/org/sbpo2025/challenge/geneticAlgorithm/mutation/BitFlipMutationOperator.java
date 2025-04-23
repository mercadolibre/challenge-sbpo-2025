package org.sbpo2025.challenge.geneticAlgorithm.mutation;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.Random;

/**
 * Operador de mutação padrão: bit flip (inversão de bits com taxa de mutação).
 */
public class BitFlipMutationOperator implements MutationOperator {
    private final Random random;

    public BitFlipMutationOperator(Random random) {
        this.random = random;
    }

    @Override
    public void mutate(Individual individual, double mutationRate) {
        boolean[] genes = individual.getGenes();
        for (int i = 0; i < genes.length; i++) {
            if (random.nextDouble() < mutationRate) {
                individual.setGene(i, !genes[i]);
            }
        }
    }
}

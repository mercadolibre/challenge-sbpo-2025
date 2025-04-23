package org.sbpo2025.challenge.geneticAlgorithm.mutation;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;

/**
 * Interface para operadores de mutação de indivíduos.
 */
public interface MutationOperator {
    /**
     * Aplica a mutação ao indivíduo.
     * @param individual O indivíduo a ser mutado
     * @param mutationRate Taxa de mutação
     */
    void mutate(Individual individual, double mutationRate);
}

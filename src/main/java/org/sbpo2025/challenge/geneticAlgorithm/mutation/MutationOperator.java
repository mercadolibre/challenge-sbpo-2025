package org.sbpo2025.challenge.geneticAlgorithm.mutation;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;

public interface MutationOperator {
    void mutate(Individual individual, double mutationRate);
}

package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;

public interface MutationOperator {
    void mutate(Individual individual);
}

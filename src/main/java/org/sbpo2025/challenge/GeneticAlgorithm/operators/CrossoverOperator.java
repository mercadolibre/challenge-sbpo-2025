package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;

public interface CrossoverOperator {
    Individual[] crossover(Individual parent1, Individual parent2);
}

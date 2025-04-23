package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;

/**
 * Interface para operadores de crossover de indivíduos.
 */
public interface CrossoverOperator {
    /**
     * Realiza o crossover entre dois pais e retorna os filhos.
     * @param parent1 Primeiro pai
     * @param parent2 Segundo pai
     * @return Array com dois filhos resultantes do crossover
     */
    Individual[] crossover(Individual parent1, Individual parent2);
}

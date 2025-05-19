package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import java.util.List;

/**
 * Interface para operadores de seleção em um Algoritmo Genético.
 * Operadores de seleção são responsáveis por escolher indivíduos de uma população
 * para serem pais da próxima geração.
 */
public interface SelectionOperator {

    /**
     * Seleciona um indivíduo de uma população com base em seus valores de fitness.
     *
     * @param population A lista de indivíduos da população atual.
     * @param fitnesses Um array contendo os valores de fitness correspondentes a cada indivíduo na população.
     *                  O fitness no índice i corresponde ao indivíduo no índice i da lista de população.
     * @return O indivíduo selecionado.
     */
    Individual select(List<Individual> population, double[] fitnesses);
}

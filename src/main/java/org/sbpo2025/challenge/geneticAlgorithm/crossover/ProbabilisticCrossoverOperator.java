package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.Random;
import java.util.Arrays;

/**
 * Meta-operador de crossover que escolhe entre diferentes estratégias conforme probabilidade.
 */
public class ProbabilisticCrossoverOperator implements CrossoverOperator {
    private final CrossoverOperator[] operators;
    private final double[] cumProbabilities;
    private final Random random;

    /**
     * @param operators Array de operadores de crossover
     * @param probabilities Probabilidades associadas a cada operador (deve somar 1.0)
     * @param random Gerador de números aleatórios
     */
    public ProbabilisticCrossoverOperator(CrossoverOperator[] operators, double[] probabilities, Random random) {
        if (operators.length != probabilities.length) {
            throw new IllegalArgumentException("Tamanhos de operadores e probabilidades devem ser iguais");
        }
        double sum = 0.0;
        this.cumProbabilities = new double[probabilities.length];
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            this.cumProbabilities[i] = sum;
        }
        if (Math.abs(sum - 1.0) > 1e-6) {
            throw new IllegalArgumentException("Probabilidades devem somar 1.0");
        }
        this.operators = operators;
        this.random = random;
    }

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        double r = random.nextDouble();
        int idx = Arrays.binarySearch(cumProbabilities, r);
        if (idx < 0) {
            idx = -idx - 1;
        }
        if (idx >= operators.length) idx = operators.length - 1;
        return operators[idx].crossover(parent1, parent2);
    }
}

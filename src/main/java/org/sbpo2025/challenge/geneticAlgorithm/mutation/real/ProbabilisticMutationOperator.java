package org.sbpo2025.challenge.geneticAlgorithm.mutation.real;

import java.util.Random;

/**
 * Operador probabilístico de mutação: escolhe um operador de mutação com base em probabilidades.
 */
public class ProbabilisticMutationOperator implements MutationOperator {
    private final MutationOperator[] operators;
    private final double[] cumProbabilities;
    private final Random random;

    public ProbabilisticMutationOperator(MutationOperator[] operators, double[] probabilities, Random random) {
        if (operators == null || probabilities == null || operators.length != probabilities.length) {
            throw new IllegalArgumentException("Operators and probabilities must be non-null and of same length");
        }
        this.operators = operators;
        this.random = random;
        this.cumProbabilities = new double[probabilities.length];
        double sum = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            sum += probabilities[i];
            this.cumProbabilities[i] = sum;
        }
        // Normalize if sum != 1
        if (sum != 1.0) {
            for (int i = 0; i < cumProbabilities.length; i++) {
                cumProbabilities[i] /= sum;
            }
        }
    }

    @Override
    public void mutate(org.sbpo2025.challenge.geneticAlgorithm.Individual individual, double mutationRate) {
        double r = random.nextDouble();
        int idx = 0;
        while (idx < cumProbabilities.length && r > cumProbabilities[idx]) {
            idx++;
        }
        if (idx >= operators.length) {
            idx = operators.length - 1;
        }
        operators[idx].mutate(individual, mutationRate);
    }
}

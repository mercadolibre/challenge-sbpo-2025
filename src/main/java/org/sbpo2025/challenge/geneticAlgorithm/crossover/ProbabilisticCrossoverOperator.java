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
    private final double[] probabilities;
    private final int[] successCounts;
    private final int[] attemptCounts;
    private static final double MIN_PROB = 0.05; // Probabilidade mínima para cada operador

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
        this.probabilities = Arrays.copyOf(probabilities, probabilities.length);
        this.successCounts = new int[operators.length];
        this.attemptCounts = new int[operators.length];
    }

    /**
     * Realiza crossover e retorna também o índice do operador utilizado.
     */
    public CrossoverResult crossoverWithOperatorIndex(Individual parent1, Individual parent2) {
        double r = random.nextDouble();
        int idx = Arrays.binarySearch(cumProbabilities, r);
        if (idx < 0) {
            idx = -idx - 1;
        }
        if (idx >= operators.length) idx = operators.length - 1;
        attemptCounts[idx]++;
        Individual[] children = operators[idx].crossover(parent1, parent2);
        return new CrossoverResult(children, idx);
    }

    /**
     * Registra sucesso para o operador idx.
     */
    public void registerSuccess(int idx) {
        if (idx >= 0 && idx < successCounts.length) {
            successCounts[idx]++;
        }
    }

    /**
     * Atualiza as probabilidades proporcionalmente ao sucesso recente.
     * Usa normalização simples e aplica probabilidade mínima.
     */
    public void updateProbabilities() {
        double[] newProbs = new double[probabilities.length];
        double total = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            newProbs[i] = successCounts[i] + 1e-3; // Suavização
            total += newProbs[i];
        }
        // Normaliza e aplica probabilidade mínima
        double minTotal = MIN_PROB * probabilities.length;
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] = Math.max(newProbs[i] / total, MIN_PROB);
        }
        // Renormaliza para somar 1
        double sum = Arrays.stream(probabilities).sum();
        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= sum;
            cumProbabilities[i] = (i == 0) ? probabilities[i] : cumProbabilities[i-1] + probabilities[i];
        }
        // Reseta contadores
        Arrays.fill(successCounts, 0);
        Arrays.fill(attemptCounts, 0);
    }

    /**
     * Classe auxiliar para retornar filhos e índice do operador.
     */
    public static class CrossoverResult {
        public final Individual[] children;
        public final int operatorIndex;
        public CrossoverResult(Individual[] children, int operatorIndex) {
            this.children = children;
            this.operatorIndex = operatorIndex;
        }
    }

    // O método crossover original pode ser mantido para compatibilidade
    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        return crossoverWithOperatorIndex(parent1, parent2).children;
    }
}

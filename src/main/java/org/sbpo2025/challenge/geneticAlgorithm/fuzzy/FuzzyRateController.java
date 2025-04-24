package org.sbpo2025.challenge.geneticAlgorithm.fuzzy;

import org.sbpo2025.challenge.geneticAlgorithm.rates.RateControlStrategy;
import org.sbpo2025.challenge.geneticAlgorithm.rates.RateControlContext;

/**
 * Controlador fuzzy para ajuste das taxas de mutação e crossover.
 * Usa regras fuzzy simples baseadas em melhoria média e diversidade.
 */
public class FuzzyRateController implements RateControlStrategy {
    // Limites das taxas
    private final double minMutation = 0.01;
    private final double maxMutation = 0.2;
    private final double minCrossover = 0.5;
    private final double maxCrossover = 0.99;

    /**
     * Calcula as taxas de mutação e crossover usando lógica fuzzy.
     * @param avgImprovement melhoria média da população (0 a 1)
     * @param diversity diversidade da população (0 a 1)
     * @return array [mutationRate, crossoverRate]
     */
    public double[] getRates(double avgImprovement, double diversity) {
        // Funções de pertinência simples (triangulares)
        double lowImprovement = pertinenciaDecrescente(avgImprovement, 0.05, 0.15);
        double highImprovement = pertinenciaCrescente(avgImprovement, 0.10, 0.25);
        double lowDiversity = pertinenciaDecrescente(diversity, 0.10, 0.30);
        double highDiversity = pertinenciaCrescente(diversity, 0.20, 0.50);

        // Regras fuzzy (exemplo):
        // 1. Se melhoria baixa OU diversidade baixa => mutationRate alto, crossoverRate baixo
        double explora = Math.max(lowImprovement, lowDiversity);
        // 2. Se melhoria alta E diversidade alta => mutationRate baixo, crossoverRate alto
        double intensifica = Math.min(highImprovement, highDiversity);
        // 3. Caso intermediário: média
        double neutro = 1.0 - Math.max(explora, intensifica);

        // Defuzzificação (média ponderada)
        double mutationRate = explora * maxMutation + intensifica * minMutation + neutro * ((maxMutation + minMutation) / 2.0);
        double crossoverRate = explora * minCrossover + intensifica * maxCrossover + neutro * ((maxCrossover + minCrossover) / 2.0);

        // Garante limites
        mutationRate = Math.max(minMutation, Math.min(maxMutation, mutationRate));
        crossoverRate = Math.max(minCrossover, Math.min(maxCrossover, crossoverRate));
        return new double[] {mutationRate, crossoverRate};
    }

    @Override
    public double[] getRates(RateControlContext context) {
        return getRates(context.avgImprovement, context.diversity);
    }

    // Função de pertinência crescente (0 até a, 1 a partir de b)
    private double pertinenciaCrescente(double x, double a, double b) {
        if (x <= a) return 0.0;
        if (x >= b) return 1.0;
        return (x - a) / (b - a);
    }
    // Função de pertinência decrescente (1 até a, 0 a partir de b)
    private double pertinenciaDecrescente(double x, double a, double b) {
        if (x <= a) return 1.0;
        if (x >= b) return 0.0;
        return (b - x) / (b - a);
    }
}

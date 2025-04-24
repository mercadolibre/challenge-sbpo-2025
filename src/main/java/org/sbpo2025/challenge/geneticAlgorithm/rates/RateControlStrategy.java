package org.sbpo2025.challenge.geneticAlgorithm.rates;

public interface RateControlStrategy {
    /**
     * Atualiza e retorna as taxas de mutação e crossover.
     * @param context contexto com métricas da população
     * @return array [mutationRate, crossoverRate]
     */
    double[] getRates(RateControlContext context);
}

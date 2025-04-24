package org.sbpo2025.challenge.geneticAlgorithm.rates;

public class DefaultRateControlStrategy implements RateControlStrategy {
    @Override
    public double[] getRates(RateControlContext context) {
        // Retorna taxas fixas (exemplo: 0.7 para crossover, 0.1 para mutação, 0.2 para outros)
        return new double[] {0.7, 0.1, 0.2};
    }
}

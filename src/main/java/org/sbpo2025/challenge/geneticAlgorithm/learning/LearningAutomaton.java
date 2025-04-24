package org.sbpo2025.challenge.geneticAlgorithm.learning;

import org.sbpo2025.challenge.geneticAlgorithm.rates.RateControlStrategy;
import org.sbpo2025.challenge.geneticAlgorithm.rates.RateControlContext;

import java.util.Random;

/**
 * Autômato de aprendizado para ajuste de parâmetros (ex: mutationRate, crossoverRate).
 * Ações: aumentar, diminuir, manter.
 */
public class LearningAutomaton implements RateControlStrategy {
    private final double[] actionProbabilities = {1.0/3, 1.0/3, 1.0/3}; // aumentar, diminuir, manter
    private final double step = 0.05; // passo de ajuste do parâmetro
    private final double learningRate = 0.1; // taxa de aprendizado
    private final double minValue;
    private final double maxValue;
    private double value;
    private int lastAction = 2; // manter por padrão

    public LearningAutomaton(double initialValue, double minValue, double maxValue) {
        this.value = initialValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    /**
     * Seleciona uma ação (0=aumentar, 1=diminuir, 2=manter) e ajusta o valor.
     */
    public double selectAndApplyAction(Random random) {
        double r = random.nextDouble();
        int action;
        if (r < actionProbabilities[0]) action = 0;
        else if (r < actionProbabilities[0] + actionProbabilities[1]) action = 1;
        else action = 2;
        lastAction = action;
        if (action == 0) value = Math.min(maxValue, value + step);
        else if (action == 1) value = Math.max(minValue, value - step);
        // manter não altera
        return value;
    }

    /**
     * Atualiza as probabilidades de ação com base em recompensa (true) ou penalidade (false).
     */
    public void update(boolean reward) {
        if (reward) {
            actionProbabilities[lastAction] += learningRate * (1 - actionProbabilities[lastAction]);
        } else {
            actionProbabilities[lastAction] -= learningRate * actionProbabilities[lastAction];
        }
        // Normaliza
        double sum = actionProbabilities[0] + actionProbabilities[1] + actionProbabilities[2];
        for (int i = 0; i < 3; i++) actionProbabilities[i] = Math.max(0.0, actionProbabilities[i] / sum);
    }

    public double getValue() { return value; }
    public void setValue(double v) { value = Math.max(minValue, Math.min(maxValue, v)); }
    public double[] getActionProbabilities() { return actionProbabilities.clone(); }

    @Override
    public double[] getRates(RateControlContext context) {
        // Para compatibilidade, ignora o contexto e retorna o valor atual do autômato para mutation e crossover
        return new double[] { getValue(), getValue() };
    }
}

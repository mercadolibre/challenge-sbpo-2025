package org.sbpo2025.challenge.geneticAlgorithm.rates;

public class RateControlContext {
    public final double avgImprovement;
    public final double diversity;
    public final double lastBestFitness;

    public RateControlContext(double avgImprovement, double diversity, double lastBestFitness) {
        this.avgImprovement = avgImprovement;
        this.diversity = diversity;
        this.lastBestFitness = lastBestFitness;
    }
}

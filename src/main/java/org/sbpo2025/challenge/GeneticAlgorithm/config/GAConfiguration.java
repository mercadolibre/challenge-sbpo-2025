package org.sbpo2025.challenge.GeneticAlgorithm.config;

public class GAConfiguration {

    private int populationSize;
    private int numberOfGenerations;
    private double crossoverRate;
    private double mutationRate;
    private double alphaCoveragePenalty;
    private double betaLBUBPenalty;
    private int tournamentSize;

    // Construtor pode ser adicionado aqui se necessário, ou usar um Builder.
    // Por enquanto, vamos focar nos getters conforme o todo.md.

    public GAConfiguration(int populationSize, int numberOfGenerations, double crossoverRate, double mutationRate, double alphaCoveragePenalty, double betaLBUBPenalty, int tournamentSize) {
        this.populationSize = populationSize;
        this.numberOfGenerations = numberOfGenerations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.alphaCoveragePenalty = alphaCoveragePenalty;
        this.betaLBUBPenalty = betaLBUBPenalty;
        this.tournamentSize = tournamentSize;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getNumberOfGenerations() {
        return numberOfGenerations;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public double getAlphaCoveragePenalty() {
        return alphaCoveragePenalty;
    }

    public double getBetaLBUBPenalty() {
        return betaLBUBPenalty;
    }

    public int getTournamentSize() {
        return tournamentSize;
    }

    // Setters podem ser adicionados se a configuração for mutável após a criação.
    // Por ora, consideramos a configuração imutável após instanciada.
}

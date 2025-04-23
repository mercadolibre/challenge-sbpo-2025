package org.sbpo2025.challenge;

/**
 * Configurações globais e parâmetros do desafio.
 * Centraliza valores para fácil ajuste e evita números mágicos no código.
 */
public final class ChallengeConfig {
    private ChallengeConfig() {}

    /** Tempo máximo de execução (ms) para o solver completo */
    public static final long MAX_RUNTIME_MILLIS = 600_000L;

    /** Tempo máximo (segundos) para busca inicial via CP-SAT */
    public static final double INITIAL_CP_SAT_TIME_LIMIT_SECONDS = 300.0;

    /** Tempo mínimo restante (ms) para executar o GA */
    public static final long MIN_TIME_FOR_GA_MILLIS = 10_000L;

    // Parâmetros padrão do Algoritmo Genético
    public static final int GA_POPULATION_SIZE_LARGE = 150;
    public static final int GA_POPULATION_SIZE_SMALL = 80;
    public static final int GA_MAX_GENERATIONS_LARGE = 500;
    public static final int GA_MAX_GENERATIONS_SMALL = 1500;
    public static final int GA_MAX_GENERATIONS_NO_INIT = 2000;
    public static final int GA_NO_IMPROVEMENT_LIMIT = 50;
    public static final int GA_NO_IMPROVEMENT_LIMIT_NO_INIT = 100;
    public static final double GA_CROSSOVER_RATE = 0.8;
    public static final double GA_CROSSOVER_RATE_NO_INIT = 0.85;
    public static final double GA_MUTATION_RATE_LARGE = 0.03;
    public static final double GA_MUTATION_RATE_NO_INIT = 0.1;
    public static final int GA_ELITISM_COUNT = 10;
    public static final int GA_ELITISM_COUNT_NO_INIT = 5;
}

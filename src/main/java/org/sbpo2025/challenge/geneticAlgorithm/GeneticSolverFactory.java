package org.sbpo2025.challenge.geneticAlgorithm;

import org.sbpo2025.challenge.ChallengeSolver;

import java.util.List;
import java.util.Map;

/**
 * Fábrica para criar instâncias do GeneticSolver com diferentes configurações.
 */
public class GeneticSolverFactory {

    /**
     * Cria um solver genético com configurações padrão
     * @param orders Lista de pedidos (mapa de item para quantidade)
     * @param aisles Lista de corredores (mapa de item para capacidade)
     * @param nItems Número total de itens diferentes
     * @param waveSizeLB Limite inferior da wave
     * @param waveSizeUB Limite superior da wave
     * @return Instância de ChallengeSolver baseada no algoritmo genético
     */
    public static ChallengeSolver createGeneticSolver(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int nItems,
            int waveSizeLB,
            int waveSizeUB) {

        return new GeneticSolver(orders, aisles, nItems, waveSizeLB, waveSizeUB);
    }

    /**
     * Cria um solver genético com configurações personalizadas
     * @param orders Lista de pedidos
     * @param aisles Lista de corredores
     * @param nItems Número total de itens diferentes
     * @param waveSizeLB Limite inferior da wave
     * @param waveSizeUB Limite superior da wave
     * @param populationSize Tamanho da população
     * @param maxGenerations Número máximo de gerações
     * @param noImprovementLimit Limite de gerações sem melhoria para parar
     * @param crossoverRate Taxa de crossover
     * @param mutationRate Taxa de mutação
     * @param elitismCount Número de indivíduos elite preservados entre gerações
     * @param useCpSat Indica se deve usar CP-SAT para solução inicial
     * @return Instância de ChallengeSolver baseada no algoritmo genético
     */
    public static ChallengeSolver createCustomGeneticSolver(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int nItems,
            int waveSizeLB,
            int waveSizeUB,
            int populationSize,
            int maxGenerations,
            int noImprovementLimit,
            double crossoverRate,
            double mutationRate,
            int elitismCount,
            boolean useCpSat) {

        return new GeneticSolver(
            orders,
            aisles,
            nItems,
            waveSizeLB,
            waveSizeUB,
            populationSize,
            maxGenerations,
            noImprovementLimit,
            crossoverRate,
            mutationRate,
            elitismCount,
            useCpSat
        );
    }

    /**
     * Cria um solver otimizado para instâncias pequenas
     */
    public static ChallengeSolver createSmallInstanceSolver(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int nItems,
            int waveSizeLB,
            int waveSizeUB) {

        return new GeneticSolver(
            orders,
            aisles,
            nItems,
            waveSizeLB,
            waveSizeUB,
            50,    // população menor
            2000,  // mais gerações
            100,   // mais gerações sem melhoria
            0.85,  // alta taxa de crossover
            0.1,   // taxa de mutação média
            5,     // poucos elites
            true   // usa CP-SAT
        );
    }

    /**
     * Cria um solver otimizado para instâncias grandes
     */
    public static ChallengeSolver createLargeInstanceSolver(
            List<Map<Integer, Integer>> orders,
            List<Map<Integer, Integer>> aisles,
            int nItems,
            int waveSizeLB,
            int waveSizeUB) {

        return new GeneticSolver(
            orders,
            aisles,
            nItems,
            waveSizeLB,
            waveSizeUB,
            150,   // população maior
            500,   // menos gerações
            30,    // menos gerações sem melhoria
            0.75,  // taxa de crossover média
            0.03,  // taxa de mutação baixa
            15,    // mais elites
            true   // usa CP-SAT
        );
    }
}

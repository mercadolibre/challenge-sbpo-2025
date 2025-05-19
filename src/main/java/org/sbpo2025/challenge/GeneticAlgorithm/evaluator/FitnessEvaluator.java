package org.sbpo2025.challenge.GeneticAlgorithm.evaluator;

import java.util.List;
import java.util.Map;
import org.sbpo2025.challenge.GeneticAlgorithm.config.GAConfiguration;
import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;

/**
 * Responsável por calcular a função de fitness de um indivíduo,
 * incluindo a aplicação de penalidades por inviabilidade.
 */
public class FitnessEvaluator {

    private final double alphaCoveragePenalty;
    private final double betaLBUBPenalty;

    public FitnessEvaluator(GAConfiguration config) {
        this.alphaCoveragePenalty = config.getAlphaCoveragePenalty();
        this.betaLBUBPenalty = config.getBetaLBUBPenalty();
    }

    /**
     * Calcula o fitness de um indivíduo.
     * Conforme Seção 2 do roteiro: Função de Fitness e item 2.3 do todo.md.
     * @param individual O indivíduo a ser avaliado.
     * @param orders Lista de pedidos, cada pedido é um mapa de ID do item para quantidade.
     * @param aisles Lista de corredores, cada corredor é um mapa de ID do item para quantidade.
     * @param LB Limite inferior para o tamanho da onda (total de itens coletados).
     * @param UB Limite superior para o tamanho da onda (total de itens coletados).
     * @return O valor de fitness calculado.
     */
    public double evaluate(Individual individual,
                           List<Map<Integer, Integer>> orders,
                           List<Map<Integer, Integer>> aisles,
                           int LB,
                           int UB) {
        // TODO: Implementar lógica da Seção 2: Função de Fitness
        // Utilizar os parâmetros: individual, orders, aisles, LB, UB
        // e os campos: alphaCoveragePenalty, betaLBUBPenalty

        // 1. Cobertura de pedidos (P_cobertura)
        //    - Verifique para cada pedido o com xo=1 se todos os itens do pedido
        //      estão disponíveis nos corredores com ya=1.
        //    - P_cobertura = alphaCoveragePenalty * (#itens não-cobertos)

        // 2. Total de itens coletados (T) e penalidade LB/UB (P_LB/UB)
        //    - T = Σ_{o: xo=1} Σ_{i∈Io} u_oi  (onde u_oi é a quantidade do item i no pedido o)
        //    - P_LB/UB = betaLBUBPenalty * max(0, LB-T, T-UB)

        // 3. Objetivo real (f_real)
        //    - f_real = T / (Σ_a y_a)  (evitar divisão por zero se Σ_a y_a == 0)
        //      (Σ_a y_a é o número de corredores selecionados no individual)

        // 4. Fitness final (F)
        //    - F = f_real - P_cobertura - P_LB/UB

        // Placeholder - deve ser substituído pela lógica real
        double fitness = 0.0;

        // Atualiza o fitness no objeto Individual também
        // Esta linha pode ser responsabilidade do chamador, dependendo do design.
        // Por enquanto, mantemos aqui para consistência com a versão anterior.
        individual.setFitness(fitness);
        return fitness;
    }

    // Métodos auxiliares podem ser adicionados aqui para calcular T, P_cobertura, etc.
    // Exemplo: private int calculateTotalCollectedItems(Individual individual, List<Map<Integer, Integer>> orders) { ... }
    // Exemplo: private int calculateNonCoveredItems(Individual individual, List<Map<Integer, Integer>> orders, List<Map<Integer, Integer>> aisles) { ... }
    // Exemplo: private int countSelectedAisles(Individual individual) { ... }
}

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
        // 1. Cobertura de pedidos (P_cobertura)
        int uncoveredItems = 0;
        boolean[] orderGenes = individual.getOrderGenes();
        boolean[] aisleGenes = individual.getAisleGenes();
        // Para cada pedido selecionado, verificar se todos os itens estão cobertos
        for (int o = 0; o < orders.size(); o++) {
            if (!orderGenes[o]) continue;
            Map<Integer, Integer> pedido = orders.get(o);
            for (Integer itemId : pedido.keySet()) {
                boolean coberto = false;
                for (int a = 0; a < aisles.size(); a++) {
                    if (!aisleGenes[a]) continue;
                    Map<Integer, Integer> corredor = aisles.get(a);
                    if (corredor.containsKey(itemId) && corredor.get(itemId) > 0) {
                        coberto = true;
                        break;
                    }
                }
                if (!coberto) uncoveredItems++;
            }
        }
        double P_cobertura = alphaCoveragePenalty * uncoveredItems;

        // 2. Total de itens coletados (T)
        int T = 0;
        for (int o = 0; o < orders.size(); o++) {
            if (!orderGenes[o]) continue;
            Map<Integer, Integer> pedido = orders.get(o);
            for (Integer itemId : pedido.keySet()) {
                T += pedido.get(itemId);
            }
        }
        // Penalidade LB/UB
        double P_LBUB = 0.0;
        if (T < LB) P_LBUB = betaLBUBPenalty * (LB - T);
        else if (T > UB) P_LBUB = betaLBUBPenalty * (T - UB);

        // 3. Razão itens/corredores
        int nAislesSelected = 0;
        for (boolean b : aisleGenes) if (b) nAislesSelected++;
        double f_real = nAislesSelected > 0 ? ((double) T) / nAislesSelected : 0.0;

        // 4. Fitness final
        double fitness = f_real - P_cobertura - P_LBUB;
        individual.setFitness(fitness);
        return fitness;
    }

    // Métodos auxiliares podem ser adicionados aqui para calcular T, P_cobertura, etc.
    // Exemplo: private int calculateTotalCollectedItems(Individual individual, List<Map<Integer, Integer>> orders) { ... }
    // Exemplo: private int calculateNonCoveredItems(Individual individual, List<Map<Integer, Integer>> orders, List<Map<Integer, Integer>> aisles) { ... }
    // Exemplo: private int countSelectedAisles(Individual individual) { ... }
}

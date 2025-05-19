package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import java.util.List;
import java.util.Map;

public class WaveRepair implements RepairOperator {
    @Override
    public void repair(Individual individual,
                      List<Map<Integer, Integer>> orders,
                      List<Map<Integer, Integer>> aisles,
                      int waveSizeLB,
                      int waveSizeUB,
                      int nItems) {
        // Exemplo simplificado: garantir pelo menos um pedido e um corredor
        boolean[] orderGenes = individual.getOrderGenes();
        boolean[] aisleGenes = individual.getAisleGenes();
        boolean algumPedido = false;
        boolean algumCorredor = false;
        for (boolean b : orderGenes) if (b) algumPedido = true;
        for (boolean b : aisleGenes) if (b) algumCorredor = true;
        if (!algumPedido && orderGenes.length > 0) orderGenes[0] = true;
        if (!algumCorredor && aisleGenes.length > 0) aisleGenes[0] = true;
        // Ajuste de T para LB/UB pode ser implementado conforme regras do problema
    }
}

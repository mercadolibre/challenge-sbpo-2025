package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import java.util.List;
import java.util.Map;

public interface RepairOperator {
    void repair(Individual individual,
                List<Map<Integer, Integer>> orders,
                List<Map<Integer, Integer>> aisles,
                int waveSizeLB,
                int waveSizeUB,
                int nItems);
}

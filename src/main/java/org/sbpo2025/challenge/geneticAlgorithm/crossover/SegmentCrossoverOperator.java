package org.sbpo2025.challenge.geneticAlgorithm.crossover;

import org.sbpo2025.challenge.geneticAlgorithm.Individual;
import java.util.Random;

/**
 * Operador Segment Crossover.
 * Realiza crossover por blocos (segmentos) de tamanho fixo, preservando blocos de genes correlacionados.
 */
public class SegmentCrossoverOperator implements CrossoverOperator {
    private final Random random;
    private final int segmentSize;

    /**
     * @param random gerador de aleatoriedade
     * @param segmentSize tamanho do segmento (janela)
     */
    public SegmentCrossoverOperator(Random random, int segmentSize) {
        this.random = random;
        this.segmentSize = segmentSize;
    }

    @Override
    public Individual[] crossover(Individual parent1, Individual parent2) {
        boolean[] genes1 = parent1.getGenes();
        boolean[] genes2 = parent2.getGenes();
        int numGenes = genes1.length;
        boolean[] child1 = new boolean[numGenes];
        boolean[] child2 = new boolean[numGenes];

        int i = 0;
        while (i < numGenes) {
            // Decide aleatoriamente se troca o bloco ou mantém igual ao pai
            boolean swap = random.nextBoolean();
            int end = Math.min(i + segmentSize, numGenes);
            for (int j = i; j < end; j++) {
                if (swap) {
                    child1[j] = genes2[j];
                    child2[j] = genes1[j];
                } else {
                    child1[j] = genes1[j];
                    child2[j] = genes2[j];
                }
            }
            i = end;
        }
        return new Individual[] { new Individual(child1), new Individual(child2) };
    }
}

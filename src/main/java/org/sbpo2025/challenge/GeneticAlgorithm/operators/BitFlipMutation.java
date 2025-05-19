package org.sbpo2025.challenge.GeneticAlgorithm.operators;

import org.sbpo2025.challenge.GeneticAlgorithm.models.Individual;
import org.sbpo2025.challenge.GeneticAlgorithm.config.GAConfiguration;
import java.util.Random;

public class BitFlipMutation implements MutationOperator {
    private final GAConfiguration gaConfiguration;
    private final Random random;

    public BitFlipMutation(GAConfiguration gaConfiguration, Random random) {
        this.gaConfiguration = gaConfiguration;
        this.random = random;
    }

    @Override
    public void mutate(Individual individual) {
        boolean[] orderGenes = individual.getOrderGenes();
        for (int i = 0; i < orderGenes.length; i++) {
            if (random.nextDouble() < gaConfiguration.getMutationRate()) {
                orderGenes[i] = !orderGenes[i];
            }
        }
        // Não precisa setOrderGenes se estamos modificando o array diretamente

        boolean[] aisleGenes = individual.getAisleGenes();
        for (int i = 0; i < aisleGenes.length; i++) {
            if (random.nextDouble() < gaConfiguration.getMutationRate()) {
                aisleGenes[i] = !aisleGenes[i];
            }
        }
    }
}

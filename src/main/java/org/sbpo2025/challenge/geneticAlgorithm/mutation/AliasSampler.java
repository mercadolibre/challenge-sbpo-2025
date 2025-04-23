package org.sbpo2025.challenge.geneticAlgorithm.mutation;

import java.util.Random;

/**
 * Utilitário para amostragem eficiente (O(1)) usando o Walker's Alias Method.
 */
public class AliasSampler {
    private final int[] alias;
    private final double[] prob;
    private final int n;
    private final Random random;

    public AliasSampler(double[] probabilities, Random random) {
        this.n = probabilities.length;
        this.prob = new double[n];
        this.alias = new int[n];
        this.random = random;
        double[] scaled = new double[n];
        int[] small = new int[n];
        int[] large = new int[n];
        int smallCount = 0, largeCount = 0;
        double sum = 0.0;
        for (double p : probabilities) sum += p;
        for (int i = 0; i < n; i++) scaled[i] = probabilities[i] * n / sum;
        for (int i = 0; i < n; i++) {
            if (scaled[i] < 1.0) small[smallCount++] = i;
            else large[largeCount++] = i;
        }
        while (smallCount > 0 && largeCount > 0) {
            int s = small[--smallCount];
            int l = large[--largeCount];
            prob[s] = scaled[s];
            alias[s] = l;
            scaled[l] = (scaled[l] + scaled[s]) - 1.0;
            if (scaled[l] < 1.0) small[smallCount++] = l;
            else large[largeCount++] = l;
        }
        while (largeCount > 0) prob[large[--largeCount]] = 1.0;
        while (smallCount > 0) prob[small[--smallCount]] = 1.0;
    }

    /**
     * Retorna um índice amostrado conforme as probabilidades originais.
     */
    public int sample() {
        int i = random.nextInt(n);
        return random.nextDouble() < prob[i] ? i : alias[i];
    }
}

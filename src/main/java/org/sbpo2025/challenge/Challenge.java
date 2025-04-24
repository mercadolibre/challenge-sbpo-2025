package org.sbpo2025.challenge;

import org.apache.commons.lang3.time.StopWatch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Challenge {

    private List<Map<Integer, Integer>> orders;
    private List<Map<Integer, Integer>> aisles;
    private int nItems;
    private int waveSizeLB;
    private int waveSizeUB;

    public void readInput(String inputFilePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            String line = reader.readLine();
            String[] firstLine = line.split(" ");
            int nOrders = Integer.parseInt(firstLine[0]);
            int nItems = Integer.parseInt(firstLine[1]);
            int nAisles = Integer.parseInt(firstLine[2]);

            // Initialize orders and aisles arrays
            orders = new ArrayList<>(nOrders);
            aisles = new ArrayList<>(nAisles);
            this.nItems = nItems;

            // Read orders
            readItemQuantityPairs(reader, nOrders, orders);

            // Read aisles
            readItemQuantityPairs(reader, nAisles, aisles);

            // Read wave size bounds
            line = reader.readLine();
            String[] bounds = line.split(" ");
            waveSizeLB = Integer.parseInt(bounds[0]);
            waveSizeUB = Integer.parseInt(bounds[1]);

            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading input from " + inputFilePath);
            e.printStackTrace();
        }
    }

    private void readItemQuantityPairs(BufferedReader reader, int nLines, List<Map<Integer, Integer>> orders) throws IOException {
        String line;
        for (int orderIndex = 0; orderIndex < nLines; orderIndex++) {
            line = reader.readLine();
            String[] orderLine = line.split(" ");
            int nOrderItems = Integer.parseInt(orderLine[0]);
            Map<Integer, Integer> orderMap = new HashMap<>();
            for (int k = 0; k < nOrderItems; k++) {
                int itemIndex = Integer.parseInt(orderLine[2 * k + 1]);
                int itemQuantity = Integer.parseInt(orderLine[2 * k + 2]);
                orderMap.put(itemIndex, itemQuantity);
            }
            orders.add(orderMap);
        }
    }

    public void writeOutput(ChallengeSolution challengeSolution, String outputFilePath) {
        if (challengeSolution == null) {
            System.err.println("Solution not found");
            return;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            var orders = challengeSolution.orders();
            var aisles = challengeSolution.aisles();

            // Write the number of orders
            writer.write(String.valueOf(orders.size()));
            writer.newLine();

            // Write each order
            for (int order : orders) {
                writer.write(String.valueOf(order));
                writer.newLine();
            }

            // Write the number of aisles
            writer.write(String.valueOf(aisles.size()));
            writer.newLine();

            // Write each aisle
            for (int aisle : aisles) {
                writer.write(String.valueOf(aisle));
                writer.newLine();
            }

            writer.close();
            System.out.println("Output written to " + outputFilePath);

        } catch (IOException e) {
            System.err.println("Error writing output to " + outputFilePath);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Start the stopwatch to track the running time
        StopWatch stopWatch = StopWatch.createStarted();

        if (args.length != 2) {
            System.out.println("Usage: java -jar target/ChallengeSBPO2025-1.0.jar <inputFilePath> <outputFilePath>");
            return;
        }

        Challenge challenge = new Challenge();
        challenge.readInput(args[0]);
        var challengeSolver = new ChallengeSolver(
                challenge.orders, challenge.aisles, challenge.nItems, challenge.waveSizeLB, challenge.waveSizeUB);
        ChallengeSolution challengeSolution = challengeSolver.solve(stopWatch);

        challenge.writeOutput(challengeSolution, args[1]);
    }
}

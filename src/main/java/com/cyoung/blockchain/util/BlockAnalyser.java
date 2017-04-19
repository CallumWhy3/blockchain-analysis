package com.cyoung.blockchain.util;

import com.cyoung.blockchain.controller.OptionsMenuController;
import com.cyoung.blockchain.domain.BitcoinTransaction;
import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.Transaction;

import java.util.ArrayList;

public class BlockAnalyser {
    public static ArrayList<BitcoinTransaction> anomalousTransactions = new ArrayList<>();

    /**
     * Assess transactions of a block to identify those considered anomalous
     * @param block Block containing transactions to be assessed
     * @return  List of transactions identified as anomalous
     */
    public ArrayList<BitcoinTransaction> calculateAnomalousTransactions(Block block) {
        ArrayList<BitcoinTransaction> allTransactions = new ArrayList<>();
        // Remove coinbase transaction
        int numberOfTransactions = block.getTransactions().size();
        for (Transaction t : block.getTransactions().subList(1, numberOfTransactions)) {
            allTransactions.add(new BitcoinTransaction(t));
        }

        calculateTransactionWeights(allTransactions);
        anomalousTransactions.clear();
        for (BitcoinTransaction t : allTransactions) {
            if (t.getWeight() >= OptionsMenuController.anomalyWeightValue) {
                anomalousTransactions.add(t);
            }
        }
        return anomalousTransactions;
    }

    /**
     * Calculate weight of transactions based on different characteristics
     * @param transactions  List of transactions you want to calculate the weight for
     */
    private void calculateTransactionWeights(ArrayList<BitcoinTransaction> transactions) {
        compareTotalBitcoinsInput(transactions);
        compareTotalNumberOfInputs(transactions);
        compareTotalNumberOfOutputs(transactions);
        compareTotalNumberOfUniqueInputs(transactions);
        compareTotalNumberOfUniqueOutputs(transactions);
    }

    /**
     * Compare total bitcoins input and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalBitcoinsInput(ArrayList<BitcoinTransaction> transactions) {
        long totalBitcoinsInput = 0;
        for (BitcoinTransaction t : transactions) {
            totalBitcoinsInput += t.getTotalBitcoinsInput();
        }

        long averageBitcoinsInput = totalBitcoinsInput / transactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            totalBitcoinsInput = t.getTotalBitcoinsInput();
            double difference = Math.abs(averageBitcoinsInput - totalBitcoinsInput) * 0.00000001;
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.8 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            totalBitcoinsInput = t.getTotalBitcoinsInput();
            double difference = Math.abs(averageBitcoinsInput - totalBitcoinsInput) * 0.00000001;
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total number of inputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfInputs(ArrayList<BitcoinTransaction> transactions) {
        long totalInputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalInputs += t.getTotalNumberOfInputs();
        }

        long averageTotalInputs = totalInputs / transactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            totalInputs = t.getTotalNumberOfInputs();
            double difference = Math.abs(averageTotalInputs - totalInputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            totalInputs = t.getTotalNumberOfInputs();
            double difference = Math.abs(averageTotalInputs - totalInputs);
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total number of outputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfOutputs(ArrayList<BitcoinTransaction> transactions) {
        long totalOutputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalOutputs += t.getTotalNumberOfOutputs();
        }

        long averageTotalOutputs = totalOutputs / transactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            totalOutputs = t.getTotalNumberOfOutputs();
            double difference = Math.abs(averageTotalOutputs - totalOutputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            totalOutputs = t.getTotalNumberOfOutputs();
            double difference = Math.abs(averageTotalOutputs - totalOutputs);
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total unique inputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfUniqueInputs(ArrayList<BitcoinTransaction> transactions) {
        long totalUniqueInputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalUniqueInputs += t.getTotalNumberOfUniqueInputs();
        }

        long averageTotalUniqueInputs = totalUniqueInputs / transactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            totalUniqueInputs = t.getTotalNumberOfUniqueInputs();
            double difference = Math.abs(averageTotalUniqueInputs - totalUniqueInputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            totalUniqueInputs = t.getTotalNumberOfUniqueInputs();
            double difference = Math.abs(averageTotalUniqueInputs - totalUniqueInputs);
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total unique outputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfUniqueOutputs(ArrayList<BitcoinTransaction> transactions) {
        long totalUniqueOutputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalUniqueOutputs += t.getTotalNumberOfUniqueOutputs();
        }

        long averageTotalUniqueOutputs = totalUniqueOutputs / transactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            totalUniqueOutputs = t.getTotalNumberOfUniqueOutputs();
            double difference = Math.abs(averageTotalUniqueOutputs - totalUniqueOutputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            totalUniqueOutputs = t.getTotalNumberOfUniqueOutputs();
            double difference = Math.abs(averageTotalUniqueOutputs - totalUniqueOutputs);
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    public ArrayList<BitcoinTransaction> getAnomalousTransactions() {
        return anomalousTransactions;
    }
}

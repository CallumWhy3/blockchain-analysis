package com.cyoung.blockchain.util;

import com.cyoung.blockchain.domain.BitcoinTransaction;
import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.Transaction;

import java.util.ArrayList;

public class BlockAnalyser {
    private ArrayList<BitcoinTransaction> allTransactions = new ArrayList<>();
    public static ArrayList<BitcoinTransaction> anomalousTransactions = new ArrayList<>();

    public BlockAnalyser(Block block) {
        // Remove coinbase transaction
        int numberOfTransactions = block.getTransactions().size();
        for (Transaction t : block.getTransactions().subList(1, numberOfTransactions)) {
            allTransactions.add(new BitcoinTransaction(t));
        }
    }

    public ArrayList<BitcoinTransaction> calculateAnomalousTransactions() {
        calculateTransactionWeights();
        for (BitcoinTransaction t : allTransactions) {
            if (t.getWeight() >= 0.3) {
                anomalousTransactions.add(t);
            }
        }
        return anomalousTransactions;
    }

    private void calculateTransactionWeights() {
        compareTotalBitcoinsInput();
        compareTotalNumberOfInputs();
        compareTotalNumberOfOutputs();
        compareTotalNumberOfUniqueInputs();
        compareTotalNumberOfUniqueOutputs();
    }

    private void compareTotalBitcoinsInput() {
        long totalBitcoinsInput = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalBitcoinsInput += t.getTotalBitcoinsInput();
        }

        long averageBitcoinsInput = totalBitcoinsInput / allTransactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalBitcoinsInput = t.getTotalBitcoinsInput();
            double difference = Math.abs(averageBitcoinsInput - totalBitcoinsInput) * 0.00000001;
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.8 / largestDifference;
        for (BitcoinTransaction t : allTransactions) {
            totalBitcoinsInput = t.getTotalBitcoinsInput();
            double difference = Math.abs(averageBitcoinsInput - totalBitcoinsInput) * 0.00000001;
            t.addToWeight(difference * weightMultiplier);
        }
    }

    private void compareTotalNumberOfInputs() {
        long totalInputs = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalInputs += t.getTotalNumberOfInputs();
        }

        long averageTotalInputs = totalInputs / allTransactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalInputs = t.getTotalNumberOfInputs();
            double difference = Math.abs(averageTotalInputs - totalInputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / largestDifference;
        for (BitcoinTransaction t : allTransactions) {
            totalInputs = t.getTotalNumberOfInputs();
            double difference = Math.abs(averageTotalInputs - totalInputs);
            t.addToWeight(difference * weightMultiplier);
        }
    }

    private void compareTotalNumberOfOutputs() {
        long totalOutputs = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalOutputs += t.getTotalNumberOfOutputs();
        }

        long averageTotalOutputs = totalOutputs / allTransactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalOutputs = t.getTotalNumberOfOutputs();
            double difference = Math.abs(averageTotalOutputs - totalOutputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / largestDifference;
        for (BitcoinTransaction t : allTransactions) {
            totalOutputs = t.getTotalNumberOfOutputs();
            double difference = Math.abs(averageTotalOutputs - totalOutputs);
            t.addToWeight(difference * weightMultiplier);
        }
    }

    private void compareTotalNumberOfUniqueInputs() {
        long totalUniqueInputs = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalUniqueInputs += t.getTotalNumberOfUniqueInputs();
        }

        long averageTotalUniqueInputs = totalUniqueInputs / allTransactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalUniqueInputs = t.getTotalNumberOfUniqueInputs();
            double difference = Math.abs(averageTotalUniqueInputs - totalUniqueInputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / largestDifference;
        for (BitcoinTransaction t : allTransactions) {
            totalUniqueInputs = t.getTotalNumberOfUniqueInputs();
            double difference = Math.abs(averageTotalUniqueInputs - totalUniqueInputs);
            t.addToWeight(difference * weightMultiplier);
        }
    }

    private void compareTotalNumberOfUniqueOutputs() {
        long totalUniqueOutputs = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalUniqueOutputs += t.getTotalNumberOfUniqueOutputs();
        }

        long averageTotalUniqueOutputs = totalUniqueOutputs / allTransactions.size();
        double largestDifference = 0;
        for (BitcoinTransaction t : allTransactions) {
            totalUniqueOutputs = t.getTotalNumberOfUniqueOutputs();
            double difference = Math.abs(averageTotalUniqueOutputs - totalUniqueOutputs);
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        double weightMultiplier = 0.05 / largestDifference;
        for (BitcoinTransaction t : allTransactions) {
            totalUniqueOutputs = t.getTotalNumberOfUniqueOutputs();
            double difference = Math.abs(averageTotalUniqueOutputs - totalUniqueOutputs);
            t.addToWeight(difference * weightMultiplier);
        }
    }

    public ArrayList<BitcoinTransaction> getAnomalousTransactions() {
        return anomalousTransactions;
    }

    public ArrayList<BitcoinTransaction> getAllTransactions() {
        return allTransactions;
    }
}

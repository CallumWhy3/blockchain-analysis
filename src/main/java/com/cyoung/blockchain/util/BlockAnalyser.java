package com.cyoung.blockchain.util;

import com.cyoung.blockchain.controller.OptionsMenuController;
import com.cyoung.blockchain.object.BitcoinTransaction;
import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class BlockAnalyser {
    public static ArrayList<BitcoinTransaction> anomalousTransactions = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(GraphGenerator.class);

    /**
     * Assess transactions of a block to identify those considered anomalous
     * @param blocks List of blocks containing transactions to be assessed
     * @return  List of transactions identified as anomalous
     */
    public ArrayList<BitcoinTransaction> calculateAnomalousTransactions(List<Block> blocks) {
        ArrayList<BitcoinTransaction> allTransactions = new ArrayList<>();
        // Remove coinbase transaction as it has no inputs and therefore cannot be analysed
        for (Block block : blocks) {
            int numberOfTransactions = block.getTransactions().size();
            for (Transaction t : block.getTransactions().subList(1, numberOfTransactions)) {
                allTransactions.add(new BitcoinTransaction(t));
            }
        }

        // If block contains only 1 coinbase transaction then no anomalous transactions can be found
        if (allTransactions.size() == 0) {
            logger.info("Not enough transactions to analyse");
            return anomalousTransactions;
        }

        // Calculate weight value for each transaction
        calculateTransactionWeights(allTransactions);

        // Create list of all transactions found with weight higher than anomaly threshold value
        anomalousTransactions.clear();
        for (BitcoinTransaction t : allTransactions) {
            if (t.getWeight() >= OptionsMenuController.anomalyWeightValue) {
                anomalousTransactions.add(t);
            }
        }
        logger.info("Transactions analysed: " + allTransactions.size());
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
        compareAverageBitcoinsTransferredPerInput(transactions);
        compareAverageBitcoinsTransferredPerOutput(transactions);
    }

    /**
     * Compare total bitcoins input and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalBitcoinsInput(ArrayList<BitcoinTransaction> transactions) {
        // Calculate total Satoshi input
        long totalSatoshiInput = 0;
        for (BitcoinTransaction t : transactions) {
            totalSatoshiInput += t.getTotalSatoshiInput();
        }

        double averageSatoshiInput = totalSatoshiInput / transactions.size();
        // Calculate largest difference from the average value
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageSatoshiInput - t.getTotalSatoshiInput());
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        // Assign weight to each transaction with the max value of 0.7 being assigned to the one with the
        // largest difference from the average. Using a squared value allows weights to scale exponentially
        double weightMultiplier = 0.7 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageSatoshiInput - t.getTotalSatoshiInput());
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total number of inputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfInputs(ArrayList<BitcoinTransaction> transactions) {
        // Calculate total number of inputs
        long totalInputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalInputs += t.getTotalNumberOfInputs();
        }

        double averageTotalInputs = totalInputs / transactions.size();
        // Calculate largest difference from the average value
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalInputs - t.getTotalNumberOfInputs());
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        // Assign weight to each transaction with the max value of 0.06 being assigned to the one with the
        // largest difference from the average. Using a squared value allows weights to scale exponentially
        double weightMultiplier = 0.06 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalInputs - t.getTotalNumberOfInputs());
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total number of outputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfOutputs(ArrayList<BitcoinTransaction> transactions) {
        // Calculate total number of outputs
        long totalOutputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalOutputs += t.getTotalNumberOfOutputs();
        }

        double averageTotalOutputs = totalOutputs / transactions.size();
        // Calculate largest difference from the average value
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalOutputs - t.getTotalNumberOfOutputs());
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        // Assign weight to each transaction with the max value of 0.06 being assigned to the one with the
        // largest difference from the average. Using a squared value allows weights to scale exponentially
        double weightMultiplier = 0.06 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalOutputs - t.getTotalNumberOfOutputs());
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total unique inputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfUniqueInputs(ArrayList<BitcoinTransaction> transactions) {
        // Calculate total number of unique inputs
        long totalUniqueInputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalUniqueInputs += t.getTotalNumberOfUniqueInputs();
        }

        double averageTotalUniqueInputs = totalUniqueInputs / transactions.size();
        // Calculate largest difference from the average value
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalUniqueInputs - t.getTotalNumberOfUniqueInputs());
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        // Assign weight to each transaction with the max value of 0.06 being assigned to the one with the
        // largest difference from the average. Using a squared value allows weights to scale exponentially
        double weightMultiplier = 0.06 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalUniqueInputs - t.getTotalNumberOfUniqueInputs());
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    /**
     * Compare total unique outputs and assign appropriate weight
     * @param transactions  List of transactions you want to compare
     */
    private void compareTotalNumberOfUniqueOutputs(ArrayList<BitcoinTransaction> transactions) {
        // Calculate total number of unique outputs
        long totalUniqueOutputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalUniqueOutputs += t.getTotalNumberOfUniqueOutputs();
        }

        double averageTotalUniqueOutputs = totalUniqueOutputs / transactions.size();
        // Calculate largest difference from the average value
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalUniqueOutputs - t.getTotalNumberOfUniqueOutputs());
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        // Assign weight to each transaction with the max value of 0.06 being assigned to the one with the
        // largest difference from the average. Using a squared value allows weights to scale exponentially
        double weightMultiplier = 0.06 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageTotalUniqueOutputs - t.getTotalNumberOfUniqueOutputs());
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    private void compareAverageBitcoinsTransferredPerInput(ArrayList<BitcoinTransaction> transactions) {
        // Calculate total Satoshi input
        long totalSatoshiInput = 0;
        for (BitcoinTransaction t : transactions) {
            totalSatoshiInput += t.getTotalSatoshiInput();
        }

        // Calculate total number of inputs
        long totalInputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalInputs += t.getTotalNumberOfInputs();
        }

        double averageSatoshiPerInput = totalSatoshiInput / totalInputs;
        // Calculate largest difference from the average value
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageSatoshiPerInput - t.getAverageSatoshiInput());
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        // Assign weight to each transaction with the max value of 0.06 being assigned to the one with the
        // largest difference from the average. Using a squared value allows weights to scale exponentially
        double weightMultiplier = 0.06 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageSatoshiPerInput - t.getAverageSatoshiInput());
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }

    private void compareAverageBitcoinsTransferredPerOutput(ArrayList<BitcoinTransaction> transactions) {
        // Calculate total Satoshi output
        long totalSatoshiOutput = 0;
        for (BitcoinTransaction t : transactions) {
            totalSatoshiOutput += t.getTotalSatoshiOutput();
        }

        // Calculate total number of outputs
        long totalOutputs = 0;
        for (BitcoinTransaction t : transactions) {
            totalOutputs += t.getTotalNumberOfOutputs();
        }

        double averageSatoshiPerOutput = totalSatoshiOutput / totalOutputs;
        // Calculate largest difference from the average value
        double largestDifference = 0;
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageSatoshiPerOutput - t.getAverageSatoshiOutput());
            if (difference > largestDifference) {
                largestDifference = difference;
            }
        }

        // Assign weight to each transaction with the max value of 0.06 being assigned to the one with the
        // largest difference from the average. Using a squared value allows weights to scale exponentially
        double weightMultiplier = 0.06 / Math.pow(largestDifference, 2);
        for (BitcoinTransaction t : transactions) {
            double difference = Math.abs(averageSatoshiPerOutput - t.getAverageSatoshiOutput());
            t.addToWeight(Math.pow(difference, 2) * weightMultiplier);
        }
    }
}

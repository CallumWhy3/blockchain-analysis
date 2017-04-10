package com.cyoung.blockchain.domain;

import info.blockchain.api.blockexplorer.Input;
import info.blockchain.api.blockexplorer.Output;
import info.blockchain.api.blockexplorer.Transaction;

import java.util.*;

public class BitcoinTransaction {
    private Transaction transaction;
    private String hash;
    private String relayedBy;
    private List<Input> inputs;
    private List<Output> outputs;
    private long size;
    private long blockHeight;
    private long index;
    private long lockTime;
    private long time;
    private int version;
    private double weight = 0;

    public BitcoinTransaction(Transaction transaction) {
        this.transaction = transaction;
        hash = transaction.getHash();
        relayedBy = transaction.getRelayedBy();
        inputs = transaction.getInputs();
        outputs = transaction.getOutputs();
        size = transaction.getSize();
        blockHeight = transaction.getBlockHeight();
        index = transaction.getIndex();
        lockTime = transaction.getLockTime();
        time = transaction.getTime();
        version = transaction.getVersion();
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getHash() {
        return hash;
    }

    public String getRelayedBy() {
        return relayedBy;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public int getTotalNumberOfInputs() {
        return inputs.size();
    }

    public long getTotalBitcoinsInput() {
        long totalBitcoinsInput = 0;
        for (Input i : inputs) {
            totalBitcoinsInput += i.getPreviousOutput().getValue();
        }
        return totalBitcoinsInput;
    }

    public int getTotalNumberOfUniqueInputs() {
        Set<String> uniqueInputs = new HashSet<>();
        for (Input i : inputs) {
            uniqueInputs.add(i.getPreviousOutput().getAddress());
        }
        return uniqueInputs.size();
    }

    public long getLargestInput() {
        ArrayList<Long> values = new ArrayList<>();
        for (Input i : inputs) {
            values.add(i.getPreviousOutput().getValue());
        }
        return Collections.max(values);
    }

    public long getSmallestInput() {
        ArrayList<Long> values = new ArrayList<>();
        for (Input i : inputs) {
            values.add(i.getPreviousOutput().getValue());
        }
        return Collections.min(values);
    }

    public long getAverageInput() {
        long values = 0;
        for (Input i : inputs) {
            values += i.getPreviousOutput().getValue();
        }
        return values / getTotalNumberOfInputs();
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public int getTotalNumberOfOutputs() {
        return outputs.size();
    }

    public long getTotalBitcoinsOutput() {
        long totalBitcoinsOutput = 0;
        for (Output o : outputs) {
            totalBitcoinsOutput += o.getValue();
        }
        return totalBitcoinsOutput;
    }

    public int getTotalNumberOfUniqueOutputs() {
        Set<String> uniqueOutputs = new HashSet<>();
        for (Output o : outputs) {
            uniqueOutputs.add(o.getAddress());
        }
        return uniqueOutputs.size();
    }

    public long getLargestOutput() {
        ArrayList<Long> values = new ArrayList<>();
        for (Output o : outputs) {
            values.add(o.getValue());
        }
        return Collections.max(values);
    }

    public long getSmallestOutput() {
        ArrayList<Long> values = new ArrayList<>();
        for (Output o : outputs) {
            values.add(o.getValue());
        }
        return Collections.min(values);
    }

    public long getAverageOutput() {
        long values = 0;
        for (Output o : outputs) {
            values += o.getValue();
        }
        return values / getTotalNumberOfOutputs();
    }

    public long getTransactionFee() {
        return getTotalBitcoinsInput() - getTotalBitcoinsOutput();
    }

    public long getSize() {
        return size;
    }

    public long getBlockHeight() {
        return blockHeight;
    }

    public long getIndex() {
        return index;
    }

    public long getLockTime() {
        return lockTime;
    }

    public long getTime() {
        return time;
    }

    public int getVersion() {
        return version;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void addToWeight(double addWeight) {
        weight += addWeight;
    }
}

package com.cyoung.blockchain.domain;

import info.blockchain.api.blockexplorer.Input;
import info.blockchain.api.blockexplorer.Output;
import info.blockchain.api.blockexplorer.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class BitcoinTransactionTest {
    private BitcoinTransaction bitcoinTransaction;

    @Before
    public void createTestTransaction() {
        List<Input> inputs = new ArrayList<>();
        List<Output> outputs = new ArrayList<>();

        Output output1 = new Output(1, 1, "address1", 1, "output1", true);
        Output output2 = new Output(2, 2, "address2", 2, "output2", true);
        Output output3 = new Output(3, 3, "address3", 3, "output3", true);
        Output output4 = new Output(4, 4, "address4", 4, "output4", true);
        Output output5 = new Output(5, 5, "address5", 5, "output5", true);
        outputs.addAll(Arrays.asList(output1, output2, output3, output4, output5));

        Input input1 = new Input(output1, 1, "input1");
        Input input2 = new Input(output2, 2, "input2");
        Input input3 = new Input(output3, 3, "input3");
        inputs.addAll(Arrays.asList(input1, input2, input3));

        Transaction transaction = new Transaction(false, 10, 1, 1, "", "hash", 1, 1, 5173, inputs, outputs);
        bitcoinTransaction = new BitcoinTransaction(transaction);
    }

    @Test
    public void shouldGetTotalNumberOfInputs() {
        int totalInputs = bitcoinTransaction.getTotalNumberOfInputs();
        assertEquals(3, totalInputs);
    }

    @Test
    public void shouldGetTotalBitcoinsInput() {
        long totalBitcoinsInput = bitcoinTransaction.getTotalBitcoinsInput();
        assertEquals(6, totalBitcoinsInput);
    }

    @Test
    public void shouldGetTotalUniqueInputs() {
        long totalUniqueInputs = bitcoinTransaction.getTotalNumberOfUniqueInputs();
        assertEquals(3, totalUniqueInputs);
    }

    @Test
    public void shouldGetLargestInput() {
        long largestInput = bitcoinTransaction.getLargestInput();
        assertEquals(3, largestInput);
    }

    @Test
    public void shouldGetSmallestInput() {
        long smallestInput = bitcoinTransaction.getSmallestInput();
        assertEquals(1, smallestInput);
    }

    @Test
    public void shouldGetAverageInput() {
        double averageInput = bitcoinTransaction.getAverageInput();
        assertEquals(2.0, averageInput);
    }

    @Test
    public void shouldGetTotalNumberOfOutputs() {
        int totalOutputs = bitcoinTransaction.getTotalNumberOfOutputs();
        assertEquals(5, totalOutputs);
    }

    @Test
    public void shouldGetTotalBitcoinsOutput() {
        long totalBitcoinsOutput = bitcoinTransaction.getTotalBitcoinsOutput();
        assertEquals(15, totalBitcoinsOutput);
    }

    @Test
    public void shouldGetTotalUniqueOutputs() {
        long totalUniqueOutputs = bitcoinTransaction.getTotalNumberOfUniqueOutputs();
        assertEquals(5, totalUniqueOutputs);
    }

    @Test
    public void shouldGetLargestOutput() {
        long largestOutput = bitcoinTransaction.getLargestOutput();
        assertEquals(5, largestOutput);
    }

    @Test
    public void shouldGetSmallestOutput() {
        long smallestOutput = bitcoinTransaction.getSmallestOutput();
        assertEquals(1, smallestOutput);
    }

    @Test
    public void shouldGetAverageOutput() {
        double averageOutput = bitcoinTransaction.getAverageOutput();
        assertEquals(3.0, averageOutput);
    }
}

package com.cyoung.blockchain.object;

public class ReoccurringInputRow {

    private String address;
    private int occurrences;

    // Object used to populate table of reoccurring inputs
    public ReoccurringInputRow(String address, int occurrences) {
        this.address = address;
        this.occurrences = occurrences;
    }

    public String getAddress() {
        return address;
    }

    public int getOccurrences() {
        return occurrences;
    }
}

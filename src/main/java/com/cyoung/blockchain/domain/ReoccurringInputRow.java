package com.cyoung.blockchain.domain;

public class ReoccurringInputRow {

    private String address;
    private int occurrences;

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

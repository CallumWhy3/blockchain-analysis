package com.cyoung.blockchain.util;

import info.blockchain.api.APIException;
import info.blockchain.api.blockexplorer.*;
import org.neo4j.driver.v1.Session;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnomalyVisualiser {

    private Session session;
    private double totalBitcoinsSent = 0;
    private int totalBitcoinSenders = 0;

    public AnomalyVisualiser(Session session) {
        this.session = session;
    }

    public void produceAnomalyGraph(File graphFile, String blockHash) {
        String graphFileAsString = fileToString(graphFile);
        Set<String> anomalousStructures = getUniqueStructuresFromString(graphFileAsString);
        session.run("MATCH (n) DETACH DELETE n");
        try {
            graphAnomalousTransactions(anomalousStructures, blockHash);
        } catch (APIException | IOException e) {
            e.printStackTrace();
        }
    }

    private String fileToString(File file) {
        String graphFilePath = file.getPath();
        try {
            BufferedReader br = new BufferedReader(new FileReader(graphFilePath));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

            String fileAsString = sb.toString();
            br.close();
            return fileAsString;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Set<String> getUniqueStructuresFromString(String graphFileAsString) {
        Set<String> structures = new HashSet<String>();
        Pattern pattern = Pattern.compile("(XP\\nv\\s\\d+\\stransaction\\n)+(v\\s\\d+\\sinput\\n)+(v\\s\\d+\\soutput\\n)+(d\\s\\d+\\s\\d+\\sinput\\n)+(d\\s\\d+\\s\\d+\\soutput\\n)+");
        Matcher matcher = pattern.matcher(graphFileAsString);

        String match;
        while (matcher.find()) {
            match = matcher.group().replace("XP\n", "");
            structures.add(match);
        }

        return structures;
    }

    private void graphAnomalousTransactions(Set<String> anomalousStructures, String blockHash) throws APIException, IOException {
        BlockExplorer blockExplorer = new BlockExplorer();
        Block block = blockExplorer.getBlock(blockHash);
        ArrayList<Transaction> anomalousTransactions = new ArrayList<>();

        for (Transaction transaction : block.getTransactions().subList(1, 300)) {
            totalBitcoinsSent += getTotalBitcoinsTansferred(transaction.getInputs());
            totalBitcoinSenders++;

            for (String structure : anomalousStructures) {
                if (transactionMatchesStructure(transaction, structure)) {
                    anomalousTransactions.add(transaction);
                }
            }
        }

        double averageBitcoinsSent = totalBitcoinsSent / totalBitcoinSenders;
        for (Transaction transaction : anomalousTransactions) {
            session.run("CREATE(t:Transaction {hash:'" + transaction.getHash() + "', time:'" + transaction.getTime() + "', index:'" + transaction.getIndex() + "', size:'" + transaction.getSize() + "'})");
            graphInputs(transaction);
            graphOutputs(transaction);
        }
        System.out.println("Average Bitcoins sent per transaction: " + averageBitcoinsSent);
    }

    private double getTotalBitcoinsTansferred(List<Input> inputs) {
        double total = 0;
        for (Input input : inputs) {
            total += input.getPreviousOutput().getValue();
        }
        return total;
    }

    private boolean transactionMatchesStructure(Transaction transaction, String structure) {
        Pattern pattern;
        Matcher matcher;

        int transactionInputs = transaction.getInputs().size();
        int transactionOutputs = transaction.getOutputs().size();

        int structureInputs = 0;
        int structureOutputs = 0;

        pattern = Pattern.compile("(v\\s\\d+\\sinput)");
        matcher = pattern.matcher(structure);
        while (matcher.find()) {
            structureInputs++;
        }

        pattern = Pattern.compile("(v\\s\\d+\\soutput)");
        matcher = pattern.matcher(structure);
        while (matcher.find()) {
            structureOutputs++;
        }

        return transactionInputs == structureInputs && transactionOutputs == structureOutputs;
    }

    private void graphInputs(Transaction transaction) {
        for (Input i : transaction.getInputs()) {
            String inputAddress = i.getPreviousOutput().getAddress();
            double inputValue = i.getPreviousOutput().getValue() * 0.00000001;
            session.run("MERGE(i:Input {name:'" + inputAddress + "'})");
            session.run("MATCH(i:Input {name:'" + inputAddress + "'}),(t:Transaction {hash:'" + transaction.getHash() + "'}) CREATE(i)-[:INPUT{value: " + inputValue + "}]->(t)");
        }
    }

    private void graphOutputs(Transaction transaction) {
        for (Output o : transaction.getOutputs()) {
            String outputHash = o.getAddress();
            double outputValue = o.getValue() * 0.00000001;
            session.run("MERGE(o:Output {name:'" + outputHash + "'})");
            session.run("MATCH(t:Transaction {hash:'" + transaction.getHash() + "'}),(o:Output {name:'" + outputHash + "'}) CREATE(t)-[:OUTPUT{value: " + outputValue + "}]->(o)");
            session.run("MATCH(o:Output {name:'" + outputHash + "'}),(i:Input {name:'" + outputHash + "'}) MERGE(o)-[:MATCH]->(i)");
        }
    }
}

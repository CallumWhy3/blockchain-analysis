package com.cyoung.blockchain.util;

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
    private int transactionNodeId = 1;
    private int userNodeID = 2;


    public AnomalyVisualiser(Session session) {
        this.session = session;
    }

    public void produceGraphFromGraphFile(File file) {
        String graphFileAsString = fileToString(file);
        Set<String> structures = getStructuresFromString(graphFileAsString);
        session.run("MATCH (n) DETACH DELETE n");

        for(String structure : structures) {
            graphStructureFromString(structure);
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

    private Set<String> getStructuresFromString(String graphFileAsString) {
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

    private void graphStructureFromString(String structure) {
        Pattern pattern;
        Matcher matcher;
        String match;

        List<String> transactions = new ArrayList<String>();
        List<String> inputs = new ArrayList<String>();
        List<String> outputs = new ArrayList<String>();

        pattern = Pattern.compile("(v\\s\\d+\\stransaction)");
        matcher = pattern.matcher(structure);
        while (matcher.find()) {
            match = matcher.group();
            transactions.add(match);
        }

        pattern = Pattern.compile("(v\\s\\d+\\sinput)");
        matcher = pattern.matcher(structure);
        while (matcher.find()) {
            match = matcher.group();
            inputs.add(match);
        }

        pattern = Pattern.compile("(v\\s\\d+\\soutput)");
        matcher = pattern.matcher(structure);
        while (matcher.find()) {
            match = matcher.group();
            outputs.add(match);
        }

        for(String transaction : transactions) {
            session.run("MERGE(t:Transaction{id:'" + transactionNodeId + "'})");
        }

        for(String input : inputs) {
            session.run("MERGE(i:Input{id:'" + userNodeID + "'})");
            session.run("MATCH(i:Input {id:'" + userNodeID + "'}),(t:Transaction {id:'" + transactionNodeId + "'}) CREATE(i)-[:INPUT]->(t)");
            userNodeID++;
        }

        for(String output: outputs) {
            session.run("MERGE(o:Output{id:'" + userNodeID + "'})");
            session.run("MATCH(t:Transaction {id:'" + transactionNodeId + "'}),(o:Output {id:'" + userNodeID + "'}) CREATE(t)-[:OUTPUT]->(o)");
            userNodeID++;
        }
        transactionNodeId++;
    }
}

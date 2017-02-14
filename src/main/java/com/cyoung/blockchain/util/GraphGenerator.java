package com.cyoung.blockchain.util;

import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.blockexplorer.Input;
import info.blockchain.api.blockexplorer.Output;
import info.blockchain.api.blockexplorer.Transaction;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class GraphGenerator {
    private Session session;
    private int subdueCounter;
    private ArrayList<String> subdueVertex;
    private ArrayList<String> subdueEdges;
    private GraphFileBuilder graphFileBuilder;
    private static final Logger logger = LoggerFactory.getLogger(GraphGenerator.class);

    public GraphGenerator(Session session) throws IOException {
        this.session = session;
        graphFileBuilder = new GraphFileBuilder(PropertyLoader.LoadProperty("graphFileOutputLocation"));
        session.run("MATCH (n) DETACH DELETE n");
    }

    public void graphTransactionByHash(String transHash) throws Exception {
        subdueVertex = new ArrayList<>();
        subdueEdges = new ArrayList<>();
        subdueCounter = 1;

        BlockExplorer blockExplorer = new BlockExplorer();
        Transaction trans = blockExplorer.getTransaction(transHash);
        session.run("CREATE(t:Transaction {hash:'" + transHash + "'})");
        subdueVertex.add("v " + subdueCounter + " transaction");
        subdueCounter++;
        graphInputs(trans);
        graphOutputs(trans);
        logger.info("Transaction " + transHash + " graphed successfully");
        try {
            graphFileBuilder.buildSubdueGraphFile(subdueVertex, subdueEdges);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void graphInputs(Transaction trans) {
        try {
            for (Input i : trans.getInputs()) {
                String inputAddress = i.getPreviousOutput().getAddress();
                double inputValue = i.getPreviousOutput().getValue() * 0.00000001;
                session.run("MERGE(i:Input {name:'" + inputAddress + "'})");
                session.run("MATCH(i:Input {name:'" + inputAddress + "'}),(t:Transaction {hash:'" + trans.getHash() + "'}) CREATE(i)-[:INPUT{value: " + inputValue + "}]->(t)");
                subdueVertex.add("v " + subdueCounter + " input");
                subdueEdges.add("d " + subdueCounter + " 1 input");
                subdueCounter++;
            }
        } catch (NullPointerException e) {
            session.run("MERGE(i:Input {name:'COINBASE'})");
            session.run("MATCH(i:Input {name:'COINBASE'}),(t:Transaction {hash:'" + trans.getHash() + "'}) CREATE(i)-[:INPUT]->(t)");
            subdueVertex.add("v " + subdueCounter + " input");
            subdueEdges.add("d " + subdueCounter + " 1 input");
            subdueCounter++;
        }
    }

    private void graphOutputs(Transaction trans){
        for (Output o : trans.getOutputs()) {
            String outputHash = o.getAddress();
            double outputValue = o.getValue() * 0.00000001;
            session.run("MERGE(o:Output {name:'" + outputHash + "'})");
            session.run("MATCH(t:Transaction {hash:'" + trans.getHash() + "'}),(o:Output {name:'" + outputHash + "'}) CREATE(t)-[:OUTPUT{value: " + outputValue + "}]->(o)");
            subdueVertex.add("v " + subdueCounter + " output");
            subdueEdges.add("d 1 " + subdueCounter + " output");
            subdueCounter++;
            session.run("MATCH(o:Output {name:'" + outputHash + "'}),(i:Input {name:'" + outputHash + "'}) MERGE(o)-[:MATCH]->(i)");
        }
    }

    public void closeSubdueGraphBuilderStream() throws IOException {
        graphFileBuilder.closeStream();
    }
}

package com.cyoung.blockchain.util;

import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.blockexplorer.Input;
import info.blockchain.api.blockexplorer.Output;
import info.blockchain.api.blockexplorer.Transaction;
import org.neo4j.driver.v1.Session;

import java.io.IOException;
import java.util.ArrayList;

public class GraphGenerator {
    private Session session;
    private int subdueCounter;
    private ArrayList<String> subdueVertex;
    private ArrayList<String> subdueEdges;
    private SubdueGraphBuilder subdueGraphBuilder;

    public GraphGenerator(Session session) throws IOException {
        this.session = session;
        subdueGraphBuilder = new SubdueGraphBuilder("/Users/callum/Projects/blockchain-analysis");
    }

    public void graphTransactionByHash(String transHash) throws Exception {
        subdueVertex = new ArrayList<String>();
        subdueEdges = new ArrayList<String>();
        subdueCounter = 1;

        BlockExplorer blockExplorer = new BlockExplorer();
        Transaction trans = blockExplorer.getTransaction(transHash);
        session.run("CREATE(t:Transaction {hash:'" + trans.getHash() + "'})");
        subdueVertex.add("v " + subdueCounter + " transaction");
        subdueCounter++;
        graphInputs(trans);
        graphOutputs(trans);
        try {
            subdueGraphBuilder.buildSubdueGraphFile(subdueVertex, subdueEdges);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void graphInputs(Transaction trans) {
        for (Input i : trans.getInputs()){
            String inputAddress = i.getPreviousOutput().getAddress();
            session.run("MERGE(i:Input {name:'" + inputAddress + "'})");
            session.run("MATCH(i:Input {name:'" + inputAddress + "'}),(t:Transaction {hash:'" + trans.getHash() + "'}) CREATE(i)-[:INPUT]->(t)");
            subdueVertex.add("v " + subdueCounter + " input");
            subdueEdges.add("e " + subdueCounter + " 1 input");
            subdueCounter++;
        }
    }

    private void graphOutputs(Transaction trans){
        for (Output o : trans.getOutputs()) {
            String outputHash = o.getAddress();
            session.run("MERGE(o:Output {name:'" + outputHash + "'})");
            session.run("MATCH(t:Transaction {hash:'" + trans.getHash() + "'}),(o:Output {name:'" + outputHash + "'}) CREATE(t)-[:OUTPUT]->(o)");
            subdueVertex.add("v " + subdueCounter + " output");
            subdueEdges.add("e 1 " + subdueCounter + " output");
            subdueCounter++;
        }
    }

    public void closeSubdueGraphBuilderStream() throws IOException {
        subdueGraphBuilder.closeStream();
    }
}

package com.cyoung.blockchain.util;

import info.blockchain.api.blockexplorer.Input;
import info.blockchain.api.blockexplorer.Output;
import info.blockchain.api.blockexplorer.Transaction;
import org.neo4j.driver.v1.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphGenerator {
    private Session session;
    private static final Logger logger = LoggerFactory.getLogger(GraphGenerator.class);

    public GraphGenerator(Session session) {
        this.session = session;
        // Delete all nodes and relationships in session
        session.run("MATCH (n) DETACH DELETE n");
    }

    /**
     * Graph bitcoin coinbase transaction in Neo4j
     * @param trans Coinbase transaction you want to graph
     */
    public void graphCoinbaseTransaction(Transaction trans) {
        // Create node for transaction
        session.run("CREATE(t:Transaction {hash:'" + trans.getHash() + "', time:'" + trans.getTime() + "', index:'" + trans.getIndex() + "', size:'" + trans.getSize() + "'})");

        // Coinbase transactions have no input value so use the value from its only output instead
        Output coinbaseInput = trans.getOutputs().get(0);
        String inputAddress = "COINBASE" + trans.getBlockHeight();
        double inputValue = convertSatoshiToBitcoin(coinbaseInput.getValue());
        session.run("CREATE(i:Input {address:'" + inputAddress + "', name:'" + inputAddress + "'})");
        session.run("MATCH(i:Input {address:'" + inputAddress + "'}),(t:Transaction {hash:'" + trans.getHash() + "'}) CREATE(i)-[:INPUT{value: " + inputValue + "}]->(t)");

        graphOutputs(trans);
        logger.info("Coinbase transaction " + trans.getHash() + " graphed successfully");
    }

    /**
     * Graph bitcoin transaction in Neo4j
     * @param trans Transaction you want to graph
     */
    public void graphTransaction(Transaction trans) {
        // Create node for transaction
        session.run("CREATE(t:Transaction {hash:'" + trans.getHash() + "', time:'" + trans.getTime() + "', index:'" + trans.getIndex() + "', size:'" + trans.getSize() + "'})");
        graphInputs(trans);
        graphOutputs(trans);
        logger.info("Transaction " + trans.getHash() + " graphed successfully");
    }

    /**
     * Graph inputs of bitcoin transaction
     * @param trans Transaction containing inputs you want to graph
     */
    private void graphInputs(Transaction trans) {
        for (Input i : trans.getInputs()) {
            String inputAddress = i.getPreviousOutput().getAddress();
            double inputValue = convertSatoshiToBitcoin(i.getPreviousOutput().getValue());
            // Create node for input
            session.run("MERGE(i:Input {address:'" + inputAddress + "', name:'" + inputAddress + "'})");
            // Create relationship linking input node to transaction node
            session.run("MATCH(i:Input {address:'" + inputAddress + "'}),(t:Transaction {hash:'" + trans.getHash() + "'}) CREATE(i)-[:INPUT{value: " + inputValue + "}]->(t)");
            // Create MATCH relationship with any existing output nodes with the same address
            session.run("MATCH(i:Input {address:'" + inputAddress + "'}),(o:Output {address:'" + inputAddress + "'}) MERGE(i)-[:MATCH]->(o)");
        }
    }

    /**
     * Graph outputs of bitcoin transaction
     * @param trans Transaction containing outputs you want to graph
     */
    private void graphOutputs(Transaction trans) {
        for (Output o : trans.getOutputs()) {
            String outputAddress = o.getAddress();
            double outputValue = convertSatoshiToBitcoin(o.getValue());
            // Create node for output
            session.run("MERGE(o:Output {address:'" + outputAddress + "', name:'" + outputAddress + "'})");
            // Create relationship linking transaction node to output node
            session.run("MATCH(t:Transaction {hash:'" + trans.getHash() + "'}),(o:Output {address:'" + outputAddress + "'}) CREATE(t)-[:OUTPUT{value: " + outputValue + "}]->(o)");
            // Create MATCH relationship with any existing input nodes with the same address
            session.run("MATCH(o:Output {address:'" + outputAddress + "'}),(i:Input {address:'" + outputAddress + "'}) MERGE(o)-[:MATCH]->(i)");
        }
    }

    /**
     * Convert Satoshi value to Bitcoin format
     */
    private double convertSatoshiToBitcoin(double satoshiValue) {
        // 1 Satoshi = 0.00000001 Bitcoin
        return satoshiValue * 0.00000001;
    }
}

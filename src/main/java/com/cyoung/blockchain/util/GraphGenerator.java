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
        session.run("MATCH (n) DETACH DELETE n");
    }

    public void graphTransaction(Transaction trans) throws Exception {
        String transHash = trans.getHash();
        session.run("CREATE(t:Transaction {hash:'" + transHash + "', time:'" + trans.getTime() + "', index:'" + trans.getIndex() + "', size:'" + trans.getSize() + "'})");
        graphInputs(trans);
        graphOutputs(trans);
        logger.info("Transaction " + transHash + " graphed successfully");
    }

    private void graphInputs(Transaction trans) {
        for (Input i : trans.getInputs()) {
            String inputAddress = i.getPreviousOutput().getAddress();
            double inputValue = i.getPreviousOutput().getValue() * 0.00000001;
            session.run("MERGE(i:Input {name:'" + inputAddress + "'})");
            session.run("MATCH(i:Input {name:'" + inputAddress + "'}),(t:Transaction {hash:'" + trans.getHash() + "'}) CREATE(i)-[:INPUT{value: " + inputValue + "}]->(t)");
        }
    }

    private void graphOutputs(Transaction trans) {
        for (Output o : trans.getOutputs()) {
            String outputHash = o.getAddress();
            double outputValue = o.getValue() * 0.00000001;
            session.run("MERGE(o:Output {name:'" + outputHash + "'})");
            session.run("MATCH(t:Transaction {hash:'" + trans.getHash() + "'}),(o:Output {name:'" + outputHash + "'}) CREATE(t)-[:OUTPUT{value: " + outputValue + "}]->(o)");
            session.run("MATCH(o:Output {name:'" + outputHash + "'}),(i:Input {name:'" + outputHash + "'}) MERGE(o)-[:MATCH]->(i)");
        }
    }
}

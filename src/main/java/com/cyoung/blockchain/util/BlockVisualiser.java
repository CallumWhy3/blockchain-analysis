package com.cyoung.blockchain.util;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Transaction;
import org.neo4j.driver.v1.Session;

public class BlockVisualiser {
    private GraphGenerator graphGenerator;

    public BlockVisualiser(Session session) throws Exception {
        graphGenerator = new GraphGenerator(session);
    }

    public void produceGraphFromBlock(Block block) throws Exception {
        for (Transaction transaction : block.getTransactions().subList(1, 30)) {
            String transHash = transaction.getHashAsString();
            try {
                graphGenerator.graphTransactionByHash(transHash);
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }
        graphGenerator.closeSubdueGraphBuilderStream();
    }
}

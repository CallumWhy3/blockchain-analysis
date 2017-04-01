package com.cyoung.blockchain.util;

import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.blockexplorer.Transaction;
import org.neo4j.driver.v1.Session;

public class BlockVisualiser {
    private GraphGenerator graphGenerator;

    public BlockVisualiser(Session session) throws Exception {
        graphGenerator = new GraphGenerator(session);
    }

    public void produceGraphFromBlockHash(String hash) throws Exception {
        BlockExplorer blockExplorer = new BlockExplorer();
        Block block = blockExplorer.getBlock(hash);
        for (Transaction transaction : block.getTransactions().subList(0, 100)) {

            try {
                graphGenerator.graphTransactionByHash(transaction);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        graphGenerator.closeSubdueGraphBuilderStream();
    }
}

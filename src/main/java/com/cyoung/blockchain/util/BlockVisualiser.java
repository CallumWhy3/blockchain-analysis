package com.cyoung.blockchain.util;

import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.blockexplorer.Transaction;
import org.neo4j.driver.v1.Session;

public class BlockVisualiser {

    /**
     * Produce graph of transactions found in block using Neo4j
     * @param session   Neo4j session you want to create the graph in
     * @param hash  Hash of block you want to graph
     * @throws Exception    Block cannot be found with specified hash
     */
    public void produceGraphFromBlockHash(Session session, String hash) throws Exception {
        GraphGenerator graphGenerator = new GraphGenerator(session);
        BlockExplorer blockExplorer = new BlockExplorer();
        Block block = blockExplorer.getBlock(hash);
        for (Transaction transaction : block.getTransactions().subList(1, 300)) {
            graphGenerator.graphTransaction(transaction);
        }
    }
}

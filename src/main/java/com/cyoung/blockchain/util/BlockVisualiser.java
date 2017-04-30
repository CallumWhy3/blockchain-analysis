package com.cyoung.blockchain.util;

import info.blockchain.api.blockexplorer.Block;
import info.blockchain.api.blockexplorer.BlockExplorer;
import info.blockchain.api.blockexplorer.Transaction;
import org.neo4j.driver.v1.Session;

import java.util.List;

public class BlockVisualiser {

    /**
     * Produce graph of transactions found in block using Neo4j
     * @param session   Neo4j session you want to create the graph in
     * @param hashes    List of hashes for blocks you want to graph
     * @throws Exception    Block cannot be found with specified hash
     */
    public void produceGraphFromBlockHashes(Session session, List<String> hashes) throws Exception {
        GraphGenerator graphGenerator = new GraphGenerator(session);
        BlockExplorer blockExplorer = new BlockExplorer();
        for (String hash : hashes) {
            Block block = blockExplorer.getBlock(hash);

            // Graph coinbase transaction
            graphGenerator.graphCoinbaseTransaction(block.getTransactions().get(0));

            // Graph other transactions
            for (Transaction transaction : block.getTransactions().subList(1, block.getTransactions().size())) {
                graphGenerator.graphTransaction(transaction);
            }
        }
    }
}

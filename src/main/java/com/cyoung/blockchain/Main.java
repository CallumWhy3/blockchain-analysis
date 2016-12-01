package com.cyoung.blockchain;

import com.cyoung.blockchain.util.TransactionGrapher;
import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String args[]) throws Exception{
        NetworkParameters params = MainNetParams.get();
        Context context = new Context(params);

        // Use specific .dat file for now
        List<File> blockFiles = new ArrayList<File>();
        File block277 = new File("/Users/callum/Documents/FinalYearProject/blk00277.dat");
        blockFiles.add(block277);

        BlockFileLoader blockFileLoader = new BlockFileLoader(params, blockFiles);
        // Only use first block for now as it's easier to set up the program using a smaller number of transactions
        Block block = blockFileLoader.next();

        Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "blockchain"));
        Session session = driver.session();

        TransactionGrapher transactionGrapher = new TransactionGrapher(session);
        for (Transaction transaction : block.getTransactions().subList(0,30)) {
            String transHash = transaction.getHashAsString();
            try {
                transactionGrapher.graphTransactionByHash(transHash);
            } catch(NullPointerException e) {
                logger.debug(e.toString());
            }
        }
        transactionGrapher.closeSubdueGraphBuilderStream();
        session.close();
        driver.close();
    }
}

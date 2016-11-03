import org.bitcoinj.core.*;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.utils.BlockFileLoader;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String args[]) throws Exception{
        NetworkParameters params = MainNetParams.get();
        Context context = new Context(params);

        // Use specific .dat file for now
        List<File> blockFiles = new ArrayList<File>();
        File block590 = new File("C:\\Users\\Callum\\Desktop\\FinalYearProject\\Blockchain\\blk00590.dat");
        blockFiles.add(block590);

        BlockFileLoader blockFileLoader = new BlockFileLoader(params, blockFiles);
        // Only use first block for now as it's easier to set up the program using a smaller number of transactions
        Block block = blockFileLoader.next();

        // Create node in Neo4J for each input address
        Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();
        for (Transaction transaction : block.getTransactions()) {
            System.out.println("Transaction hash: " + transaction.getHashAsString());
            for (TransactionInput input : transaction.getInputs()) {
                System.out.println("Transaction input addresses: ");
                try {
                    System.out.println("- " + input.getFromAddress());
                    session.run( "CREATE (a:TransactionInputAddress {address:'" + input.getFromAddress() + "'})" );
                } catch (ScriptException e) {
                    System.out.println(e);
                }
            }
            System.out.println();
        }
        session.close();
        driver.close();
    }
}

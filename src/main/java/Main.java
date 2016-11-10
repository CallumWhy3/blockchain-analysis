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
        File block590 = new File("C:\\Users\\Callum\\Desktop\\FinalYearProject\\Blockchain\\blk00277.dat");
        blockFiles.add(block590);

        BlockFileLoader blockFileLoader = new BlockFileLoader(params, blockFiles);
        // Only use first block for now as it's easier to set up the program using a smaller number of transactions
        Block block = blockFileLoader.next();

        Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();

        String inputName, outputName;
        int inputNumber = 1;
        int outputNumber = 1;
        for (Transaction transaction : block.getTransactions().subList(0,30)) {
            String transHash = transaction.getHashAsString();
             session.run("CREATE(t:Transaction {hash:'" + transHash + "'})");

            for (TransactionInput input : transaction.getInputs()) {
                inputName = "input" + inputNumber;
                session.run("CREATE(i:Input {name:'" + inputName + "'})");
                session.run("MATCH(i:Input {name:'" + inputName + "'}),(t:Transaction {hash:'" + transHash + "'}) CREATE(i)-[:INPUT]->(t)");
                inputNumber++;
            }

            for (TransactionOutput output : transaction.getOutputs()) {
                outputName = "output" + outputNumber;
                session.run("CREATE(o:Output {name:'" + outputName + "'})");
                session.run("MATCH(t:Transaction {hash:'" + transHash + "'}),(o:Output {name:'" + outputName + "'}) CREATE(t)-[:OUTPUT]->(o)");
                outputNumber++;
            }
        }
        session.close();
        driver.close();
    }
}

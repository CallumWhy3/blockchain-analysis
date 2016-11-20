import info.blockchain.api.blockexplorer.*;
import org.neo4j.driver.v1.Session;

public class TransactionGrapher {
    private Transaction trans;
    private Session session;

    public TransactionGrapher(Session session) throws Exception {
        this.session = session;
    }

    public void graphTransactionByHash(String transHash) throws Exception {
        BlockExplorer blockExplorer = new BlockExplorer();
        trans = blockExplorer.getTransaction(transHash);
        session.run("CREATE(t:Transaction {hash:'" + trans.getHash() + "'})");
        graphInputs();
        graphOutputs();
    }

    private void graphInputs(){
        for (Input i : trans.getInputs()){
            String inputAddress = i.getPreviousOutput().getAddress();
            session.run("MERGE(i:Input {name:'" + inputAddress + "'})");
            session.run("MATCH(i:Input {name:'" + inputAddress + "'}),(t:Transaction {hash:'" + trans.getHash() + "'}) CREATE(i)-[:INPUT]->(t)");
        }
    }

    private void graphOutputs(){
        for (Output o : trans.getOutputs()) {
            String outputHash = o.getAddress();
            session.run("MERGE(o:Output {name:'" + outputHash + "'})");
            session.run("MATCH(t:Transaction {hash:'" + trans.getHash() + "'}),(o:Output {name:'" + outputHash + "'}) CREATE(t)-[:OUTPUT]->(o)");
        }
    }
}

package com.cyoung.blockchain.util;

import info.blockchain.api.blockexplorer.Input;
import info.blockchain.api.blockexplorer.Output;
import info.blockchain.api.blockexplorer.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;

import java.util.*;

import static junit.framework.TestCase.assertEquals;

public class GraphGeneratorTest {
    private GraphGenerator graphGenerator;
    private Session session;
    private Transaction validTransaction;

    @Before
    public void initialiseValues() {
        // Create graph generator using Neo4j session
        String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
        String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");
        Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
        session = driver.session();
        graphGenerator = new GraphGenerator(session);

        // Create inputs for transaction
        Output output1 = new Output(1, 100000000, "address1", 1, "output1", true);
        Output output2 = new Output(2, 200000000, "address2", 2, "output2", true);
        Input input1 = new Input(output1, 1, "input1");
        Input input2 = new Input(output2, 2, "input2");
        List<Input> inputs = new ArrayList<>();
        inputs.addAll(Arrays.asList(input1, input2));

        // Create outputs for transaction
        Output output3 = new Output(3, 300000000, "address3", 3, "output3", true);
        Output output4 = new Output(4, 400000000, "address4", 4, "output4", true);
        List<Output> outputs = new ArrayList<>();
        outputs.addAll(Arrays.asList(output3, output4));

        // Create transaction
        validTransaction = new Transaction(false, 10, 1, 1, "", "hash", 1, 1, 5173, inputs, outputs);
    }

    @Test
    public void shouldCreateNeo4jGraphOfCoinbaseTransaction() {
        // Coinbase transactions have no inputs
        List<Input> inputs = new ArrayList<>();

        // Create output for coinbase transaction
        Output output = new Output(1, 100000000, "address1", 1, "output1", true);
        List<Output> outputs = new ArrayList<>();
        outputs.addAll(Collections.singletonList(output));

        // Create coinbase transaction
        Transaction coinbaseTransaction = new Transaction(false, 10, 1, 1, "", "hash", 1, 1, 5173, inputs, outputs);


        graphGenerator.graphCoinbaseTransaction(coinbaseTransaction);
        StatementResult result;

        // Validate 1 transaction node, 1 input node and 1 output node has been created
        result = session.run("MATCH (t:Transaction) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (i:Input) RETURN i");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (o:Output) RETURN o");
        assertEquals(1, result.list().size());

        // Validate coinbase transaction node hash is equal to 'hash'
        result = session.run("MATCH (t:Transaction) RETURN t.hash");
        assertEquals("hash", result.next().values().get(0).asString());

        // Validate coinbase transaction node time is equal to '1'
        result = session.run("MATCH (t:Transaction) RETURN t.time");
        assertEquals("1", result.next().values().get(0).asString());

        // Validate coinbase transaction node index is equal to '1'
        result = session.run("MATCH (t:Transaction) RETURN t.index");
        assertEquals("1", result.next().values().get(0).asString());

        // Validate 1 input node with address equal to 'COINBASE' concatenated with block height
        result = session.run("MATCH (i:Input) RETURN i.address");
        List<Record> inputNameList = result.list();
        assertEquals(1, inputNameList.size());
        assertEquals("COINBASE10", inputNameList.get(0).values().get(0).asString());

        // Validate 1 output node with address 'address1'
        result = session.run("MATCH (o:Output) RETURN o.address");
        List<Record> outputNameList = result.list();
        assertEquals(1, outputNameList.size());
        assertEquals("address1", outputNameList.get(0).values().get(0).asString());

        // Validate 1 input relationship with values 1
        result = session.run("MATCH (i:Input)-[n:INPUT]->(t:Transaction) RETURN n.value");
        List<Record> inputValueList = result.list();
        assertEquals(1, inputValueList.size());
        assertEquals((float)1.0, inputValueList.get(0).values().get(0).asFloat());

        // Validate 1 output relationship with value 1
        result = session.run("MATCH (t:Transaction)-[n:OUTPUT]->(o:Output) RETURN n.value");
        List<Record> outputValueList = result.list();
        assertEquals(1, outputValueList.size());
        assertEquals((float)1.0, outputValueList.get(0).values().get(0).asFloat());
    }

    @Test
    public void shouldCreateNeo4jGraphOfNormalTransaction() {
        graphGenerator.graphTransaction(validTransaction);
        StatementResult result;

        // Validate 1 transaction node, 2 input nodes and 2 output nodes have been created
        result = session.run("MATCH (t:Transaction) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (i:Input) RETURN i");
        assertEquals(2, result.list().size());
        result = session.run("MATCH (o:Output) RETURN o");
        assertEquals(2, result.list().size());

        // Validate transaction node hash is equal to 'hash'
        result = session.run("MATCH (t:Transaction) RETURN t.hash");
        assertEquals("hash", result.next().values().get(0).asString());

        // Validate transaction node time is equal to '1'
        result = session.run("MATCH (t:Transaction) RETURN t.time");
        assertEquals("1", result.next().values().get(0).asString());

        // Validate transaction node index is equal to '1'
        result = session.run("MATCH (t:Transaction) RETURN t.index");
        assertEquals("1", result.next().values().get(0).asString());

        // Validate 2 input nodes with address address1 and address2
        result = session.run("MATCH (i:Input) RETURN i.address");
        List<Record> inputNameList = result.list();
        assertEquals(2, inputNameList.size());
        assertEquals("address1", inputNameList.get(0).values().get(0).asString());
        assertEquals("address2", inputNameList.get(1).values().get(0).asString());

        // Validate 2 output nodes with addresses address3 and address4
        result = session.run("MATCH (o:Output) RETURN o.address");
        List<Record> outputNameList = result.list();
        assertEquals(2, outputNameList.size());
        assertEquals("address3", outputNameList.get(0).values().get(0).asString());
        assertEquals("address4", outputNameList.get(1).values().get(0).asString());

        // Validate 2 input relationships with values 1 and 2
        result = session.run("MATCH (i:Input)-[n:INPUT]->(t:Transaction) RETURN n");
        List<Record> inputValueList = result.list();
        assertEquals(2, inputValueList.size());
        result = session.run("MATCH (i:Input)-[n:INPUT{value:1}]->(t:Transaction) RETURN n");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (i:Input)-[n:INPUT{value:2}]->(t:Transaction) RETURN n");
        assertEquals(1, result.list().size());

        // Validate 2 output relationships with values 3 and 4
        result = session.run("MATCH (t:Transaction)-[n:OUTPUT]->(o:Output) RETURN n");
        List<Record> outputValueList = result.list();
        assertEquals(2, outputValueList.size());
        result = session.run("MATCH (t:Transaction)-[n:OUTPUT{value:3}]->(o:Output) RETURN n");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction)-[n:OUTPUT{value:4}]->(o:Output) RETURN n");
        assertEquals(1, result.list().size());
    }
}

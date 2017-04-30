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
    public void shouldCreateValidNeo4jGraphWith1Transaction() {
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

        // Validate 2 input nodes with names address1 and address2
        result = session.run("MATCH (i:Input) RETURN i.name");
        List<Record> inputNameList = result.list();
        assertEquals(2, inputNameList.size());
        assertEquals("address1", inputNameList.get(0).values().get(0).asString());
        assertEquals("address2", inputNameList.get(1).values().get(0).asString());

        // Validate 2 output nodes with names address3 and address4
        result = session.run("MATCH (o:Output) RETURN o.name");
        List<Record> outputNameList = result.list();
        assertEquals(2, outputNameList.size());
        assertEquals("address3", outputNameList.get(0).values().get(0).asString());
        assertEquals("address4", outputNameList.get(1).values().get(0).asString());

        // Validate 2 input relationships with values 1 and 2
        result = session.run("MATCH (i:Input)-[n:INPUT]->(t:Transaction) RETURN n.value");
        List<Record> inputValueList = result.list();
        assertEquals(2, inputValueList.size());
        assertEquals((float)2.0, inputValueList.get(0).values().get(0).asFloat());
        assertEquals((float)1.0, inputValueList.get(1).values().get(0).asFloat());

        // Validate 2 output relationships with values 3 and 4
        result = session.run("MATCH (t:Transaction)-[n:OUTPUT]->(o:Output) RETURN n.value");
        List<Record> outputValueList = result.list();
        assertEquals(2, outputValueList.size());
        assertEquals((float)4.0, outputValueList.get(0).values().get(0).asFloat());
        assertEquals((float)3.0, outputValueList.get(1).values().get(0).asFloat());
    }
}

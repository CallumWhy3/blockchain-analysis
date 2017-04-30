package com.cyoung.blockchain.util;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class BlockVisualiserTest {
    // Hash for block at height 150000, known to contain 10 transactions (including coinbase transaction)
    private String blockHash = "0000000000000a3290f20e75860d505ce0e948a1d1d846bec7e39015d242884b";
    private BlockVisualiser blockVisualiser;
    private Session session;

    @Before
    public void initialiseValues() {
        // Create Neo4j session
        String neo4jUsername = PropertyLoader.LoadProperty("neo4jUsername");
        String neo4jPassword = PropertyLoader.LoadProperty("neo4jPassword");
        Driver driver = GraphDatabase.driver("bolt://localhost", AuthTokens.basic(neo4jUsername, neo4jPassword));
        session = driver.session();

        // Create block visualiser
        blockVisualiser = new BlockVisualiser();
    }

    @Test
    public void shouldVisualiseValidBlock() throws Exception {
        // Produce graph of existing block
        List<String> blockHashes = new ArrayList<>();
        blockHashes.add(blockHash);
        blockVisualiser.produceGraphFromBlockHashes(session, blockHashes);

        StatementResult result;

        // Validate 10 transactions nodes created
        result = session.run("MATCH (t:Transaction) RETURN t");
        assertEquals(10, result.list().size());

        // Validate 10 transaction node hash values are correct
        result = session.run("MATCH (t:Transaction {hash:'bcdc61cbecf6137eec5c8ad4047fcdc36710e77e404b17378a33ae605920afe1'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'f7f4c281ee20ab8d1b00734b92b60582b922211a7e470accd147c6d70c9714a3'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'b5f6e3b217fa7f6d58081b5d2a9a6607eebd889ed2c470191b2a45e0dcb98eb0'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'4206f171f06913b1d40597edcaf75780559231fb504c49ba85a4a9ae949e8b95'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'a1a6ad6ff321c76496a89b6a4eb9bcfb76b9e068b677d5c7d427c51ca08c273d'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'89c82039452c14a9314b5834e5d2b9241b1fdccdb6e4f4f68e49015540faaf95'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'25c6a1f8c0b5be2bee1e8dd3478b4ec8f54bbc3742eaf90bfb5afd46cf217ad9'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'57eef4da5edacc1247e71d3a93ed2ccaae69c302612e414f98abf8db0b671eae'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'8d30eb0f3e65b8d8a9f26f6f73fc5aafa5c0372f9bb38aa38dd4c9dd1933e090'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'13e3167d46334600b59a5aa286dd02147ac33e64bfc2e188e1f0c0a442182584'}) RETURN t");
        assertEquals(1, result.list().size());
    }
}

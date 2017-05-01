package com.cyoung.blockchain.util;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class BlockVisualiserTest {
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
    public void shouldVisualiseSingleBlock() throws Exception {
        // Produce graph of single block
        List<String> blockHashes = new ArrayList<>();
        // Hash for block at height 150000, known to contain 10 transactions in total
        blockHashes.add("0000000000000a3290f20e75860d505ce0e948a1d1d846bec7e39015d242884b");
        blockVisualiser.produceGraphFromBlockHashes(session, blockHashes);

        StatementResult result;

        // Validate 10 transaction nodes created
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

    @Test
    public void shouldVisualiseMultipleBlocks() throws Exception {
        // Produce graph of multiple blocks
        List<String> blockHashes = new ArrayList<>();
        // Hash for blocks from height 80000 to 80002, known to contain 7 transactions in total
        blockHashes.add("000000000043a8c0fd1d6f726790caa2a406010d19efd2780db27bdbbd93baf6");
        blockHashes.add("00000000000036312a44ab7711afa46f475913fbd9727cf508ed4af3bc933d16");
        blockHashes.add("0000000000242548cf2ae995356d8c3e45103f04d17a6bb4e0e7d89ec45d8045");
        blockVisualiser.produceGraphFromBlockHashes(session, blockHashes);

        StatementResult result;

        // Validate 7 transaction nodes created
        result = session.run("MATCH (t:Transaction) RETURN t");
        assertEquals(7, result.list().size());

        // Validate 7 transaction node hash values are correct
        result = session.run("MATCH (t:Transaction {hash:'c06fbab289f723c6261d3030ddb6be121f7d2508d77862bb1e484f5cd7f92b25'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'5a4ebf66822b0b2d56bd9dc64ece0bc38ee7844a23ff1d7320a88c5fdb2ad3e2'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'fd859b8a041591c4a759fc5e0a1eba3776739eef2066823a15fa3c2f2f0eb15e'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'10b6fe7a18750cd43c847ed1d82daf8f3ee19f885da2b770ecfa22e961a5b829'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'73496b488e2fccace327a81c6887ca08c3551c42f9adfe3984104390859bd794'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'57a6a3e225d2f67595c4a8462bd607a902a367b839f75df0dc92ad365ad4f8fc'}) RETURN t");
        assertEquals(1, result.list().size());
        result = session.run("MATCH (t:Transaction {hash:'a82bcbade7f2ab98a0b9208dae2e5fa6b44f5e37f8403ec8f74ca63c4d6189d2'}) RETURN t");
        assertEquals(1, result.list().size());
    }
}

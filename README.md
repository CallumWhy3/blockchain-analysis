**Requirements**  
You wil need a couple of things before using the program:  
- [Neo4j](https://neo4j.com/): a graph database program  
- A block hash or .dat block file to feed into the program. Block hashes can be found by using an online block explorer such as [blockexplorer.com](https://blockexplorer.com/). Alternativly block files in .dat format can be obtained by downloading the blockchain (or at least part of it). To do this I would suggest using [Bitcoin Core](https://bitcoin.org/en/bitcoin-core/) but there are many options available.  

**Configuration**  
Before runnig the program you must edit config.properties, which can be found in src/main/resources. There are 2 values to set:  
`neo4jUsername` - Name used to log in to Neo4j (default is 'neo4j')  
`neo4jPassword` - Password used to log in to Neo4j (default is also 'neo4j')  

**Running the program**  
Once you have installed Neo4j and set your configuration fields you are ready to run the program. First open Neo4j and start the server, once its up and running you can connect to http://localhost:7474/ in your brower of choice where you will be able to query the database (if you are running Neo4j for the first time there may be some additional setup required but Neo4j will guide you through it). Once you have done this all you need to do is execute the main method in your IDE of choice. After that you can use the GUI to interact with the program.  

**Example use**  
The main menu consists of 2 buttons, 'start' and 'options'. The options menu allows you to adjust the anomaly weight threshold value, which determines the weight required for a transaction to be considered anomalous, this is set to 0.3 by default. If you are happy with this value then click the 'start' button and you will be taken to the 'Block visualiser' page. From here click the '...' button and select a valid .dat file, or if you want to use a block hash click the 'Change input type' button, paste in your block hash and then click the 'Find' button. You will then be able to click the 'Produce graph' button. Once this process completes you should be able to view the graph on Neo4j by running a cypher query such as `MATCH (n) RETURN n`, which simply returns all nodes and relationships.  

Next if you go back to the program and click the 'Analyse' button you will be brought to a new screen titled 'Block analyser'. From here you can analyse the transactions found in the block from the previous page. The transactions will be assessed by the application and assigned a weight based on their different characteristics. Once they have been analysed the transactions that were identified as anomalous will be listed in the text area.  

After getting these results click the 'Visualise' button. This will take you to a page titled 'Anomaly visualiser'. From here if you click the 'Visualise' button you will be able to see the anomalous transactions represented in Neo4j.  

Finally if you click the 'Analyse' button you will be taken to the 'Anomaly analyser'. This page allows you to get an overview of the anomalous transactions identified by the program.  

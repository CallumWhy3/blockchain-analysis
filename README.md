**Operating system**  
This Application has been developed and tested on macOS, whilst there are no OS specific requirements it should be noted that additional steps may be needed if you wish to use Linux or Windows.  

**Requirements**  
You wil need the following before using the program:  
- [Neo4j](https://neo4j.com/): a graph database program  
- A block hash or .dat block file to feed into the program. Block hashes can be found by using an online block explorer such as [blockexplorer.com](https://blockexplorer.com/). Alternativly block files in .dat format can be obtained by downloading the blockchain (or at least part of it). To do this I would suggest using [Bitcoin Core](https://bitcoin.org/en/bitcoin-core/) but there are many options available.  

**Configuration**  
Before running the program you must edit config.properties, which can be found in src/main/resources. There are 2 values to set:  
`neo4jUsername` - Name used to log in to Neo4j (default is 'neo4j')  
`neo4jPassword` - Password used to log in to Neo4j (default is also 'neo4j')  

**Running the program**  
Once you have installed Neo4j and set your configuration fields you are ready to run the program. First open Neo4j and start the server, once its up and running you can connect to http://localhost:7474/ in your brower of choice, where you will be able to query the database (if you are running Neo4j for the first time there may be some additional setup required but Neo4j will guide you through it). Once you have done this all you need to do is execute the main method in your IDE of choice. After that you can use the GUI to interact with the program.  

**Example use**  
The main menu consists of 3 buttons 'start', 'options' and 'Live Anomaly Detection'. The options menu allows you to adjust the anomaly weight threshold value, which determines the weight required for a transaction to be considered anomalous, this is set to 0.05 by default. If you are happy with this value then click the 'start' button and you will be taken to the 'Block visualiser' page.  

You must select at least 1 block in this section to continue, but you can also select multiple blocks if you would like to analyse a larger data set. If you are planning on using multiple blocks it is recommended that you select blocks that are of similar height to get the best results when analysing, as the value of Bitcoins can vary greatly depending on when the transactions took place. If you want to add a block file then click the '...' button, select a valid .dat file and click the 'Add block' button. If you want to use a block hash click the 'Change input type' button, paste in your block hash, click the 'Find' button and then click the 'Add block' button. You will then be able to click either the 'Produce graph' or 'Analyse' button. The 'Produce graph' button will create a Neo4j graoh of all the transactions found in your selected blocks which can be viewed in the browser once the process is complete. The 'Analyse' button takes you to a new page where the transactions in the selected blocks can be analysed.  

After clicking the 'Analyse' button you will be brought to a new screen titled 'Block analyser'. From here you can analyse the transactions found in the blocks from the previous page. The transactions will be assessed by the application and assigned a weight based on their different characteristics. Once they have been analysed the transactions that were identified as anomalous will be listed in the text area.  

After getting these results click the 'Visualise' button. This will take you to a page titled 'Anomaly visualiser'. From here if you can either click the 'Visualise' button which will graph the anomalous transactions in Neo4j, or you can skip this step and click the 'Analyse' button which will take you to the next page.  

The final page is titled 'Anomaly analyser'. This page allows you to get an overview of the anomalous transactions identified by the program and gives statistics based on what was found. From here you can click the 'Main menu' button to be returned to the main menu.  

The main menu also contains a 'Live Anomaly Detection' button, if you click it you will be taken to a page that polls the blockchain every 15 seconds for the most recent block. The block will then be analysed and any anomalous transactions will be highlighted in a table. The time estimation bar will give a rough guide on when the next block will be added to the blockchain, on average a new block is added every 10 minutes. The 'Visualise' button allows you to visualise the anomalous transactions in Neo4j.

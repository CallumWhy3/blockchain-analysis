**Requirements**  
You wil need a couple of things before using the program:  
- [Neo4j](https://neo4j.com/): a graph database program
- [Subdue](https://github.com/gromgull/subdue): a graph based data-mining program
- You will also need some .dat block files to feed into the program, which you can get by downloading the blockchain (or at least part of it). To do this I would suggest using [Bitcoin Core](https://bitcoin.org/en/bitcoin-core/) but there are many options available.  

**Configuration**  
Before runnig the program you must edit config.properties, which can be found in src/main/resources. There are 3 values to set:  
`graphFileOutputDirectory` - This specifies the output directory for the graph file that can be read by Subdue  
`neo4jUsername` - Name used to log in to Neo4j (default is 'neo4j')  
`neo4jPassword` - Password used to log in to Neo4j (default is also 'neo4j')  
`subdueLoaction` - Directory that holds the subdue executable

**Running the program**  
Once you have installed subdue and set your configuration fields you are ready to run the program. To do this you need to run the main method. After that you can use the GUI to interact with the program.  

**Example use**  
Launch the program and Neo4j, then click the 'Start' button. From the first screen select a .dat file and click the 'Produce graph' button. Once this process completes you should be able to view the graph on Neo4j by running a cypher query such as `MATCH (n) RETURN n`, which simply returns all nodes and relationships.  

Next if you go back to program and click the 'Analyse' button you will be brought to a new screen titled 'Graph analyser'. From here you can analyse the graph you just made using Subdue. The program will automatically load the .g file that was created along with your graph but you can select a different file if you want. Finally if you click the 'Execute subdue' button and wait for the process to finish, you will see the 3 most common structures output to the text area in the middle of the screen which will be in the format of vertexes and edges. If one of the results says 'No match at index #' then the result subdue found didn't contain at least one transaction, input and output.  

After getting the Subdue results you will then be able to click a 'Remove common cases button'. This will take the Subdue results and remove them from the previously selected .g file, leaving the uncommon cases.

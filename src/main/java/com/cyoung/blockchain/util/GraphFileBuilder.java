package com.cyoung.blockchain.util;

import java.io.*;
import java.util.ArrayList;

class GraphFileBuilder {
    private PrintWriter out;

    GraphFileBuilder(String directory) throws IOException {
        File graphFile = new File(directory + "/subdueGraph.g");
        out = new PrintWriter(graphFile);
    }

    void buildSubdueGraphFile(ArrayList<String> vertexArray, ArrayList<String> edgeArray) throws IOException {
        out.println("XP");
        for(int i = 0; i < vertexArray.size(); i++){
            out.println(vertexArray.get(i));
        }
        for(int i = 0; i < edgeArray.size(); i++){
            out.println(edgeArray.get(i));
        }
        out.println();
    }

    void closeStream() throws IOException {
        out.close();
    }
}

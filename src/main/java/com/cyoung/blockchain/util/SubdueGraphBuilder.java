package com.cyoung.blockchain.util;

import java.io.*;
import java.util.ArrayList;

class SubdueGraphBuilder {
    private PrintWriter out;

    SubdueGraphBuilder(String directory) throws IOException {
        File graphFile = new File(directory + "/subdueGraph.g");
        out = new PrintWriter(graphFile);
    }

    void buildSubdueGraphFile(ArrayList<String> vertexArray, ArrayList<String> edgeArray) throws IOException {
        out.println("XP");
        for(int i = 0; i < vertexArray.size(); i++){
            out.write(vertexArray.get(i) + "\n");
        }
        for(int i = 0; i < edgeArray.size(); i++){
            out.write(edgeArray.get(i) + "\n");
        }
        out.println();
    }

    void closeStream() throws IOException {
        out.close();
    }
}

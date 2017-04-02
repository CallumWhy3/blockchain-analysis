package com.cyoung.blockchain.util;

import java.io.*;
import java.util.ArrayList;

class GraphFileBuilder {
    private PrintWriter out;

    GraphFileBuilder(String directory) throws IOException {
        File graphFile = new File(directory);
        out = new PrintWriter(graphFile);
    }

    void buildSubdueGraphFile(ArrayList<String> vertexes, ArrayList<String> edges) throws IOException {
        out.println("XP");
        for (int i = 0; i < vertexes.size(); i++) {
            out.println(vertexes.get(i));
        }
        for (int i = 0; i < edges.size(); i++) {
            out.println(edges.get(i));
        }
        out.println();
    }

    void closeStream() throws IOException {
        out.close();
    }
}

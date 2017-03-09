package com.cyoung.blockchain.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class GraphFileBuilderTest {
    private GraphFileBuilder graphFileBuilder;
    private ArrayList vertexes;
    private ArrayList edges;

    @Before
    public void createTestGraphFile() throws IOException {
        graphFileBuilder = new GraphFileBuilder("subdueGraphTest.g");
    }

    @After
    public void deleteTestGraphFile() throws IOException {
        Path path = Paths.get("subdueGraphTest.g");
        Files.delete(path);
    }

    @Test
    public void shouldBuildValidGraphFileWithOneGraph() throws IOException {

        vertexes = new ArrayList<>(Arrays.asList(
                "v 1 transaction",
                "v 2 input",
                "v 3 output"));

        edges = new ArrayList<>(Arrays.asList(
                "d 2 1 input",
                "d 1 3 output"));

        graphFileBuilder.buildSubdueGraphFile(vertexes, edges);
        graphFileBuilder.closeStream();

        String fileAsString = getFileAsString("subdueGraphTest.g");
        assertEquals("Should build valid graph file",
                "XP\n" +
                        "v 1 transaction\n" +
                        "v 2 input\n" +
                        "v 3 output\n" +
                        "d 2 1 input\n" +
                        "d 1 3 output\n" +
                        "\n",
                        fileAsString);
    }

    @Test
    public void shouldBuildValidGraphFileWithMultipleGraphs() throws IOException {

        vertexes = new ArrayList<>(Arrays.asList(
                "v 1 transaction",
                "v 2 input",
                "v 3 output"));

        edges = new ArrayList<>(Arrays.asList(
                "d 2 1 input",
                "d 1 3 output"));

        graphFileBuilder.buildSubdueGraphFile(vertexes, edges);

        vertexes = new ArrayList<>(Arrays.asList(
                "v 1 transaction",
                "v 2 input",
                "v 3 input",
                "v 4 output"));

        edges = new ArrayList<>(Arrays.asList(
                "d 2 1 input",
                "d 3 1 input",
                "d 1 4 output"));

        graphFileBuilder.buildSubdueGraphFile(vertexes, edges);

        vertexes = new ArrayList<>(Arrays.asList(
                "v 1 transaction",
                "v 2 input",
                "v 3 output",
                "v 4 output"));

        edges = new ArrayList<>(Arrays.asList(
                "d 2 1 input",
                "d 1 3 output",
                "d 1 4 output"));

        graphFileBuilder.buildSubdueGraphFile(vertexes, edges);

        graphFileBuilder.closeStream();

        String fileAsString = getFileAsString("subdueGraphTest.g");
        assertEquals("Should build valid graph file",
                "XP\n" +
                        "v 1 transaction\n" +
                        "v 2 input\n" +
                        "v 3 output\n" +
                        "d 2 1 input\n" +
                        "d 1 3 output\n" +
                        "\n" +
                        "XP\n" +
                        "v 1 transaction\n" +
                        "v 2 input\n" +
                        "v 3 input\n" +
                        "v 4 output\n" +
                        "d 2 1 input\n" +
                        "d 3 1 input\n" +
                        "d 1 4 output\n" +
                        "\n" +
                        "XP\n" +
                        "v 1 transaction\n" +
                        "v 2 input\n" +
                        "v 3 output\n" +
                        "v 4 output\n" +
                        "d 2 1 input\n" +
                        "d 1 3 output\n" +
                        "d 1 4 output\n" +
                        "\n",
                fileAsString);
    }

    private String getFileAsString(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }

            return sb.toString();
        } finally {
            br.close();
        }
    }
}

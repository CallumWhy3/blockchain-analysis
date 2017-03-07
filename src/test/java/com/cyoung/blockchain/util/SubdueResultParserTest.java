package com.cyoung.blockchain.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubdueResultParserTest {
    private String subdueResult;
    private SubdueResultParser testParser;

    @Test
    public void shouldFindThreeValidResults() {
        subdueResult = "SUBDUE 5.2.2\n" +
                "\n" +
                "Parameters:\n" +
                "  Input file..................... ../../../Documents/FinalYearProject/subdueGraph.g\n" +
                "  Predefined substructure file... none\n" +
                "  Output file.................... none\n" +
                "  Beam width..................... 4\n" +
                "  Compress....................... false\n" +
                "  Evaluation method.............. MDL\n" +
                "  'e' edges directed............. true\n" +
                "  Incremental.................... false\n" +
                "  Iterations..................... 1\n" +
                "  Limit.......................... 288\n" +
                "  Minimum size of substructures.. 1\n" +
                "  Maximum size of substructures.. 726\n" +
                "  Number of best substructures... 3\n" +
                "  Output level................... 2\n" +
                "  Allow overlapping instances.... false\n" +
                "  Prune.......................... false\n" +
                "  Threshold...................... 0.000000\n" +
                "  Value-based queue.............. false\n" +
                "  Recursion...................... false\n" +
                "\n" +
                "Read 149 total positive graphs\n" +
                "\n" +
                "149 positive graphs: 726 vertices, 577 edges, 9657 bits\n" +
                "3 unique labels\n" +
                "\n" +
                "3 initial substructures\n" +
                "\n" +
                "Best 3 substructures:\n" +
                "\n" +
                "(1) Substructure: value = 2.82916, pos instances = 135, neg instances = 0\n" +
                "  Graph(4v,3e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 output\n" +
                "    v 4 output\n" +
                "    d 2 1 input\n" +
                "    d 1 3 output\n" +
                "    d 1 4 output\n" +
                "\n" +
                "(2) Substructure: value = 1.77588, pos instances = 149, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 output\n" +
                "    d 2 1 input\n" +
                "    d 1 3 output\n" +
                "\n" +
                "(3) Substructure: value = 1.68573, pos instances = 135, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 input\n" +
                "    v 4 output\n" +
                "    d 2 1 input\n" +
                "    d 3 1 input\n" +
                "    d 1 4 output\n" +
                "\n" +
                "\n" +
                "SUBDUE done (elapsed CPU time =    0.10 seconds).";

        testParser = new SubdueResultParser(subdueResult);
        assertEquals("Should parse first subdue result", testParser.getResult(1),
                        "v 1 transaction\n" +
                        "    v 2 input\n" +
                        "    v 3 output\n" +
                        "    v 4 output\n" +
                        "    d 2 1 input\n" +
                        "    d 1 3 output\n" +
                        "    d 1 4 output");

        assertEquals("Should parse second subdue result", testParser.getResult(2),
                "v 1 transaction\n" +
                        "    v 2 input\n" +
                        "    v 3 output\n" +
                        "    d 2 1 input\n" +
                        "    d 1 3 output");

        assertEquals("Should parse third subdue result", testParser.getResult(3),
                "v 1 transaction\n" +
                        "    v 2 input\n" +
                        "    v 3 input\n" +
                        "    v 4 output\n" +
                        "    d 2 1 input\n" +
                        "    d 3 1 input\n" +
                        "    d 1 4 output");
    }

    @Test
    public void shouldFindTwoValidResults() {
        subdueResult = "SUBDUE 5.2.2\n" +
                "\n" +
                "Parameters:\n" +
                "  Input file..................... ../../../Documents/FinalYearProject/subdueGraph.g\n" +
                "  Predefined substructure file... none\n" +
                "  Output file.................... none\n" +
                "  Beam width..................... 4\n" +
                "  Compress....................... false\n" +
                "  Evaluation method.............. MDL\n" +
                "  'e' edges directed............. true\n" +
                "  Incremental.................... false\n" +
                "  Iterations..................... 1\n" +
                "  Limit.......................... 288\n" +
                "  Minimum size of substructures.. 1\n" +
                "  Maximum size of substructures.. 726\n" +
                "  Number of best substructures... 3\n" +
                "  Output level................... 2\n" +
                "  Allow overlapping instances.... false\n" +
                "  Prune.......................... false\n" +
                "  Threshold...................... 0.000000\n" +
                "  Value-based queue.............. false\n" +
                "  Recursion...................... false\n" +
                "\n" +
                "Read 149 total positive graphs\n" +
                "\n" +
                "149 positive graphs: 726 vertices, 577 edges, 9657 bits\n" +
                "3 unique labels\n" +
                "\n" +
                "3 initial substructures\n" +
                "\n" +
                "Best 3 substructures:\n" +
                "\n" +
                "(1) Substructure: value = 2.82916, pos instances = 135, neg instances = 0\n" +
                "  Graph(4v,3e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 output\n" +
                "    v 4 output\n" +
                "    d 2 1 input\n" +
                "    d 1 3 output\n" +
                "    d 1 4 output\n" +
                "\n" +
                "(2) Substructure: value = 1.77588, pos instances = 149, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 output\n" +
                "    d 2 1 input\n" +
                "    d 1 3 output\n" +
                "\n" +
                "(3) Substructure: value = 1.68573, pos instances = 135, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 input\n" +
                "    d 2 1 input\n" +
                "    d 3 1 input\n" +
                "\n" +
                "\n" +
                "SUBDUE done (elapsed CPU time =    0.10 seconds).";

        testParser = new SubdueResultParser(subdueResult);

        assertEquals("Should parse first subdue result", testParser.getResult(1),
                "v 1 transaction\n" +
                        "    v 2 input\n" +
                        "    v 3 output\n" +
                        "    v 4 output\n" +
                        "    d 2 1 input\n" +
                        "    d 1 3 output\n" +
                        "    d 1 4 output");

        assertEquals("Should parse second subdue result", testParser.getResult(2),
                "v 1 transaction\n" +
                        "    v 2 input\n" +
                        "    v 3 output\n" +
                        "    d 2 1 input\n" +
                        "    d 1 3 output");

        assertEquals("Should parse third subdue result", testParser.getResult(3),
                "No match at index 3");
    }

    @Test
    public void shouldFindOneValidResult() {
        subdueResult = "SUBDUE 5.2.2\n" +
                "\n" +
                "Parameters:\n" +
                "  Input file..................... ../../../Documents/FinalYearProject/subdueGraph.g\n" +
                "  Predefined substructure file... none\n" +
                "  Output file.................... none\n" +
                "  Beam width..................... 4\n" +
                "  Compress....................... false\n" +
                "  Evaluation method.............. MDL\n" +
                "  'e' edges directed............. true\n" +
                "  Incremental.................... false\n" +
                "  Iterations..................... 1\n" +
                "  Limit.......................... 288\n" +
                "  Minimum size of substructures.. 1\n" +
                "  Maximum size of substructures.. 726\n" +
                "  Number of best substructures... 3\n" +
                "  Output level................... 2\n" +
                "  Allow overlapping instances.... false\n" +
                "  Prune.......................... false\n" +
                "  Threshold...................... 0.000000\n" +
                "  Value-based queue.............. false\n" +
                "  Recursion...................... false\n" +
                "\n" +
                "Read 149 total positive graphs\n" +
                "\n" +
                "149 positive graphs: 726 vertices, 577 edges, 9657 bits\n" +
                "3 unique labels\n" +
                "\n" +
                "3 initial substructures\n" +
                "\n" +
                "Best 3 substructures:\n" +
                "\n" +
                "(1) Substructure: value = 2.82916, pos instances = 135, neg instances = 0\n" +
                "  Graph(4v,3e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 output\n" +
                "    v 4 output\n" +
                "    d 2 1 input\n" +
                "    d 1 3 output\n" +
                "    d 1 4 output\n" +
                "\n" +
                "(2) Substructure: value = 1.77588, pos instances = 149, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 output\n" +
                "    v 3 output\n" +
                "    d 1 2 output\n" +
                "    d 1 3 output\n" +
                "\n" +
                "(3) Substructure: value = 1.68573, pos instances = 135, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 input\n" +
                "    d 2 1 input\n" +
                "    d 3 1 input\n" +
                "\n" +
                "\n" +
                "SUBDUE done (elapsed CPU time =    0.10 seconds).";

        testParser = new SubdueResultParser(subdueResult);

        assertEquals("Should parse first subdue result", testParser.getResult(1),
                "v 1 transaction\n" +
                        "    v 2 input\n" +
                        "    v 3 output\n" +
                        "    v 4 output\n" +
                        "    d 2 1 input\n" +
                        "    d 1 3 output\n" +
                        "    d 1 4 output");

        assertEquals("Should parse second subdue result", testParser.getResult(2),
                "No match at index 2");

        assertEquals("Should parse third subdue result", testParser.getResult(3),
                "No match at index 3");

    }

    @Test
    public void shouldFindNoValidResults() {
        subdueResult = "SUBDUE 5.2.2\n" +
                "\n" +
                "Parameters:\n" +
                "  Input file..................... ../../../Documents/FinalYearProject/subdueGraph.g\n" +
                "  Predefined substructure file... none\n" +
                "  Output file.................... none\n" +
                "  Beam width..................... 4\n" +
                "  Compress....................... false\n" +
                "  Evaluation method.............. MDL\n" +
                "  'e' edges directed............. true\n" +
                "  Incremental.................... false\n" +
                "  Iterations..................... 1\n" +
                "  Limit.......................... 288\n" +
                "  Minimum size of substructures.. 1\n" +
                "  Maximum size of substructures.. 726\n" +
                "  Number of best substructures... 3\n" +
                "  Output level................... 2\n" +
                "  Allow overlapping instances.... false\n" +
                "  Prune.......................... false\n" +
                "  Threshold...................... 0.000000\n" +
                "  Value-based queue.............. false\n" +
                "  Recursion...................... false\n" +
                "\n" +
                "Read 149 total positive graphs\n" +
                "\n" +
                "149 positive graphs: 726 vertices, 577 edges, 9657 bits\n" +
                "3 unique labels\n" +
                "\n" +
                "3 initial substructures\n" +
                "\n" +
                "Best 3 substructures:\n" +
                "\n" +
                "(1) Substructure: value = 2.82916, pos instances = 135, neg instances = 0\n" +
                "  Graph(4v,3e):\n" +
                "    v 1 input\n" +
                "    v 2 output\n" +
                "    d 1 2 input\n" +
                "\n" +
                "(2) Substructure: value = 1.77588, pos instances = 149, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 output\n" +
                "    v 3 output\n" +
                "    d 1 2 output\n" +
                "    d 1 3 output\n" +
                "\n" +
                "(3) Substructure: value = 1.68573, pos instances = 135, neg instances = 0\n" +
                "  Graph(3v,2e):\n" +
                "    v 1 transaction\n" +
                "    v 2 input\n" +
                "    v 3 input\n" +
                "    d 2 1 input\n" +
                "    d 3 1 input\n" +
                "\n" +
                "\n" +
                "SUBDUE done (elapsed CPU time =    0.10 seconds).";

        testParser = new SubdueResultParser(subdueResult);

        assertEquals("Should parse first subdue result", testParser.getResult(1),
                "No match at index 1");

        assertEquals("Should parse second subdue result", testParser.getResult(2),
                "No match at index 2");

        assertEquals("Should parse third subdue result", testParser.getResult(3),
                "No match at index 3");
    }
}
package com.cyoung.blockchain.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubdueResultParser {
    private String result;

    public SubdueResultParser(String subdueResult) {
        Pattern pattern = Pattern.compile("Best 3 substructures:(.|\n)*SUBDUE done");
        Matcher matcher = pattern.matcher(subdueResult);

        if (matcher.find()) {
            result = matcher.group(0);
        } else {
            result = "No match";
        }
    }

    public String getAllResults() {
        return result;
    }

    public String getResult(int index) {
//        Pattern pattern = Pattern.compile("\\(" + index + "\\) Substructure: value = \\d*.\\d*, pos instances = \\d*, neg instances = \\d*\n  Graph\\(\\d*v,\\d*e\\):\n(.|\n)*\\(" + (index+1) + "\\)");
//        Pattern pattern = Pattern.compile("[(]" + index + "[)] Substructure: value = \\d*.\\d*, pos instances = \\d*, neg instances = \\d*\n  Graph[(]d*v,\\d*e[)]:\n(.|\n)*[(]" + (index+1) + "[)]");
//        Pattern pattern = Pattern.compile("(v \\d+ transaction\\s*|v \\d+ input\\s*|v \\d+ output\\s*|d \\d+ \\d+ input\\s*|d \\d+ \\d+ output\\s*)");
//        Pattern pattern = Pattern.compile("(v \\d+ transaction\\n\\s*|v \\d+ input\\n\\s*|v \\d+ output\\n\\s*|d \\d+ \\d+ input\\n\\s*|d \\d+ \\d+ output\\n\\s*)*");
//        String strippedResult = result.replace("\n", "");
        Pattern pattern = Pattern.compile("Substructure");
        Matcher matcher = pattern.matcher(result);
        return matcher.group(index);
//        (v \d+ transaction\n\s*|v \d+ input\n\s*|v \d+ output\n\s*|d \d+ \d+ input\n\s*|d \d+ \d+ output\n\s*)*
    }
}

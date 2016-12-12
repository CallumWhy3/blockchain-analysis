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
        Pattern pattern = Pattern.compile("(\\s*v\\s\\d+\\stransaction\\n(\\s*v\\s\\d+\\sinput\\n)+(\\s*v\\s\\d+\\soutput\\n)+(\\s*d\\s\\d+\\s\\d+\\sinput\\n)+(\\s*d\\s\\d+\\s\\d+\\soutput)+)");
        Matcher matcher = pattern.matcher(result);

        if (matcher.find()) {
            return matcher.group(index);
        } else {
            return "No match";
        }
    }
}

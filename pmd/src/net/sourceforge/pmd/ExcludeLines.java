package net.sourceforge.pmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class ExcludeLines {

    private static final String NO_USER_MESSAGE = "No user message";
    private static final String COMMENT = "//";
    public static final String EXCLUDE_MARKER = "NOPMD";

    private Map lines = new HashMap();
    private String copy;

    public ExcludeLines(Reader r, String marker) throws IOException {
        BufferedReader br = new BufferedReader(r);
        StringBuffer copyBuffer = new StringBuffer();
        String tmp;
        int counter = 1;
        // store the line number where NOPMD marker initially was
        Integer hasMessage = null;
        while ((tmp = br.readLine()) != null) {
            copyBuffer.append(tmp + PMD.EOL);
            int indexOfMarker = tmp.indexOf(marker);
            if (indexOfMarker != -1) {
            	String message = NO_USER_MESSAGE;
                hasMessage = new Integer(counter);
            	// is there a user message after PMD marker?
            	indexOfMarker = tmp.indexOf(" ", indexOfMarker);
            	if (indexOfMarker != -1) {
	            	String temp = tmp.substring(indexOfMarker).trim();
	            	if (!"".equals(temp)) {
	            		message = temp;
	            	}
            	}
                lines.put(hasMessage, message);
            } else if (hasMessage != null) {
                // is there a user message (comment) on this line?
                tmp = tmp.trim();
                if (tmp.startsWith(COMMENT) && tmp.length() > 2) {
                    String message = (String) lines.get(hasMessage);
                    tmp = tmp.substring(2).trim();
                    if (!"".equals(tmp)) {
                        if (message == null || message.equals(NO_USER_MESSAGE) ) {
                            message = tmp;
                        } else {
                            message = message + " " + tmp;
                        }
                        lines.put(hasMessage, message);
                    }
                } else {
                    hasMessage = null;
                }
            }

            counter++;
        }
        copy = copyBuffer.toString();
    }

    public ExcludeLines(Reader r) throws IOException {
        this(r, EXCLUDE_MARKER);
    }

    public Reader getCopyReader() {
        return new StringReader(copy);
    }

    public Map getLinesToExclude() {
        return lines;
    }
}

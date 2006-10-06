/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 * @authors: Zev Blut zb@ubit.com
 */
package net.sourceforge.pmd.cpd;

import java.util.List;

public class RubyTokenizer implements Tokenizer {
    private boolean downcaseString = true;

    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
        List code = tokens.getCode();
        for (int i = 0; i < code.size(); i++) {
            String currentLine = (String) code.get(i);
            int loc = 0;
            while (loc < currentLine.length()) {
                StringBuffer token = new StringBuffer();
                loc = getTokenFromLine(currentLine, token, loc);
                if (token.length() > 0 && !isIgnorableString(token.toString())) {
                    if (downcaseString) {
                        token = new StringBuffer(token.toString().toLowerCase());
                    }
                    tokenEntries.add(new TokenEntry(token.toString(),
                            tokens.getFileName(),
                            i + 1));
                }
            }
        }
        tokenEntries.add(TokenEntry.getEOF());
    }

    private int getTokenFromLine(String line, StringBuffer token, int loc) {
        for (int j = loc; j < line.length(); j++) {
            char tok = line.charAt(j);
            if (!Character.isWhitespace(tok) && !ignoreCharacter(tok)) {
                if (isComment(tok)) {
                    if (token.length() > 0) {
                        return j;
                    } else {
                        return getCommentToken(line, token, loc);
                    }
                } else if (isString(tok)) {
                    if (token.length() > 0) {
                        //if (loc == lin
                        return j; // we need to now parse the string as a seperate token.
                    } else {
                        // we are at the start of a string
                        return parseString(line, token, j, tok);
                    }
                } else {
                    token.append(tok);
                }
            } else {
                if (token.length() > 0) {
                    return j;
                }
            }
            loc = j;
        }
        return loc + 1;
    }

    private int parseString(String line, StringBuffer token, int loc, char stringType) {
        boolean escaped = false;
        boolean done = false;
        //System.out.println("Parsing String:" + stringType);
        //System.out.println("Starting loc:" + loc);
        // problem of strings that span multiple lines :-(
        char tok = ' '; // this will be replaced.
        while ((loc < line.length()) && !done) {
            tok = line.charAt(loc);
            if (escaped && tok == stringType) {
                //     System.out.println("Found an escaped string");
                escaped = false;
            } else if (tok == stringType && (token.length() > 0)) {
                // we are done
                //   System.out.println("Found an end string");
                done = true;
            } else if (tok == '\\') {
                // System.out.println("Found an escaped char");
                escaped = true;
            } else {
                // System.out.println("Adding char:" + tok + ";loc:" + loc);
                escaped = false;
            }
            //System.out.println("Adding char to String:" + token.toString());
            token.append(tok);
            loc++;
        }
        return loc + 1;
    }

    private boolean ignoreCharacter(char tok) {
        boolean result = false;
        switch (tok) {
            case '{':
            case '}':
            case '(':
            case ')':
            case ';':
            case ',':
                result = true;
                break;
            default :
                result = false;
        }
        return result;
    }

    private boolean isString(char tok) {
        boolean result = false;
        switch (tok) {
            case '\'':
            case '"':
                result = true;
                break;
            default:
                result = false;
        }
        return result;
    }

    private boolean isComment(char tok) {
        return tok == '#';
    }

    private int getCommentToken(String line, StringBuffer token, int loc) {
        while (loc < line.length()) {
            token.append(line.charAt(loc));
            loc++;
        }
        return loc;
    }

    private boolean isIgnorableString(String token) {
        return "do".equals(token) || "end".equals(token);
    }
}

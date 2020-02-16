/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Zev Blut zb@ubit.com
 * @author Romain PELISSE belaran@gmail.com
 */
public abstract class AbstractTokenizer implements Tokenizer {

    // FIXME depending on subclasses to assign local vars is rather fragile -
    // better to make private and setup via explicit hook methods

    protected List<String> stringToken; // List<String>, should be set by sub
    // classes
    protected List<String> ignorableCharacter; // List<String>, should be set by
    // sub classes
    // FIXME:Maybe an array of 'char'
    // would be better for
    // performance ?
    protected List<String> ignorableStmt; // List<String>, should be set by sub
    // classes
    protected char oneLineCommentChar = '#'; // Most script languages ( shell,
    // ruby, python,...) use this
    // symbol for comment line

    private List<String> code;
    private int lineNumber = 0;
    private String currentLine;

    protected boolean spanMultipleLinesString = true; // Most languages do, so
    // default is true
    protected Character spanMultipleLinesLineContinuationCharacter = null;

    private boolean downcaseString = true;

    @Override
    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
        code = tokens.getCode();

        for (lineNumber = 0; lineNumber < code.size(); lineNumber++) {
            currentLine = code.get(lineNumber);
            int loc = 0;
            while (loc < currentLine.length()) {
                StringBuilder token = new StringBuilder();
                loc = getTokenFromLine(token, loc);
                if (token.length() > 0 && !isIgnorableString(token.toString())) {
                    if (downcaseString) {
                        token = new StringBuilder(token.toString().toLowerCase(Locale.ROOT));
                    }
                    // need to re-think how to link this
                    // if ( CPD.debugEnable ) {
                    // System.out.println("Token added:" + token.toString());
                    // }
                    tokenEntries.add(new TokenEntry(token.toString(), tokens.getFileName(), lineNumber + 1, loc - token.length(), loc - 1));
                }
            }
        }
        tokenEntries.add(TokenEntry.getEOF());
    }

    private int getTokenFromLine(StringBuilder token, int loc) {
        for (int j = loc; j < currentLine.length(); j++) {
            char tok = currentLine.charAt(j);
            if (!Character.isWhitespace(tok) && !ignoreCharacter(tok)) {
                if (isComment(tok)) {
                    if (token.length() > 0) {
                        return j;
                    } else {
                        return getCommentToken(token, loc);
                    }
                } else if (isString(tok)) {
                    if (token.length() > 0) {
                        return j; // we need to now parse the string as a
                        // separate token.
                    } else {
                        // we are at the start of a string
                        return parseString(token, j, tok);
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

    private int parseString(StringBuilder token, int loc, char stringDelimiter) {
        boolean escaped = false;
        boolean done = false;
        char tok = ' '; // this will be replaced.
        while (loc < currentLine.length() && !done) {
            tok = currentLine.charAt(loc);
            if (escaped && tok == stringDelimiter) { // Found an escaped string
                escaped = false;
            } else if (tok == stringDelimiter && token.length() > 0) {
                // We are done, we found the end of the string...
                done = true;
            } else if (tok == '\\') { // Found an escaped char
                escaped = true;
            } else { // Adding char...
                escaped = false;
            }
            // Adding char to String:" + token.toString());
            token.append(tok);
            loc++;
        }
        // Handling multiple lines string
        if (!done && // ... we didn't find the end of the string
                loc >= currentLine.length() && // ... we have reach the end of
                // the line ( the String is
                // incomplete, for the moment at
                // least)
                spanMultipleLinesString && // ... the language allow multiple
                // line span Strings
                lineNumber < code.size() - 1 // ... there is still more lines to
        // parse
        ) {
            // removes last character, if it is the line continuation (e.g.
            // backslash) character
            if (spanMultipleLinesLineContinuationCharacter != null && token.length() > 0
                    && token.charAt(token.length() - 1) == spanMultipleLinesLineContinuationCharacter.charValue()) {
                token.deleteCharAt(token.length() - 1);
            }
            // parsing new line
            currentLine = code.get(++lineNumber);
            // Warning : recursive call !
            loc = parseString(token, 0, stringDelimiter);
        }
        return loc + 1;
    }

    private boolean ignoreCharacter(char tok) {
        return ignorableCharacter.contains(String.valueOf(tok));
    }

    private boolean isString(char tok) {
        return stringToken.contains(String.valueOf(tok));
    }

    private boolean isComment(char tok) {
        return tok == oneLineCommentChar;
    }

    private int getCommentToken(StringBuilder token, int loc) {
        while (loc < currentLine.length()) {
            token.append(currentLine.charAt(loc++));
        }
        return loc;
    }

    private boolean isIgnorableString(String token) {
        return ignorableStmt.contains(token);
    }
}

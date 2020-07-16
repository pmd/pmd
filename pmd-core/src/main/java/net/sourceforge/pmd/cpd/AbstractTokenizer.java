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

    // both zero-based
    private int tokBeginLine;
    private int tokBeginCol;

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
                loc = getTokenFromLine(token, loc); // may jump several lines

                if (token.length() > 0 && !isIgnorableString(token.toString())) {
                    final String image;
                    if (downcaseString) {
                        image = token.toString().toLowerCase(Locale.ROOT);
                    } else {
                        image = token.toString();
                    }

                    tokenEntries.add(new TokenEntry(image,
                                                    tokens.getFileName(),
                                                    tokBeginLine + 1,
                                                    tokBeginCol + 1,
                                                    loc));
                }
            }
        }
        tokenEntries.add(TokenEntry.getEOF());
    }

    /**
     * Returns (0-based) EXclusive offset of the end of the token,
     * may jump several lines (sets {@link #lineNumber} in this case).
     */
    private int getTokenFromLine(StringBuilder token, int loc) {
        tokBeginLine = lineNumber;
        tokBeginCol = loc;

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
                } else {
                    // ignored char
                    tokBeginCol++;
                }
            }
            loc = j;
        }
        return loc + 1;
    }

    private int parseString(StringBuilder token, int loc, char stringDelimiter) {
        boolean escaped = false;
        boolean done = false;
        char tok;
        while (loc < currentLine.length() && !done) {
            tok = currentLine.charAt(loc);
            if (escaped && tok == stringDelimiter) { // Found an escaped string
                escaped = false;
            } else if (tok == stringDelimiter && token.length() > 0) {
                // We are done, we found the end of the string...
                done = true;
            } else {
                // Found an escaped char?
                escaped = tok == '\\';
            }
            // Adding char to String:" + token.toString());
            token.append(tok);
            loc++;
        }
        // Handling multiple lines string
        if (!done // ... we didn't find the end of the string (but the end of the line)
            && spanMultipleLinesString // ... the language allow multiple line span Strings
            && lineNumber < code.size() - 1 // ... there is still more lines to parse
        ) {
            // removes last character, if it is the line continuation (e.g.
            // backslash) character
            if (spanMultipleLinesLineContinuationCharacter != null
                && token.length() > 0
                && token.charAt(token.length() - 1) == spanMultipleLinesLineContinuationCharacter) {
                token.setLength(token.length() - 1);
            }
            // parsing new line
            currentLine = code.get(++lineNumber);
            // Warning : recursive call !
            loc = parseString(token, 0, stringDelimiter);
        }
        return loc;
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

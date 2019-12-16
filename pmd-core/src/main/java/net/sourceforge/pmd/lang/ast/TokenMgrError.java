/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * An error thrown during lexical analysis of a file.
 */
public final class TokenMgrError extends RuntimeException {

    /**
     * Lexical error occurred.
     */
    public static final int LEXICAL_ERROR = 0;

    /**
     * Tried to change to an invalid lexical state.
     */
    public static final int INVALID_LEXICAL_STATE = 2;

    /**
     * Detected (and bailed out of) an infinite loop in the token manager.
     */
    public static final int LOOP_DETECTED = 3;

    /** Constructor with message. */
    private TokenMgrError(String message, Throwable cause) {
        super(message, cause);
    }

    /** Constructor with message and reason. */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public TokenMgrError(String message, int reason) {
        super(message);
    }

    /** Full Constructor. */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public TokenMgrError(boolean eofSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int reason) {
        this(makeMessage(eofSeen, errorLine, errorColumn, errorAfter, curChar), reason);
    }

    /**
     * Replaces unprintable characters by their escaped (or unicode escaped)
     * equivalents in the given string
     */
    private static String addEscapes(String str) {
        StringBuilder retval = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            final char ch = str.charAt(i);
            switch (ch) {
            case 0:
                break;
            case '\b':
                retval.append("\\b");
                break;
            case '\t':
                retval.append("\\t");
                break;
            case '\n':
                retval.append("\\n");
                break;
            case '\f':
                retval.append("\\f");
                break;
            case '\r':
                retval.append("\\r");
                break;
            case '\"':
                retval.append("\\\"");
                break;
            case '\'':
                retval.append("\\'");
                break;
            case '\\':
                retval.append("\\\\");
                break;
            default:
                if (ch < 0x20 || ch > 0x7e) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("\\u").append(s.substring(s.length() - 4));
                } else {
                    retval.append(ch);
                }
                break;
            }
        }
        return retval.toString();
    }

    /**
     * Returns a detailed message for the Error when it is thrown by the
     * token manager to indicate a lexical error.
     * Parameters :
     * eofseen     : indicates if EOF caused the lexical error
     * curLexState : lexical state in which this error occurred
     * errorLine   : line number when the error occurred
     * errorColumn : column number when the error occurred
     * errorAfter  : prefix that was seen before this error occurred
     * curchar     : the offending character
     * Note: You can customize the lexical error message by modifying this method.
     */
    private static String makeMessage(boolean eofseen, int errorLine, int errorColumn, String errorAfter, char curChar) {
        return "Lexical error at line " + errorLine + ", column " + errorColumn + ".  Encountered: "
            + (eofseen ? "<EOF> " : "\"" + addEscapes(String.valueOf(curChar)) + "\"" + " (" + (int) curChar + "), ")
            + "after : \"" + addEscapes(errorAfter) + "\"";
    }

    public static TokenMgrError withFileName(String filename, TokenMgrError error) {
        String newMessage = "Lexical error in file " + filename + error.getMessage().substring("Lexical error ".length());
        return new TokenMgrError(newMessage, error);
    }
}

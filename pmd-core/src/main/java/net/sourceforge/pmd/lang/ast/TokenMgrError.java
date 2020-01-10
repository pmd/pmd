/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.StringUtil;

/**
 * An error thrown during lexical analysis of a file.
 */
public final class TokenMgrError extends RuntimeException {


    /**
     * @deprecated Will be removed when all modules are ported
     */
    @Deprecated
    public static final int LEXICAL_ERROR = 0;

    /**
     * @deprecated Will be removed when all modules are ported,
     *     see {@link #TokenMgrError(String, int)}
     */
    @Deprecated
    public static final int INVALID_LEXICAL_STATE = 1;

    private final int line;
    private final int column;
    private final String filename;

    public TokenMgrError(int line, int column, @Nullable String filename, String message, @Nullable Throwable cause) {
        super(message, cause);
        this.line = line;
        this.column = column;
        this.filename = filename;
    }

    public TokenMgrError(int line, int column, String message, @Nullable Throwable cause) {
        this(line, column, null, message, cause);
    }

    /**
     * @deprecated This is used by javacc but those usages are being replaced with an IllegalArgumentException
     */
    @Deprecated
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public TokenMgrError(String message, int errorCode) {
        this(-1, -1, null, message, null);
    }

    /**
     * Constructor called by JavaCC.
     */
    @InternalApi
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public TokenMgrError(boolean eofSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar) {
        super(makeMessage(eofSeen, errorLine, errorColumn, errorAfter, curChar));
        line = errorLine;
        column = errorColumn;
        filename = AbstractTokenManager.getFileName();
    }

    /**
     * Constructor called by JavaCC.
     *
     * @deprecated The error code is useless, ported modules use the other constructor
     */
    @Deprecated
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public TokenMgrError(boolean eofSeen, int lexState, int errorLine, int errorColumn, String errorAfter, char curChar, int errorCode) {
        super(makeMessage(eofSeen, errorLine, errorColumn, errorAfter, curChar));
        line = errorLine;
        column = errorColumn;
        filename = AbstractTokenManager.getFileName();
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public @Nullable String getFilename() {
        return filename;
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
        String message;
        if (eofseen) {
            message = "<EOF> ";
        } else {
            message = "\"" + StringUtil.escapeJava(String.valueOf(curChar)) + "\"" + " (" + (int) curChar + "), ";
        }
        message += "after : \"" + StringUtil.escapeJava(errorAfter) + "\"";

        return makeMessage(errorLine, errorColumn, message, AbstractTokenManager.getFileName());
    }

    private static String makeMessage(int errorLine, int errorColumn, String message, String fileName) {
        return "Lexical error in file " + fileName
            + " at line " + errorLine + ", column " + errorColumn
            + ".  Encountered: " + message;
    }

}

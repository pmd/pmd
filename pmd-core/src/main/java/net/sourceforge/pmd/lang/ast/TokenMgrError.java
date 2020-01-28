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
    public TokenMgrError(boolean eofSeen, String lexStateName, int errorLine, int errorColumn, String errorAfter, char curChar) {
        super(makeReason(eofSeen, lexStateName, errorAfter, curChar));
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
        super(makeReason(eofSeen, String.valueOf(lexState), errorAfter, curChar));
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


    @Override
    public String getMessage() {
        String leader = filename != null ? "Lexical error in file " + filename : "Lexical error";
        return leader + " at line " + line + ", column " + column + ".  Encountered: " + super.getMessage();
    }

    /**
     * Replace the file name of this error.
     *
     * @param filename New filename
     */
    public TokenMgrError withFileName(String filename) {
        return new TokenMgrError(this.line, this.column, filename, this.getMessage(), this.getCause());
    }

    private static String makeReason(boolean eofseen, String lexStateName, String errorAfter, char curChar) {
        String message;
        if (eofseen) {
            message = "<EOF> ";
        } else {
            message = "\"" + StringUtil.escapeJava(String.valueOf(curChar)) + "\"" + " (" + (int) curChar + "), ";
        }
        message += "after : \"" + StringUtil.escapeJava(errorAfter) + "\" (in lexical state " + lexStateName + ")";

        return message;
    }
}

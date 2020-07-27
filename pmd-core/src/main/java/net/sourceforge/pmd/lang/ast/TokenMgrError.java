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
public final class TokenMgrError extends FileAnalysisException {

    private final int line;
    private final int column;
    private final String filename;

    /**
     * Create a new exception.
     *
     * @param line     Line number
     * @param column   Column number
     * @param filename Filename. If unknown, it can be completed with {@link #withFileName(String)} later
     * @param message  Message of the error
     * @param cause    Cause of the error, if any
     */
    public TokenMgrError(int line, int column, @Nullable String filename, String message, @Nullable Throwable cause) {
        super(message, cause);
        this.line = line;
        this.column = column;
        this.filename = filename;
    }

    /**
     * Constructor called by JavaCC.
     */
    @InternalApi
    public TokenMgrError(boolean eofSeen, String lexStateName, int errorLine, int errorColumn, String errorAfter, char curChar) {
        super(makeReason(eofSeen, lexStateName, errorAfter, curChar));
        line = errorLine;
        column = errorColumn;
        filename = null; // may be replaced with #withFileName
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
     *
     * @return A new exception
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

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.util.StringUtil;

/**
 * An error thrown during lexical analysis of a file.
 */
public final class TokenMgrError extends FileAnalysisException {

    private final int line;
    private final int column;

    /**
     * Create a new exception.
     *
     * @param line     Line number
     * @param column   Column number
     * @param filename Filename. If unknown, it can be completed with {@link #setFileName(String)} later
     * @param message  Message of the error
     * @param cause    Cause of the error, if any
     */
    public TokenMgrError(int line, int column, @Nullable FileId filename, String message, @Nullable Throwable cause) {
        super(message, cause);
        this.line = line;
        this.column = column;
        if (filename != null) {
            super.setFileId(filename);
        }
    }

    /**
     * Constructor called by JavaCC.
     */
    @InternalApi
    public TokenMgrError(boolean eofSeen, String lexStateName, int errorLine, int errorColumn, String errorAfter, char curChar) {
        super(makeReason(eofSeen, lexStateName, errorAfter, curChar));
        line = errorLine;
        column = errorColumn;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    protected @NonNull FileLocation location() {
        return FileLocation.caret(getFileId(), line, column);
    }

    @Override
    protected String errorKind() {
        return "Lexical error";
    }

    /**
     * Replace the file name of this error.
     *
     * @param fileId New filename
     *
     * @return A new exception
     */
    @Override
    public TokenMgrError setFileId(FileId fileId) {
        super.setFileId(fileId);
        return this;
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

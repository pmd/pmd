/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;

/**
 * A {@link FileAnalysisException} thrown when the source format is invalid,
 * for example if some unicode escapes cannot be translated.
 */
public class MalformedSourceException extends FileAnalysisException {

    private final int line;
    private final int col;

    public MalformedSourceException(String message, Throwable cause, int line, int col) {
        super(message, cause);
        this.line = line;
        this.col = col;
    }

    @Override
    protected String positionToString() {
        return super.positionToString() + " at line " + line + ", column " + col;
    }

    @Override
    protected String errorKind() {
        return "Source format error";
    }
}

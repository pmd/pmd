/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc.io;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

/**
 *
 */
public class MalformedSourceException extends FileAnalysisException {

    private final int offset;
    private final int line;
    private final int col;

    public MalformedSourceException(String message, Throwable cause, int offset, int line, int col) {
        super(message, cause);
        this.offset = offset;
        this.line = line;
        this.col = col;
    }

    public TokenMgrError toLexException(String filename) {
        return new TokenMgrError(line, col, filename, getMessage(), getCause());
    }
}

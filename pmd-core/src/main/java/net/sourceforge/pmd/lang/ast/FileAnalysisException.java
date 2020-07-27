/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * An exception that occurs while processing a file. Subtypes include
 * <ul>
 * <li>{@link TokenMgrError}: lexical syntax errors
 * <li>{@link ParseException}: syntax errors
 * <li>{@link SemanticException}: exceptions occurring after the parsing
 * phase, because the source code is semantically invalid
 * </ul>
 */
public class FileAnalysisException extends RuntimeException {

    public FileAnalysisException() {
        super();
    }

    public FileAnalysisException(String message) {
        super(message);
    }

    public FileAnalysisException(Throwable cause) {
        super(cause);
    }
    public FileAnalysisException(String message, Throwable cause) {
        super(message, cause);
    }

}

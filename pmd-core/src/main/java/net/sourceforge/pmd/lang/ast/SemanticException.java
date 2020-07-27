/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * An error that occurs after validating a file.
 */
public class SemanticException extends FileAnalysisException {

    public SemanticException() {
        super();
    }

    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(Throwable cause) {
        super(cause);
    }
    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }
}

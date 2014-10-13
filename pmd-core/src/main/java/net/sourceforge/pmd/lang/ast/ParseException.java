/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast;

public class ParseException extends RuntimeException {

    public ParseException() {
	super();
    }

    public ParseException(String message) {
	super(message);
    }

    public ParseException(Throwable cause) {
	super(cause);
    }

    public ParseException(String message, Throwable cause) {
	super(message, cause);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

/**
 * Exception during the parsing and visitors of the compilation units. Could be specialized into one exception per
 * visitor (eg type res).
 */
public class ParseTimeException extends RuntimeException {

    public ParseTimeException(Throwable t) {
        super(t);
    }
}

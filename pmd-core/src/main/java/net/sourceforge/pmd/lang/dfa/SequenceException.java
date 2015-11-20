/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

/**
 * @author raik
 */
public class SequenceException extends Exception {
    private static final long serialVersionUID = -3271242247181888687L;

    public SequenceException() {
        super("Sequence error."); //TODO redefinition
    }

    public SequenceException(String message) {
        super(message);
    }
}

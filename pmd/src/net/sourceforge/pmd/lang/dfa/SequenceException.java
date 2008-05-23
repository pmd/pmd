/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.dfa;

/**
 * @author raik
 */
public class SequenceException extends Exception {

    public SequenceException() {
        super("Sequence error."); //TODO redefinition
    }

    public SequenceException(String message) {
        super(message);
    }
}

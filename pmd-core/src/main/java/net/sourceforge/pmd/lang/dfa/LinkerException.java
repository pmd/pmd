/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dfa;

/**
 * @author raik
 * @deprecated See {@link DataFlowNode}
 */
@Deprecated
public class LinkerException extends Exception {
    private static final long serialVersionUID = 3238380880636634352L;

    public LinkerException() {
        // TODO redefinition | accurate?
        super("An error occurred by computing the data flow paths");
    }

    public LinkerException(String message) {
        super(message);
    }

}

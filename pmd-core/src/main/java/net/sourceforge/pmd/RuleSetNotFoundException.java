/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

public class RuleSetNotFoundException extends Exception {
    private static final long serialVersionUID = -4617033110919250810L;

    public RuleSetNotFoundException(String msg) {
        super(msg);
    }
}

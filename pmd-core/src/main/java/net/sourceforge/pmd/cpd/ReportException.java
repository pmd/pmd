/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

/**
 * @author Philippe T'Seyen
 */
public class ReportException extends Exception {
    private static final long serialVersionUID = 6043174086675858209L;

    public ReportException(Throwable cause) {
        super(cause);
    }
}

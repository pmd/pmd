/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

/**
 * @author Philippe T'Seyen
 * @internalApi None of this is published API, and compatibility can be broken anytime! Use this only at your own risk.
 */
class ReportException extends RuntimeException {
    private static final long serialVersionUID = 6043174086675858209L;

    ReportException(Throwable cause) {
        super(cause);
    }
}

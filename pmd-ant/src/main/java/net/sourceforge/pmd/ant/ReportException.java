/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

/**
 * @author Philippe T'Seyen
 * @apiNote Internal API
 */
class ReportException extends RuntimeException {
    private static final long serialVersionUID = 6043174086675858209L;

    ReportException(Throwable cause) {
        super(cause);
    }
}

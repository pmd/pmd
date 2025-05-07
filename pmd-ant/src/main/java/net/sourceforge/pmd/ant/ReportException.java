/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import java.io.Serial;

/**
 * @author Philippe T'Seyen
 * @apiNote Internal API
 */
class ReportException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6043174086675858209L;

    ReportException(Throwable cause) {
        super(cause);
    }
}

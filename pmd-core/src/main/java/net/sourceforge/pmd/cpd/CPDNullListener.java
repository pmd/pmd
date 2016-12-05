/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;

public class CPDNullListener implements CPDListener {
    @Override
    public void addedFile(int fileCount, File file) {
    }

    @Override
    public void phaseUpdate(int phase) {
    }
}

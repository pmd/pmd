/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

public interface CPDListener {

    int INIT = 0;
    int HASH = 1;
    int MATCH = 2;
    int GROUPING = 3;
    int DONE = 4;

    void addedFile(int fileCount);

    void phaseUpdate(int phase);
}

/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:40:22 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

public interface CPDListener {
    void addedFile(int fileCount, File file);
}

/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:40:22 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

public interface CPDListener {
    public void update(String msg);
    public void addedFile(int fileCount, File file);
    public void addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID);
    public void expandingTile(String tileImage);
}

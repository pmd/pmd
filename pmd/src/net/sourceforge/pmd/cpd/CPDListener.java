/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:40:22 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

public interface CPDListener {
    public boolean update(String msg);
    public boolean addedFile(int fileCount, File file);
    public boolean addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID);
    public boolean addedNewTile(Tile tile, int tilesSoFar, int totalTiles);
}

/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:40:22 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

public interface CPDListener {
    boolean update(String msg);

    boolean addedFile(int fileCount, File file);

    boolean addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID);

    boolean addedNewTile(Tile tile, int tilesSoFar, int totalTiles);
}

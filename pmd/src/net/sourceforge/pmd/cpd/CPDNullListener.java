/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:41:43 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

public class CPDNullListener implements CPDListener{
    public boolean update(String msg) {return false;}
    public boolean addedFile(int fileCount, File file) {return false;}
    public boolean addingTokens(int tokenSetCount, int doneSoFar, String tokenSrcID) {return false;}
    public boolean addedNewTile(Tile tile, int tilesSoFar, int totalTiles) {return false;}
		
}

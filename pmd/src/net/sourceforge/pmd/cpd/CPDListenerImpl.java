/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:41:22 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

public class CPDListenerImpl implements CPDListener{
    public void update(String msg) {
        System.out.println(msg);
    }

    public void addedFile(File file) {
        update("Added file " + file.getAbsolutePath());
    }

    public void addingTokens(String tokenSrcID) {
        update("Adding token set " +  tokenSrcID);
    }

}


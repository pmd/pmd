/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 2:41:43 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;

public class CPDNullListener implements CPDListener{
    public void update(String msg) {}
    public void addedFile(File file) {}
}

/*
 * User: tom
 * Date: Aug 22, 2002
 * Time: 4:37:01 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

    private FilenameFilter filter;

    public List findFilesFrom(String dir, FilenameFilter filter, boolean recurse) {
        this.filter = filter;
        List files = new ArrayList();
        scanDirectory(new File(dir), files, recurse);
        return files;
    }

    /**
     * Implements a tail recursive file scanner
     */
    private void scanDirectory(File dir, List list, boolean recurse) {
     String[] possibles = dir.list(filter);
     for (int i=0; i<possibles.length; i++) {
        File tmp = new File(dir + System.getProperty("file.separator") + possibles[i]);
        if (tmp.isDirectory()) {
            if (recurse) {
                scanDirectory(tmp, list, true);
            }
        } else {
           list.add(new File(dir + System.getProperty("file.separator") + possibles[i]));
        }
     }
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileFinder {

    private FilenameFilter filter;
    private static final String FILE_SEP = System.getProperty("file.separator");

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
        String[] candidates = dir.list(filter);
        for (int i = 0; i < candidates.length; i++) {
            File tmp = new File(dir + FILE_SEP + candidates[i]);
            if (tmp.isDirectory()) {
                if (recurse) {
                    scanDirectory(tmp, list, true);
                }
            } else {
                list.add(new File(dir + FILE_SEP + candidates[i]));
            }
        }
    }
}

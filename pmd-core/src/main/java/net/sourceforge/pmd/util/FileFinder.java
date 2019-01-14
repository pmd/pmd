/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A utility class for finding files within a directory.
 */
public class FileFinder {

    private FilenameFilter filter;
    private static final String FILE_SEP = System.getProperty("file.separator");

    /**
     * Searches for files in a given directory.
     * The returned files are sorted alphabetically by path, ignoring the case.
     *
     * @param dir     the directory to search files
     * @param filter  the filename filter that can optionally be passed to get files that match this filter
     * @param recurse search for files recursively or not
     * @return list of the found files sorted alphabetically by path, ignoring the case
     */
    public List<File> findFilesFrom(File dir, FilenameFilter filter, boolean recurse) {
        this.filter = filter;
        List<File> files = new ArrayList<>();
        scanDirectory(dir, files, recurse);
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getPath().compareToIgnoreCase(o2.getPath());
            }
        });
        return files;
    }

    /**
     * Implements a tail recursive file scanner
     */
    private void scanDirectory(File dir, List<File> list, boolean recurse) {
        String[] candidates = dir.list(filter);
        if (candidates == null) {
            return;
        }
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

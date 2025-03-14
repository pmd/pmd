/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A utility class for finding files within a directory.
 */
public class FileFinder {

    private FilenameFilter filter;

    /**
     * Searches for files in a given directory.
     *
     * @param dir     the directory to search files
     * @param filter  the filename filter that can optionally be passed to get files that match this filter
     * @param recurse search for files recursively or not
     * @return list of files from the given directory
     */
    public List<File> findFilesFrom(File dir, FilenameFilter filter, boolean recurse) {
        this.filter = filter;
        List<File> files = new ArrayList<>();
        scanDirectory(dir, files, recurse);

        return files;
    }

    /**
     * Implements a tail recursive file scanner
     */
    private void scanDirectory(File dir, List<File> list, boolean recurse) {
        File[] candidates = dir.listFiles(filter);
        if (candidates == null) {
            return;
        }

        Arrays.sort(candidates, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getPath().compareToIgnoreCase(o2.getPath());
            }
        });

        for (File tmp : candidates) {
            if (tmp.isDirectory()) {
                if (recurse) {
                    scanDirectory(tmp, list, true);
                }
            } else {
                list.add(tmp);
            }
        }
    }
}

/*
 * User: tom
 * Date: Aug 22, 2002
 * Time: 4:34:12 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

public class JavaFileOrDirectoryFilter implements FilenameFilter {

    public boolean accept(File dir, String filename) {
        return filename.endsWith("java") || (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory());
    }

}

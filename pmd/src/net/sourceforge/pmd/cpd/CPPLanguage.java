/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

public class CPPLanguage implements Language{

    public static class CPPFileOrDirectoryFilter implements FilenameFilter {
        public boolean accept(File dir, String filename) {
            return filename.endsWith(".c") || filename.endsWith(".cpp") || (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory());
        }
    }

    public Tokenizer getTokenizer() {
        return new CPPTokenizer();
    }

    public FilenameFilter getFileFilter() {
        return new CPPFileOrDirectoryFilter();
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

public class JavaLanguage implements Language {

    public static class JavaFileOrDirectoryFilter implements FilenameFilter {
        public boolean accept(File dir, String filename) {
            return filename.endsWith("java") || (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory());
        }
    }

    public Tokenizer getTokenizer() {
        return new JavaTokenizer();
    }

    public FilenameFilter getFileFilter() {
        return new JavaFileOrDirectoryFilter();
    }
}

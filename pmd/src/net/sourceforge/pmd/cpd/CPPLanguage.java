/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

public class CPPLanguage implements Language {

    public static class CPPFileOrDirectoryFilter implements FilenameFilter {
        public boolean accept(File dir, String filename) {
            return filename.endsWith(".h") || filename.endsWith(".c") || filename.endsWith(".cpp") || filename.endsWith(".cxx") || filename.endsWith(".cc") || (new File(dir.getAbsolutePath() + fileSeparator + filename).isDirectory());
        }
    }

    public Tokenizer getTokenizer() {
        return new CPPTokenizer();
    }

    public FilenameFilter getFileFilter() {
        return new CPPFileOrDirectoryFilter();
    }
}

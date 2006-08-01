/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;

public class AnyLanguage implements Language {

    public static class AnyFileOrDirectoryFilter implements FilenameFilter {
        String extension;

        public AnyFileOrDirectoryFilter(String extension) {
            this.extension = extension;
        }

        public boolean accept(File dir, String filename) {
            return filename.endsWith(extension) || (new File(dir.getAbsolutePath() + fileSeparator + filename).isDirectory());
        }
    }

    private AnyTokenizer tokenizer;
    private String extension;

    public AnyLanguage(String extension) {
        this.extension = extension;
        tokenizer = new AnyTokenizer();
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public FilenameFilter getFileFilter() {
        return new AnyFileOrDirectoryFilter(this.extension);
    }
}

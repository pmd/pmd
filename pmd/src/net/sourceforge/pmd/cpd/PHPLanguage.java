package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;
import java.io.File;

public class PHPLanguage implements Language {

    public static class PHPFileOrDirectoryFilter implements FilenameFilter {
        public boolean accept(File dir, String filename) {
            return filename.endsWith("php") || filename.endsWith("class") ||(new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory());
        }
    }

    public Tokenizer getTokenizer() {
        return new PHPTokenizer();
    }

    public FilenameFilter getFileFilter() {
        return new PHPFileOrDirectoryFilter();
    }
}

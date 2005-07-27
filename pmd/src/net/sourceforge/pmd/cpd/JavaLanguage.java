/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Properties;

public class JavaLanguage implements Language {

    public static class JavaFileOrDirectoryFilter implements FilenameFilter {
        public boolean accept(File dir, String filename) {
            return (filename.endsWith("java") || (new File(dir.getAbsolutePath() + System.getProperty("file.separator") + filename).isDirectory())) && !filename.equals("SCCS");
        }
    }

    private JavaTokenizer tokenizer;
    
    public JavaLanguage() {
        this(new Properties());
    }
    
    public JavaLanguage(Properties properties) {
        tokenizer = new JavaTokenizer();
        tokenizer.setProperties(properties);
    }
    
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public FilenameFilter getFileFilter() {
        return new JavaFileOrDirectoryFilter();
    }
}

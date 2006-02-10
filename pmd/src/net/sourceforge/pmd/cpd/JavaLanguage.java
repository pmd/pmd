/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.SourceFileSelector;

import java.io.FilenameFilter;
import java.util.Properties;

public class JavaLanguage implements Language {

    private JavaTokenizer tokenizer;

    private SourceFileSelector sourceFileSelector;

    public JavaLanguage() {
        this(new Properties());
    }

    public JavaLanguage(Properties properties) {
        tokenizer = new JavaTokenizer();
        tokenizer.setProperties(properties);
        sourceFileSelector = new SourceFileSelector();
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public FilenameFilter getFileFilter() {
        return new SourceFileOrDirectoryFilter(sourceFileSelector);
    }
}

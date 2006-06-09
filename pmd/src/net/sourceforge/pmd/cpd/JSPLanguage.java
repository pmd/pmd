/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.SourceFileSelector;

import java.io.FilenameFilter;

public class JSPLanguage implements Language {

    private JSPTokenizer tokenizer = new JSPTokenizer();
    private SourceFileSelector sourceFileSelector;

    public JSPLanguage() {
        sourceFileSelector = new SourceFileSelector();
        sourceFileSelector.setSelectJavaFiles(false);
        sourceFileSelector.setSelectJspFiles(true);
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public FilenameFilter getFileFilter() {
        return new SourceFileOrDirectoryFilter(sourceFileSelector);
    }
}


/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;

public interface Language {
    public Tokenizer getTokenizer();
    public FilenameFilter getFileFilter();
}

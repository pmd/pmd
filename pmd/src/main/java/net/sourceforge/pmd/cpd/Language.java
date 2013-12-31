/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;
import java.util.Properties;

public interface Language {

    Tokenizer getTokenizer();

    FilenameFilter getFileFilter();

    void setProperties(Properties properties);
}

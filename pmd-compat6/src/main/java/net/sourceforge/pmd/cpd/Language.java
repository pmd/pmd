/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0

package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;
import java.util.List;
import java.util.Properties;

public interface Language {
    String getName();

    String getTerseName();

    Tokenizer getTokenizer();

    FilenameFilter getFileFilter();

    void setProperties(Properties properties);

    List<String> getExtensions();
}

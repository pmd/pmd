/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;

public interface Tokenizer {
    String IGNORE_LITERALS = "ignore_literals";
    String IGNORE_IDENTIFIERS = "ignore_identifiers";
    String IGNORE_ANNOTATIONS = "ignore_annotations";

    void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException;
}

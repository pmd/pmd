/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.cpd;

import java.io.IOException;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.lang.document.TextDocument;

public class ApexCpdLexer implements CpdLexer {
    private final AntlrApexCpdLexer lexer = new AntlrApexCpdLexer();

    @Override
    public void tokenize(TextDocument document, TokenFactory tokenEntries) throws IOException {
        lexer.tokenize(document, tokenEntries);
    }
}

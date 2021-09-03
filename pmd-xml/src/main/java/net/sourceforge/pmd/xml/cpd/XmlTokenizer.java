/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.xml.cpd;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.xml.antlr4.XMLLexer;

public class XmlTokenizer extends AntlrTokenizer {

    @Override
    protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        return new AntlrTokenManager(new XMLLexer(charStream), sourceCode.getFileName());
    }
}

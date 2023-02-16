/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql.cpd;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.AntlrTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.antlr.AntlrTokenManager;
import net.sourceforge.pmd.lang.tsql.ast.TSqlLexer;

public class TSqlTokenizer extends AntlrTokenizer {

    @Override
    protected AntlrTokenManager getLexerForSource(SourceCode sourceCode) {
        CharStream charStream = AntlrTokenizer.getCharStreamFromSourceCode(sourceCode);
        charStream = new CaseChangingCharStream(charStream, true);
        return new AntlrTokenManager(new TSqlLexer(charStream), sourceCode.getFileName());
    }
}

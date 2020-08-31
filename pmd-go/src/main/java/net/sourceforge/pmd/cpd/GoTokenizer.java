/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.lang.go.ast.GolangLexer;

public class GoTokenizer extends AntlrTokenizer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new GolangLexer(charStream);
    }
}

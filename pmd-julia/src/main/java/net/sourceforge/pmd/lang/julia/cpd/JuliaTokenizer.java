/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.julia.cpd;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrTokenizer;
import net.sourceforge.pmd.lang.julia.ast.JuliaLexer;

/**
 * The Julia Tokenizer.
 */
public class JuliaTokenizer extends AntlrTokenizer {
    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new JuliaLexer(charStream);
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.css.cpd;

import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.css.ast.CssLexer;

public class CssCpdLexer extends AntlrCpdLexer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new CssLexer(charStream);
    }

    @Override
    protected String getImage(AntlrToken token) {
        // normalize case-insensitive tokens
        return token.getImage().toUpperCase(Locale.ROOT);
    }
}

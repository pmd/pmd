/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.css.cpd;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.css.ast.CssLexer;

import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

public class CssCpdLexer extends AntlrCpdLexer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        return new CssLexer(charStream);
    }

    @Override
    protected String getImage(AntlrToken token) {
        if (token.getKind() == CssLexer.STRING) {
            // This path is for case-sensitive tokens
            return token.getImage();
        }
        // normalize case-insensitive tokens
        return token.getImage().toUpperCase(Locale.ROOT);
    }
}
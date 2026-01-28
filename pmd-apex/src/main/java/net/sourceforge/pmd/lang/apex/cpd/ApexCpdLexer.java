/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.cpd;

import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

import net.sourceforge.pmd.cpd.impl.AntlrCpdLexer;
import net.sourceforge.pmd.cpd.impl.BaseTokenFilter;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.document.TextDocument;

import io.github.apexdevtools.apexparser.ApexLexer;
import io.github.apexdevtools.apexparser.CaseInsensitiveInputStream;

public class ApexCpdLexer extends AntlrCpdLexer {

    @Override
    protected Lexer getLexerForSource(CharStream charStream) {
        CaseInsensitiveInputStream caseInsensitiveInputStream = new CaseInsensitiveInputStream(charStream);
        return new ApexLexer(caseInsensitiveInputStream);
    }

    @Override
    protected TokenManager<AntlrToken> filterTokenStream(TokenManager<AntlrToken> tokenManager) {
        return new BaseTokenFilter<>(tokenManager);
    }
    
    @Override
    public void tokenize(TextDocument document, TokenFactory tokenFactory) {
        super.tokenize(document, tokenFactory);
    }

    @Override
    protected String getImage(AntlrToken token) {
        // be case-insensitive
        return token.getImage().toLowerCase(Locale.ROOT);
    }
}

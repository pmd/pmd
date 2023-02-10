/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Locale;

import org.antlr.v4.runtime.CharStream;

import net.sourceforge.pmd.cpd.internal.AntlrTokenizer;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;

public class ApexTokenizer extends AntlrTokenizer {
    private final boolean caseSensitive;

    public ApexTokenizer(ApexLanguageProperties properties) {
        this.caseSensitive = properties.getProperty(Tokenizer.CPD_CASE_SENSITIVE);
    }

    @Override
    protected String getImage(AntlrToken token) {
        if (caseSensitive) {
            return token.getImage();
        }
        return token.getImage().toLowerCase(Locale.ROOT);
    }

    @Override
    protected org.antlr.v4.runtime.Lexer getLexerForSource(CharStream charStream) {
        return new com.nawforce.runtime.parsers.ApexLexer(charStream);
    }
}

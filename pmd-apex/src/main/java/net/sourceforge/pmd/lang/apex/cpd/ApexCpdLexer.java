/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.cpd;

import java.io.IOException;
import java.util.Locale;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrToken;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrTokenManager;
import net.sourceforge.pmd.lang.document.TextDocument;

import io.github.apexdevtools.apexparser.ApexLexer;
import io.github.apexdevtools.apexparser.CaseInsensitiveInputStream;

public class ApexCpdLexer implements CpdLexer {
    @Override
    public void tokenize(TextDocument document, TokenFactory tokenEntries) throws IOException {

        CharStream charStream = CharStreams.fromReader(document.newReader());
        CaseInsensitiveInputStream caseInsensitiveInputStream = new CaseInsensitiveInputStream(charStream);
        ApexLexer lexer = new ApexLexer(caseInsensitiveInputStream);
        AntlrTokenManager tokenManager = new AntlrTokenManager(lexer, document);

        AntlrToken token = tokenManager.getNextToken();

        while (!token.isEof()) {
            if (token.isDefault()) { // excludes WHITESPACE_CHANNEL and COMMENT_CHANNEL
                String tokenText = token.getImage();
                // be case-insensitive
                tokenText = tokenText.toLowerCase(Locale.ROOT);
                tokenEntries.recordToken(tokenText, token.getReportLocation());
            }
            token = tokenManager.getNextToken();
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties;

/**
 * Creates a tokenizer, that uses the syntactic grammar to provide context
 * for the tokenizer when reducing the input characters to tokens.
 *
 * @deprecated This implementation has been superseded. It is not necessary to parse Java code in order to tokenize it.
 */
@Deprecated
public final class SyntacticJavaTokenizerFactory {
    private SyntacticJavaTokenizerFactory() {
        // factory class
    }

    @Deprecated
    public static TokenManager<JavaccToken> createTokenizer(CharStream cs) {
        final List<JavaccToken> tokenList = new ArrayList<>();
        JavaParserImplTokenManager tokenManager = new JavaParserImplTokenManager(cs) {
            @Override
            public JavaccToken getNextToken() {
                JavaccToken token = super.getNextToken();
                tokenList.add(token);
                return token;
            }
        };

        LanguageVersion latestVersion = JavaLanguageModule.getInstance().getLatestVersion();
        JavaParserImpl parser = new JavaParserImpl(tokenManager);
        parser.setJdkVersion(JavaLanguageProperties.getInternalJdkVersion(latestVersion));
        parser.setPreview(JavaLanguageProperties.isPreviewEnabled(latestVersion));

        ASTCompilationUnit compilationUnit = parser.CompilationUnit();
        assert compilationUnit != null;

        return new TokenManager<JavaccToken>() {
            Iterator<JavaccToken> iterator = tokenList.iterator();

            @Override
            public JavaccToken getNextToken() {
                return iterator.next();
            }
        };
    }
}

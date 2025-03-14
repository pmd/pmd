/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.cpd;

import net.sourceforge.pmd.cpd.impl.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaTokenKinds;


/**
 * <p>Note: This class has been called MatlabTokenizer in PMD 6</p>.
 */
public class ModelicaCpdLexer extends JavaccCpdLexer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return ModelicaTokenKinds.newTokenManager(CharStream.create(doc));
    }

    @Override
    protected TokenManager<JavaccToken> filterTokenStream(TokenManager<JavaccToken> tokenManager) {
        return new ModelicaTokenFilter(tokenManager);
    }

    public static class ModelicaTokenFilter extends JavaCCTokenFilter {
        private boolean discardingWithinAndImport = false;
        private boolean discardingAnnotation = false;

        ModelicaTokenFilter(TokenManager<JavaccToken> tokenManager) {
            super(tokenManager);
        }

        private void skipWithinAndImport(JavaccToken currentToken) {
            final int type = currentToken.kind;
            if (type == ModelicaTokenKinds.IMPORT || type == ModelicaTokenKinds.WITHIN) {
                discardingWithinAndImport = true;
            } else if (discardingWithinAndImport && type == ModelicaTokenKinds.SC) {
                discardingWithinAndImport = false;
            }
        }

        private void skipAnnotation(JavaccToken currentToken) {
            final int type = currentToken.kind;
            if (type == ModelicaTokenKinds.ANNOTATION) {
                discardingAnnotation = true;
            } else if (discardingAnnotation && type == ModelicaTokenKinds.SC) {
                discardingAnnotation = false;
            }
        }

        @Override
        protected void analyzeToken(JavaccToken currentToken) {
            skipWithinAndImport(currentToken);
            skipAnnotation(currentToken);
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingWithinAndImport || discardingAnnotation;
        }
    }
}

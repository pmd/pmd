/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.io.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaTokenKinds;


public class ModelicaTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return ModelicaTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected JavaCCTokenFilter getTokenFilter(TokenManager<JavaccToken> tokenManager) {
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

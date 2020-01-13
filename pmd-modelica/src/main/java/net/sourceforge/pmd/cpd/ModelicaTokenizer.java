/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaTokenKinds;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaTokenManager;


public class ModelicaTokenizer extends JavaCCTokenizer {
    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        final StringBuilder stringBuilder = sourceCode.getCodeBuffer();
        return new ModelicaTokenManager(new StringReader(stringBuilder.toString()));
    }

    @Override
    protected JavaCCTokenFilter getTokenFilter(TokenManager tokenManager) {
        return new ModelicaTokenFilter(tokenManager);
    }

    public static class ModelicaTokenFilter extends JavaCCTokenFilter {
        private boolean discardingWithinAndImport = false;
        private boolean discardingAnnotation = false;

        ModelicaTokenFilter(TokenManager tokenManager) {
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
        protected void analyzeToken(GenericToken currentToken) {
            skipWithinAndImport((JavaccToken) currentToken);
            skipAnnotation((JavaccToken) currentToken);
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingWithinAndImport || discardingAnnotation;
        }
    }
}

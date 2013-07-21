/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.Properties;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.java.ast.JavaParserConstants;
import net.sourceforge.pmd.lang.java.ast.Token;

public class JavaTokenizer implements Tokenizer {

    public static final String IGNORE_LITERALS = "ignore_literals";
    public static final String IGNORE_IDENTIFIERS = "ignore_identifiers";
    public static final String IGNORE_ANNOTATIONS = "ignore_annotations";
    public static final String CPD_START = "\"CPD-START\"";
    public static final String CPD_END = "\"CPD-END\"";

    private boolean ignoreAnnotations;
    private boolean ignoreLiterals;
    private boolean ignoreIdentifiers;

    public void setProperties(Properties properties) {
        ignoreAnnotations = Boolean.parseBoolean(properties.getProperty(IGNORE_ANNOTATIONS, "false"));
        ignoreLiterals = Boolean.parseBoolean(properties.getProperty(IGNORE_LITERALS, "false"));
        ignoreIdentifiers = Boolean.parseBoolean(properties.getProperty(IGNORE_IDENTIFIERS, "false"));
    }

    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder stringBuilder = sourceCode.getCodeBuffer();

        // Note that Java version is irrelevant for tokenizing
        LanguageVersionHandler languageVersionHandler = LanguageVersion.JAVA_14.getLanguageVersionHandler();
        String fileName = sourceCode.getFileName();
        TokenManager tokenMgr = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions()).getTokenManager(
                fileName, new StringReader(stringBuilder.toString()));
        Token currentToken = (Token) tokenMgr.getNextToken();

        TokenDiscarder discarder = new TokenDiscarder(ignoreAnnotations);

        while (currentToken.image.length() > 0) {
            discarder.updateState(currentToken);

            if (discarder.isDiscarding()) {
                currentToken = (Token) tokenMgr.getNextToken();
                continue;
            }

            processToken(tokenEntries, fileName, currentToken);
            currentToken = (Token) tokenMgr.getNextToken();
        }
        tokenEntries.add(TokenEntry.getEOF());
    }

    private void processToken(Tokens tokenEntries, String fileName, Token currentToken) {
        String image = currentToken.image;
        if (ignoreLiterals
                && (currentToken.kind == JavaParserConstants.STRING_LITERAL
                || currentToken.kind == JavaParserConstants.CHARACTER_LITERAL
                || currentToken.kind == JavaParserConstants.DECIMAL_LITERAL
                || currentToken.kind == JavaParserConstants.FLOATING_POINT_LITERAL)) {
            image = String.valueOf(currentToken.kind);
        }
        if (ignoreIdentifiers && currentToken.kind == JavaParserConstants.IDENTIFIER) {
            image = String.valueOf(currentToken.kind);
        }
        tokenEntries.add(new TokenEntry(image, fileName, currentToken.beginLine));
    }

    public void setIgnoreLiterals(boolean ignore) {
        this.ignoreLiterals = ignore;
    }

    public void setIgnoreIdentifiers(boolean ignore) {
        this.ignoreIdentifiers = ignore;
    }

    public void setIgnoreAnnotations(boolean ignoreAnnotations) {
        this.ignoreAnnotations = ignoreAnnotations;
    }

    /**
     * The {@link TokenDiscarder} consumes token by token and maintains state.
     * It can detect, whether the current token belongs to an annotation and whether
     * the current token should be discarded by CPD.
     * <p>
     * By default, it discards semicolons, package and import statements, and enables CPD suppression.
     * Optionally, all annotations can be ignored, too.
     * </p>
     */
    private static class TokenDiscarder {
        private boolean isAnnotation = false;
        private boolean nextTokenEndsAnnotation = false;
        private int annotationStack = 0;

        private boolean discardingSemicolon = false;
        private boolean discardingKeywords = false;
        private boolean discardingSuppressing = false;
        private boolean discardingAnnotations = false;
        private boolean ignoreAnnotations = false;

        public TokenDiscarder(boolean ignoreAnnotations) {
            this.ignoreAnnotations = ignoreAnnotations;
        }

        public void updateState(Token currentToken) {
            detectAnnotations(currentToken);

            skipSemicolon(currentToken);
            skipPackageAndImport(currentToken);
            skipCPDSuppression(currentToken);
            if (ignoreAnnotations) {
                skipAnnotations();
            }
        }

        public void skipPackageAndImport(Token currentToken) {
            if (currentToken.kind == JavaParserConstants.PACKAGE || currentToken.kind == JavaParserConstants.IMPORT) {
                discardingKeywords = true;
            } else if (discardingKeywords && currentToken.kind == JavaParserConstants.SEMICOLON) {
                discardingKeywords = false;
            }
        }

        public void skipSemicolon(Token currentToken) {
            if (currentToken.kind == JavaParserConstants.SEMICOLON) {
                discardingSemicolon = true;
            } else if (discardingSemicolon && currentToken.kind != JavaParserConstants.SEMICOLON) {
                discardingSemicolon = false;
            }
        }

        public void skipCPDSuppression(Token currentToken) {
            //if processing an annotation, look for a CPD-START or CPD-END
            if (isAnnotation) {
                if (!discardingSuppressing && currentToken.kind == JavaParserConstants.STRING_LITERAL && CPD_START.equals(currentToken.image)) {
                    discardingSuppressing = true;
                } else if (discardingSuppressing && currentToken.kind == JavaParserConstants.STRING_LITERAL && CPD_END.equals(currentToken.image)) {
                    discardingSuppressing = false;
                }
            }
        }

        public void skipAnnotations() {
            if (!discardingAnnotations && isAnnotation) {
                discardingAnnotations = true;
            } else if (discardingAnnotations && !isAnnotation) {
                discardingAnnotations = false;
            }
        }

        public boolean isDiscarding() {
            boolean result = discardingSemicolon || discardingKeywords || discardingAnnotations || discardingSuppressing;
            return result;
        }

        public void detectAnnotations(Token currentToken) {
            if (isAnnotation && nextTokenEndsAnnotation) {
                isAnnotation = false;
                nextTokenEndsAnnotation = false;
            }
            if (isAnnotation) {
                if (currentToken.kind == JavaParserConstants.LPAREN) {
                    annotationStack++;
                } else if (currentToken.kind == JavaParserConstants.RPAREN) {
                    annotationStack--;
                    if (annotationStack == 0) {
                        nextTokenEndsAnnotation = true;
                    }
                } else if (annotationStack == 0 && currentToken.kind != JavaParserConstants.IDENTIFIER &&  currentToken.kind != JavaParserConstants.LPAREN) {
                    isAnnotation = false;
                }
            }
            if (currentToken.kind == JavaParserConstants.AT) {
                isAnnotation = true;
            }
        }
    }
}

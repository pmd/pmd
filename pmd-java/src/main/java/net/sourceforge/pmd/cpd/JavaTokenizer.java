/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Properties;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.JavaParserConstants;
import net.sourceforge.pmd.lang.java.ast.Token;

public class JavaTokenizer implements Tokenizer {

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
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getVersion("1.4").getLanguageVersionHandler();
        String fileName = sourceCode.getFileName();
        TokenManager tokenMgr = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions())
                .getTokenManager(fileName, new StringReader(stringBuilder.toString()));
        Token currentToken = (Token) tokenMgr.getNextToken();

        TokenDiscarder discarder = new TokenDiscarder(ignoreAnnotations);
        ConstructorDetector constructorDetector = new ConstructorDetector(ignoreIdentifiers);

        while (currentToken.image.length() > 0) {
            discarder.updateState(currentToken);

            if (discarder.isDiscarding()) {
                currentToken = (Token) tokenMgr.getNextToken();
                continue;
            }

            processToken(tokenEntries, fileName, currentToken, constructorDetector);
            currentToken = (Token) tokenMgr.getNextToken();
        }
        tokenEntries.add(TokenEntry.getEOF());
    }

    private void processToken(Tokens tokenEntries, String fileName, Token currentToken,
            ConstructorDetector constructorDetector) {
        String image = currentToken.image;

        constructorDetector.restoreConstructorToken(tokenEntries, currentToken);

        if (ignoreLiterals && (currentToken.kind == JavaParserConstants.STRING_LITERAL
                || currentToken.kind == JavaParserConstants.CHARACTER_LITERAL
                || currentToken.kind == JavaParserConstants.DECIMAL_LITERAL
                || currentToken.kind == JavaParserConstants.FLOATING_POINT_LITERAL)) {
            image = String.valueOf(currentToken.kind);
        }
        if (ignoreIdentifiers && currentToken.kind == JavaParserConstants.IDENTIFIER) {
            image = String.valueOf(currentToken.kind);
        }

        constructorDetector.processToken(currentToken);

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
     * It can detect, whether the current token belongs to an annotation and
     * whether the current token should be discarded by CPD.
     * <p>
     * By default, it discards semicolons, package and import statements, and
     * enables CPD suppression. Optionally, all annotations can be ignored, too.
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

        TokenDiscarder(boolean ignoreAnnotations) {
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
            // if processing an annotation, look for a CPD-START or CPD-END
            if (isAnnotation) {
                if (!discardingSuppressing && currentToken.kind == JavaParserConstants.STRING_LITERAL
                        && CPD_START.equals(currentToken.image)) {
                    discardingSuppressing = true;
                } else if (discardingSuppressing && currentToken.kind == JavaParserConstants.STRING_LITERAL
                        && CPD_END.equals(currentToken.image)) {
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
            boolean result = discardingSemicolon || discardingKeywords || discardingAnnotations
                    || discardingSuppressing;
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
                } else if (annotationStack == 0 && currentToken.kind != JavaParserConstants.IDENTIFIER
                        && currentToken.kind != JavaParserConstants.LPAREN) {
                    isAnnotation = false;
                }
            }
            if (currentToken.kind == JavaParserConstants.AT) {
                isAnnotation = true;
            }
        }
    }

    /**
     * The {@link ConstructorDetector} consumes token by token and maintains
     * state. It can detect, whether the current token belongs to a constructor
     * method identifier and if so, is able to restore it when using
     * ignoreIdentifiers.
     */
    private static class ConstructorDetector {
        private boolean ignoreIdentifiers;

        private Deque<TypeDeclaration> classMembersIndentations;
        private int currentNestingLevel;
        private boolean storeNextIdentifier;
        private String prevIdentifier;

        ConstructorDetector(boolean ignoreIdentifiers) {
            this.ignoreIdentifiers = ignoreIdentifiers;

            currentNestingLevel = 0;
            classMembersIndentations = new LinkedList<TypeDeclaration>();
        }

        public void processToken(Token currentToken) {
            if (!ignoreIdentifiers) {
                return;
            }

            switch (currentToken.kind) {
            case JavaParserConstants.IDENTIFIER:
                if ("enum".equals(currentToken.image)) {
                    // If declaring an enum, add a new block nesting level at
                    // which constructors may exist
                    pushTypeDeclaration();
                } else if (storeNextIdentifier) {
                    classMembersIndentations.peek().name = currentToken.image;
                    storeNextIdentifier = false;
                }

                // Store this token
                prevIdentifier = currentToken.image;
                break;

            case JavaParserConstants.CLASS:
                // If declaring a class, add a new block nesting level at which
                // constructors may exist
                pushTypeDeclaration();
                break;

            case JavaParserConstants.LBRACE:
                currentNestingLevel++;
                break;

            case JavaParserConstants.RBRACE:
                // Discard completed blocks
                if (!classMembersIndentations.isEmpty()
                        && classMembersIndentations.peek().indentationLevel == currentNestingLevel) {
                    classMembersIndentations.pop();
                }
                currentNestingLevel--;
                break;

            default:
                /*
                 * Did we find a "class" token not followed by an identifier? i.e:
                 * expectThrows(IllegalStateException.class, () -> {
                 *  newSearcher(r).search(parentQuery.build(), c);
                 * });
                 */
                if (storeNextIdentifier) {
                    classMembersIndentations.pop();
                    storeNextIdentifier = false;
                }
                break;
            }
        }

        private void pushTypeDeclaration() {
            TypeDeclaration cd = new TypeDeclaration(currentNestingLevel + 1);
            classMembersIndentations.push(cd);
            storeNextIdentifier = true;
        }

        public void restoreConstructorToken(Tokens tokenEntries, Token currentToken) {
            if (!ignoreIdentifiers) {
                return;
            }

            if (currentToken.kind == JavaParserConstants.LPAREN) {
                // was the previous token a constructor? If so, restore the
                // identifier
                if (!classMembersIndentations.isEmpty()
                        && classMembersIndentations.peek().name.equals(prevIdentifier)) {
                    int lastTokenIndex = tokenEntries.size() - 1;
                    TokenEntry lastToken = tokenEntries.getTokens().get(lastTokenIndex);
                    lastToken.setImage(prevIdentifier);
                }
            }
        }
    }

    private static class TypeDeclaration {
        int indentationLevel;
        String name;

        TypeDeclaration(int indentationLevel) {
            this.indentationLevel = indentationLevel;
        }
    }
}

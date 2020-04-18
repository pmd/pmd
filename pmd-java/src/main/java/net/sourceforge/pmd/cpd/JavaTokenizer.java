/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Properties;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.cpd.token.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.token.TokenFilter;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.util.document.TextDocument;

public class JavaTokenizer extends JavaCCTokenizer {

    public static final String CPD_START = "\"CPD-START\"";
    public static final String CPD_END = "\"CPD-END\"";

    private boolean ignoreAnnotations;
    private boolean ignoreLiterals;
    private boolean ignoreIdentifiers;

    private ConstructorDetector constructorDetector;

    public void setProperties(Properties properties) {
        ignoreAnnotations = Boolean.parseBoolean(properties.getProperty(IGNORE_ANNOTATIONS, "false"));
        ignoreLiterals = Boolean.parseBoolean(properties.getProperty(IGNORE_LITERALS, "false"));
        ignoreIdentifiers = Boolean.parseBoolean(properties.getProperty(IGNORE_IDENTIFIERS, "false"));
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException {
        constructorDetector = new ConstructorDetector(ignoreIdentifiers);
        super.tokenize(sourceCode, tokenEntries);
    }

    @Override
    protected JavaccTokenDocument newTokenDoc(TextDocument textDoc) {
        return InternalApiBridge.javaTokenDoc(textDoc);
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(CharStream sourceCode) {
        return JavaTokenKinds.newTokenManager(sourceCode);
    }

    @Override
    protected TokenFilter<JavaccToken> getTokenFilter(TokenManager<JavaccToken> tokenManager) {
        return new JavaTokenFilter(tokenManager, ignoreAnnotations);
    }

    @Override
    protected TokenEntry processToken(Tokens tokenEntries, JavaccToken javaToken, String fileName) {
        String image = javaToken.getImage();

        constructorDetector.restoreConstructorToken(tokenEntries, javaToken);

        if (ignoreLiterals && (javaToken.kind == JavaTokenKinds.STRING_LITERAL
                || javaToken.kind == JavaTokenKinds.CHARACTER_LITERAL
                || javaToken.kind == JavaTokenKinds.INTEGER_LITERAL
                || javaToken.kind == JavaTokenKinds.FLOATING_POINT_LITERAL)) {
            image = String.valueOf(javaToken.kind);
        }
        if (ignoreIdentifiers && javaToken.kind == JavaTokenKinds.IDENTIFIER) {
            image = String.valueOf(javaToken.kind);
        }

        constructorDetector.processToken(javaToken);

        return new TokenEntry(image, fileName, javaToken.getReportLocation());
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
     * The {@link JavaTokenFilter} extends the {@link JavaCCTokenFilter} to discard
     * Java-specific tokens.
     * <p>
     * By default, it discards semicolons, package and import statements, and
     * enables annotation-based CPD suppression. Optionally, all annotations can be ignored, too.
     * </p>
     */
    private static class JavaTokenFilter extends JavaCCTokenFilter {
        private boolean isAnnotation = false;
        private boolean nextTokenEndsAnnotation = false;
        private int annotationStack = 0;

        private boolean discardingSemicolon = false;
        private boolean discardingKeywords = false;
        private boolean discardingSuppressing = false;
        private boolean discardingAnnotations = false;
        private boolean ignoreAnnotations = false;

        JavaTokenFilter(final TokenManager<JavaccToken> tokenManager, final boolean ignoreAnnotations) {
            super(tokenManager);
            this.ignoreAnnotations = ignoreAnnotations;
        }

        @Override
        protected void analyzeToken(final JavaccToken token) {
            detectAnnotations(token);

            skipSemicolon(token);
            skipPackageAndImport(token);
            skipAnnotationSuppression(token);
            if (ignoreAnnotations) {
                skipAnnotations();
            }
        }

        private void skipPackageAndImport(final JavaccToken currentToken) {
            if (currentToken.kind == JavaTokenKinds.PACKAGE || currentToken.kind == JavaTokenKinds.IMPORT) {
                discardingKeywords = true;
            } else if (discardingKeywords && currentToken.kind == JavaTokenKinds.SEMICOLON) {
                discardingKeywords = false;
            }
        }

        private void skipSemicolon(final JavaccToken currentToken) {
            if (currentToken.kind == JavaTokenKinds.SEMICOLON) {
                discardingSemicolon = true;
            } else if (discardingSemicolon) {
                discardingSemicolon = false;
            }
        }

        private void skipAnnotationSuppression(final JavaccToken currentToken) {
            // if processing an annotation, look for a CPD-START or CPD-END
            if (isAnnotation) {
                if (!discardingSuppressing && currentToken.kind == JavaTokenKinds.STRING_LITERAL
                        && CPD_START.equals(currentToken.getImage())) {
                    discardingSuppressing = true;
                } else if (discardingSuppressing && currentToken.kind == JavaTokenKinds.STRING_LITERAL
                        && CPD_END.equals(currentToken.getImage())) {
                    discardingSuppressing = false;
                }
            }
        }

        private void skipAnnotations() {
            if (!discardingAnnotations && isAnnotation) {
                discardingAnnotations = true;
            } else if (discardingAnnotations && !isAnnotation) {
                discardingAnnotations = false;
            }
        }

        @Override
        protected boolean isLanguageSpecificDiscarding() {
            return discardingSemicolon || discardingKeywords || discardingAnnotations
                    || discardingSuppressing;
        }

        private void detectAnnotations(JavaccToken currentToken) {
            if (isAnnotation && nextTokenEndsAnnotation) {
                isAnnotation = false;
                nextTokenEndsAnnotation = false;
            }
            if (isAnnotation) {
                if (currentToken.kind == JavaTokenKinds.LPAREN) {
                    annotationStack++;
                } else if (currentToken.kind == JavaTokenKinds.RPAREN) {
                    annotationStack--;
                    if (annotationStack == 0) {
                        nextTokenEndsAnnotation = true;
                    }
                } else if (annotationStack == 0 && currentToken.kind != JavaTokenKinds.IDENTIFIER
                        && currentToken.kind != JavaTokenKinds.LPAREN) {
                    isAnnotation = false;
                }
            }
            if (currentToken.kind == JavaTokenKinds.AT) {
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
            classMembersIndentations = new LinkedList<>();
        }

        public void processToken(JavaccToken currentToken) {
            if (!ignoreIdentifiers) {
                return;
            }

            switch (currentToken.kind) {
            case JavaTokenKinds.IDENTIFIER:
                if ("enum".equals(currentToken.getImage())) {
                    // If declaring an enum, add a new block nesting level at
                    // which constructors may exist
                    pushTypeDeclaration();
                } else if (storeNextIdentifier) {
                    classMembersIndentations.peek().name = currentToken.getImage();
                    storeNextIdentifier = false;
                }

                // Store this token
                prevIdentifier = currentToken.getImage();
                break;

            case JavaTokenKinds.CLASS:
                // If declaring a class, add a new block nesting level at which
                // constructors may exist
                pushTypeDeclaration();
                break;

            case JavaTokenKinds.LBRACE:
                currentNestingLevel++;
                break;

            case JavaTokenKinds.RBRACE:
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

        public void restoreConstructorToken(Tokens tokenEntries, JavaccToken currentToken) {
            if (!ignoreIdentifiers) {
                return;
            }

            if (currentToken.kind == JavaTokenKinds.LPAREN) {
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

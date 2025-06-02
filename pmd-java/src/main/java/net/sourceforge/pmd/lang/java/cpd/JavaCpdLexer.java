/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.cpd;

import java.util.Deque;
import java.util.LinkedList;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.cpd.impl.JavaCCTokenFilter;
import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaTokenKinds;
import net.sourceforge.pmd.lang.java.internal.JavaLanguageProperties;

/**
 * <p>Note: This class has been called JavaTokenizer in PMD 6</p>.
 */
public class JavaCpdLexer extends JavaccCpdLexer {

    private static final String CPD_START = "\"CPD-START\"";
    private static final String CPD_END = "\"CPD-END\"";

    private final boolean ignoreAnnotations;
    private final boolean ignoreLiterals;
    private final boolean ignoreIdentifiers;

    private final ConstructorDetector constructorDetector;

    public JavaCpdLexer(JavaLanguageProperties properties) {
        ignoreAnnotations = properties.getProperty(CpdLanguageProperties.CPD_IGNORE_METADATA);
        ignoreLiterals = properties.getProperty(CpdLanguageProperties.CPD_ANONYMIZE_LITERALS);
        ignoreIdentifiers = properties.getProperty(CpdLanguageProperties.CPD_ANONYMIZE_IDENTIFIERS);
        constructorDetector = new ConstructorDetector(ignoreIdentifiers);
    }

    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) {
        return JavaTokenKinds.newTokenManager(CharStream.create(doc, InternalApiBridge.javaTokenDoc()));
    }

    @Override
    protected TokenManager<JavaccToken> filterTokenStream(TokenManager<JavaccToken> tokenManager) {
        return new JavaTokenFilter(tokenManager, ignoreAnnotations);
    }

    @Override
    protected void processToken(TokenFactory tokenEntries, JavaccToken javaToken) {
        String image = javaToken.getImage();

        constructorDetector.restoreConstructorToken(tokenEntries, javaToken);

        if (ignoreLiterals && (javaToken.kind == JavaTokenKinds.STRING_LITERAL
                || javaToken.kind == JavaTokenKinds.CHARACTER_LITERAL
                || javaToken.kind == JavaTokenKinds.INTEGER_LITERAL
                || javaToken.kind == JavaTokenKinds.FLOATING_POINT_LITERAL)) {
            image = JavaTokenKinds.describe(javaToken.kind);
        }
        if (ignoreIdentifiers && javaToken.kind == JavaTokenKinds.IDENTIFIER) {
            image = JavaTokenKinds.describe(javaToken.kind);
        }

        constructorDetector.processToken(javaToken);

        tokenEntries.recordToken(image, javaToken.getReportLocation());
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
        private final boolean ignoreIdentifiers;

        private final Deque<TypeDeclaration> classMembersIndentations;
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

        public void restoreConstructorToken(TokenFactory tokenEntries, JavaccToken currentToken) {
            if (!ignoreIdentifiers) {
                return;
            }

            if (currentToken.kind == JavaTokenKinds.LPAREN) {
                // was the previous token a constructor? If so, restore the
                // identifier
                if (!classMembersIndentations.isEmpty()
                        && classMembersIndentations.peek().name.equals(prevIdentifier)) {
                    TokenEntry lastToken = tokenEntries.peekLastToken();
                    tokenEntries.setImage(lastToken, prevIdentifier);
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

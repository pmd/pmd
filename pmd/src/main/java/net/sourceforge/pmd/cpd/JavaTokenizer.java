/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.StringReader;
import java.util.*;

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
    List<Discarder> discarders = new ArrayList<Discarder>();


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

        initDiscarders();

        while (currentToken.image.length() > 0) {
            for (Discarder discarder : discarders) {
                discarder.add(currentToken);
            }

            if (inDiscardingState()) {
                currentToken = (Token) tokenMgr.getNextToken();
                continue;
            }

            //skip semicolons
            if (currentToken.kind != JavaParserConstants.SEMICOLON) {
                processToken(tokenEntries, fileName, currentToken);
            }
            currentToken = (Token) tokenMgr.getNextToken();
        }
        tokenEntries.add(TokenEntry.getEOF());
    }

    private void processToken(Tokens tokenEntries, String fileName, Token currentToken) {
        String image = currentToken.image;
        if (ignoreLiterals
                && (currentToken.kind == JavaParserConstants.STRING_LITERAL
                || currentToken.kind == JavaParserConstants.CHARACTER_LITERAL
                || currentToken.kind == JavaParserConstants.DECIMAL_LITERAL || currentToken.kind == JavaParserConstants.FLOATING_POINT_LITERAL)) {
            image = String.valueOf(currentToken.kind);
        }
        if (ignoreIdentifiers && currentToken.kind == JavaParserConstants.IDENTIFIER) {
            image = String.valueOf(currentToken.kind);
        }
        tokenEntries.add(new TokenEntry(image, fileName, currentToken.beginLine));
    }

    private void initDiscarders() {
        if (ignoreAnnotations)
            discarders.add(new AnnotationStateDiscarder());
        discarders.add(new SuppressCPDDiscarder());
        discarders.add(new KeyWordToSemiColonStateDiscarder(JavaParserConstants.IMPORT));
        discarders.add(new KeyWordToSemiColonStateDiscarder(JavaParserConstants.PACKAGE));
    }

    private boolean inDiscardingState() {
        boolean discarding = false;
        for (Discarder discarder : discarders) {
            if (discarder.isDiscarding())
                discarding = true;
        }
        return discarding;
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

    static public interface Discarder {
        public void add(Token token);

        public boolean isDiscarding();
    }

    static public class AnnotationStateDiscarder implements Discarder {

        Stack<Token> tokenStack = new Stack<Token>();

        public void add(Token token) {
            if (isDiscarding() && tokenStack.size() == 2 && token.kind != JavaParserConstants.LPAREN) {
                tokenStack.clear();
            }

            if (token.kind == JavaParserConstants.AT && !isDiscarding()) {
                tokenStack.push(token);
                return;
            }
            if (token.kind == JavaParserConstants.RPAREN && isDiscarding()) {
                Token popped = null;
                while ((popped = tokenStack.pop()).kind != JavaParserConstants.LPAREN) ;
                return;

            } else {
                if (isDiscarding())
                    tokenStack.push(token);
            }
        }

        public boolean isDiscarding() {
            return !tokenStack.isEmpty();
        }

    }

    static public class KeyWordToSemiColonStateDiscarder implements Discarder {

        private final int keyword;
        Stack<Token> tokenStack = new Stack<Token>();

        public KeyWordToSemiColonStateDiscarder(int keyword) {
            this.keyword = keyword;
        }

        public void add(Token token) {
            if (token.kind == keyword)
                tokenStack.add(token);
            if (token.kind == JavaParserConstants.SEMICOLON && isDiscarding())
                tokenStack.clear();
        }

        public boolean isDiscarding() {
            return !tokenStack.isEmpty();
        }

    }

    static public class SuppressCPDDiscarder implements Discarder {
        AnnotationStateDiscarder asm = new AnnotationStateDiscarder();
        Boolean discarding = false;

        public void add(Token token) {
            asm.add(token);
            //if processing an annotation, look for a CPD-START or CPD-END
            if (asm.isDiscarding()) {
                if (CPD_START.equals(token.image))
                    discarding = true;
                if (CPD_END.equals(token.image) && discarding)
                    discarding = false;
            }
        }

        public boolean isDiscarding() {
            return discarding;
        }

    }

}

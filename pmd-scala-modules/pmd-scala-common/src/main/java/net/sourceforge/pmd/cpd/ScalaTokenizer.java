/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.cpd.token.internal.BaseTokenFilter;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.TokenMgrError;
import net.sourceforge.pmd.lang.scala.ScalaLanguageHandler;
import net.sourceforge.pmd.lang.scala.ScalaLanguageModule;

import scala.collection.Iterator;
import scala.meta.Dialect;
import scala.meta.inputs.Input;
import scala.meta.inputs.Position;
import scala.meta.internal.tokenizers.ScalametaTokenizer;
import scala.meta.tokenizers.TokenizeException;
import scala.meta.tokens.Token;

/**
 * Scala Tokenizer class. Uses the Scala Meta Tokenizer, but adapts it for use with generic filtering
 */
public class ScalaTokenizer implements Tokenizer {

    /**
     * Denotes the version of the scala dialect to use. Based on the values in
     * {@linkplain ScalaLanguageModule#getVersions()}
     */
    public static final String SCALA_VERSION_PROPERTY = "net.sourceforge.pmd.scala.version";
    private final Dialect dialect;

    /**
     * Create the Tokenizer using properties from the system environment.
     */
    public ScalaTokenizer() {
        this(System.getProperties());
    }

    /**
     * Create the Tokenizer given a set of properties.
     *
     * @param properties
     *            the {@linkplain Properties} object to use
     */
    public ScalaTokenizer(Properties properties) {
        String scalaVersion = properties.getProperty(SCALA_VERSION_PROPERTY);
        LanguageVersion langVer;
        if (scalaVersion == null) {
            langVer = LanguageRegistry.getLanguage(ScalaLanguageModule.NAME).getDefaultVersion();
        } else {
            langVer = LanguageRegistry.getLanguage(ScalaLanguageModule.NAME).getVersion(scalaVersion);
        }
        dialect = ((ScalaLanguageHandler) langVer.getLanguageVersionHandler()).getDialect();
    }

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException {
        String filename = sourceCode.getFileName();
        // create the full code file
        String fullCode = StringUtils.join(sourceCode.getCode(), "\n");

        // create the input file for scala
        Input.VirtualFile vf = new Input.VirtualFile(filename, fullCode);
        ScalametaTokenizer tokenizer = new ScalametaTokenizer(vf, dialect);

        // tokenize with a filter
        try {
            scala.meta.tokens.Tokens tokens = tokenizer.tokenize();

            // use extensions to the standard PMD TokenManager and Filter
            ScalaTokenManager scalaTokenManager = new ScalaTokenManager(tokens.iterator());
            ScalaTokenFilter filter = new ScalaTokenFilter(scalaTokenManager);

            ScalaTokenAdapter token;
            while ((token = filter.getNextToken()) != null) {
                if (StringUtils.isEmpty(token.getImage())) {
                    continue;
                }
                TokenEntry cpdToken = new TokenEntry(token.getImage(),
                                                     filename,
                                                     token.getBeginLine(),
                                                     token.getBeginColumn(),
                                                     token.getEndColumn());
                tokenEntries.add(cpdToken);
            }
        } catch (Exception e) {
            if (e instanceof TokenizeException) { // NOPMD
                // cannot catch it as it's a checked exception and Scala sneaky throws
                TokenizeException tokE = (TokenizeException) e;
                Position pos = tokE.pos();
                throw new TokenMgrError(pos.startLine() + 1, pos.startColumn() + 1, filename, "Scalameta threw", tokE);
            } else {
                throw e;
            }
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }

    }

    /**
     * Implementation of the generic Token Manager, also skips un-helpful tokens and comments to only register important tokens
     * and patterns.
     *
     * Keeps track of comments, for special comment processing
     */
    private static class ScalaTokenManager implements TokenManager<ScalaTokenAdapter> {

        Iterator<Token> tokenIter;
        Class<?>[] skippableTokens = new Class<?>[] { Token.Space.class, Token.Tab.class, Token.CR.class,
            Token.LF.class, Token.FF.class, Token.LFLF.class, Token.EOF.class, Token.Comment.class };

        ScalaTokenAdapter previousComment = null;

        ScalaTokenManager(Iterator<Token> iterator) {
            this.tokenIter = iterator;
        }

        @Override
        public ScalaTokenAdapter getNextToken() {
            if (!tokenIter.hasNext()) {
                return null;
            }

            Token token;
            do {
                token = tokenIter.next();
                if (isComment(token)) {
                    previousComment = new ScalaTokenAdapter(token, previousComment);
                }
            } while (token != null && skipToken(token) && tokenIter.hasNext());

            return new ScalaTokenAdapter(token, previousComment);
        }

        private boolean skipToken(Token token) {
            boolean skip = false;
            if (token.text() != null) {
                for (Class<?> skipTokenClazz : skippableTokens) {
                    skip |= skipTokenClazz.isInstance(token);
                }
            }
            return skip;
        }

        private boolean isComment(Token token) {
            return token instanceof Token.Comment;
        }
    }

    private static class ScalaTokenFilter extends BaseTokenFilter<ScalaTokenAdapter> {
        ScalaTokenFilter(TokenManager<ScalaTokenAdapter> tokenManager) {
            super(tokenManager);
        }

        @Override
        protected boolean shouldStopProcessing(ScalaTokenAdapter currentToken) {
            return currentToken == null;
        }

    }

}

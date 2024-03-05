/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.cpd;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.TokenFactory;
import net.sourceforge.pmd.cpd.impl.BaseTokenFilter;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.scala.internal.ScalaDialect;

import scala.collection.Iterator;
import scala.meta.Dialect;
import scala.meta.inputs.Input;
import scala.meta.inputs.Position;
import scala.meta.internal.tokenizers.ScalametaTokenizer;
import scala.meta.tokenizers.TokenizeException;
import scala.meta.tokens.Token;

/**
 * Scala Tokenizer class. Uses the Scala Meta Tokenizer, but adapts it for use with generic filtering
 *
 * <p>Note: This class has been called ScalaTokenizer in PMD 6</p>.
 */
public class ScalaCpdLexer implements CpdLexer {

    private final Dialect dialect;

    /**
     * Create the Tokenizer using properties from the system environment.
     */
    public ScalaCpdLexer(LanguagePropertyBundle bundle) {
        LanguageVersion langVer = bundle.getLanguageVersion();
        dialect = ScalaDialect.dialectOf(langVer);
    }

    @Override
    public void tokenize(TextDocument document, TokenFactory tokenEntries) {


        try {
            String fullCode = document.getText().toString();

            // create the input file for scala
            Input.VirtualFile vf = new Input.VirtualFile(document.getFileId().getOriginalPath(), fullCode);
            ScalametaTokenizer tokenizer = new ScalametaTokenizer(vf, dialect);

            // tokenize with a filter
            scala.meta.tokens.Tokens tokens = tokenizer.tokenize();
            // use extensions to the standard PMD TokenManager and Filter
            ScalaTokenManager scalaTokenManager = new ScalaTokenManager(tokens.iterator(), document);
            ScalaTokenFilter filter = new ScalaTokenFilter(scalaTokenManager);

            ScalaTokenAdapter token;
            while ((token = filter.getNextToken()) != null) {
                if (StringUtils.isEmpty(token.getImage())) {
                    continue;
                }
                tokenEntries.recordToken(token.getImage(),
                                         token.getReportLocation());
            }
        } catch (Exception e) {
            if (e instanceof TokenizeException) { // NOPMD
                // cannot catch it as it's a checked exception and Scala sneaky throws
                TokenizeException tokE = (TokenizeException) e;
                Position pos = tokE.pos();
                throw tokenEntries.makeLexException(
                    pos.startLine() + 1, pos.startColumn() + 1, "Scalameta threw", tokE);
            } else {
                throw e;
            }
        }

    }

    /**
     * Implementation of the generic Token Manager, also skips un-helpful tokens and comments to only register important
     * tokens
     * and patterns.
     *
     * Keeps track of comments, for special comment processing
     */
    private static class ScalaTokenManager implements TokenManager<ScalaTokenAdapter> {

        private final Iterator<Token> tokenIter;
        private final TextDocument textDocument;
        private static final Class<?>[] SKIPPABLE_TOKENS = {
            Token.Space.class, Token.Tab.class, Token.CR.class,
            Token.LF.class, Token.FF.class, Token.LFLF.class, Token.EOF.class, Token.Comment.class };

        private ScalaTokenAdapter previousComment = null;

        ScalaTokenManager(Iterator<Token> iterator, TextDocument textDocument) {
            this.tokenIter = iterator;
            this.textDocument = textDocument;
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
                    previousComment = new ScalaTokenAdapter(token, textDocument, previousComment);
                }
            } while (token != null && skipToken(token) && tokenIter.hasNext());

            return new ScalaTokenAdapter(token, textDocument, previousComment);
        }

        private boolean skipToken(Token token) {
            boolean skip = false;
            if (token.text() != null) {
                for (Class<?> skipTokenClazz : SKIPPABLE_TOKENS) {
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

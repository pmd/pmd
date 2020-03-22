/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.token.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;

public class BaseTokenFilterTest {

    static class StringToken implements GenericToken<StringToken> {

        private final String text;

        StringToken(final String text) {
            this.text = text;
        }

        @Override
        public StringToken getNext() {
            return null;
        }

        @Override
        public StringToken getPreviousComment() {
            return null;
        }

        @Override
        public boolean isEof() {
            return text == null;
        }

        @Override
        public String getImage() {
            return text;
        }

        @Override
        public int getBeginLine() {
            return 0;
        }

        @Override
        public int getEndLine() {
            return 0;
        }

        @Override
        public int getBeginColumn() {
            return 0;
        }

        @Override
        public int getEndColumn() {
            return 0;
        }
    }

    static class StringTokenManager implements TokenManager<StringToken> {

        Iterator<String> iterator = Collections.unmodifiableList(Arrays.asList("a", "b", "c")).iterator();

        @Override
        public StringToken getNextToken() {
            if (iterator.hasNext()) {
                return new StringToken(iterator.next());
            } else {
                return null;
            }
        }

    }

    static class DummyTokenFilter<T extends GenericToken<T>> extends BaseTokenFilter<T> {

        Iterable<T> remainingTokens;

        DummyTokenFilter(final TokenManager<T> tokenManager) {
            super(tokenManager);
        }

        @Override
        protected boolean shouldStopProcessing(final T currentToken) {
            return currentToken == null;
        }

        @Override
        protected void analyzeTokens(final T currentToken, final Iterable<T> remainingTokens) {
            this.remainingTokens = remainingTokens;
        }

        public Iterable<T> getRemainingTokens() {
            return remainingTokens;
        }
    }

    @Test
    public void testRemainingTokensFunctionality1() {
        final TokenManager<StringToken> tokenManager = new StringTokenManager();
        final DummyTokenFilter<StringToken> tokenFilter = new DummyTokenFilter<>(tokenManager);
        final StringToken firstToken = tokenFilter.getNextToken();
        assertEquals("a", firstToken.getImage());
        final Iterable<StringToken> iterable = tokenFilter.getRemainingTokens();
        final Iterator<StringToken> it1 = iterable.iterator();
        final Iterator<StringToken> it2 = iterable.iterator();
        assertTrue(it1.hasNext());
        assertTrue(it2.hasNext());
        final StringToken firstValFirstIt = it1.next();
        final StringToken firstValSecondIt = it2.next();
        assertTrue(it1.hasNext());
        assertTrue(it2.hasNext());
        final StringToken secondValFirstIt = it1.next();
        assertFalse(it1.hasNext());
        assertTrue(it2.hasNext());
        final StringToken secondValSecondIt = it2.next();
        assertFalse(it2.hasNext());
        assertEquals("b", firstValFirstIt.getImage());
        assertEquals("b", firstValSecondIt.getImage());
        assertEquals("c", secondValFirstIt.getImage());
        assertEquals("c", secondValSecondIt.getImage());
    }

    @Test
    public void testRemainingTokensFunctionality2() {
        final TokenManager<StringToken> tokenManager = new StringTokenManager();
        final DummyTokenFilter<StringToken> tokenFilter = new DummyTokenFilter<>(tokenManager);
        final StringToken firstToken = tokenFilter.getNextToken();
        assertEquals("a", firstToken.getImage());
        final Iterable<StringToken> iterable = tokenFilter.getRemainingTokens();
        final Iterator<StringToken> it1 = iterable.iterator();
        final Iterator<StringToken> it2 = iterable.iterator();
        assertTrue(it1.hasNext());
        assertTrue(it2.hasNext());
        final StringToken firstValFirstIt = it1.next();
        assertTrue(it1.hasNext());
        final StringToken secondValFirstIt = it1.next();
        assertFalse(it1.hasNext());
        assertTrue(it2.hasNext());
        final StringToken firstValSecondIt = it2.next();
        assertTrue(it2.hasNext());
        final StringToken secondValSecondIt = it2.next();
        assertFalse(it2.hasNext());
        assertEquals("b", firstValFirstIt.getImage());
        assertEquals("b", firstValSecondIt.getImage());
        assertEquals("c", secondValFirstIt.getImage());
        assertEquals("c", secondValSecondIt.getImage());
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemainingTokensFunctionality3() {
        final TokenManager<StringToken> tokenManager = new StringTokenManager();
        final DummyTokenFilter<StringToken> tokenFilter = new DummyTokenFilter<>(tokenManager);
        final StringToken firstToken = tokenFilter.getNextToken();
        assertEquals("a", firstToken.getImage());
        final Iterable<StringToken> iterable = tokenFilter.getRemainingTokens();
        final Iterator<StringToken> it1 = iterable.iterator();
        final Iterator<StringToken> it2 = iterable.iterator();
        it1.next();
        it1.next();
        it2.next();
        it2.next();
        it1.next();
    }

    @Test(expected = ConcurrentModificationException.class)
    public void testRemainingTokensFunctionality4() {
        final TokenManager<StringToken> tokenManager = new StringTokenManager();
        final DummyTokenFilter<StringToken> tokenFilter = new DummyTokenFilter<>(tokenManager);
        final StringToken firstToken = tokenFilter.getNextToken();
        assertEquals("a", firstToken.getImage());
        final Iterable<StringToken> iterable = tokenFilter.getRemainingTokens();
        final Iterator<StringToken> it1 = iterable.iterator();
        final StringToken secondToken = tokenFilter.getNextToken();
        assertEquals("b", secondToken.getImage());
        it1.next();
    }

}

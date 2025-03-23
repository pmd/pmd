/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.document.TextRegion;

class BaseTokenFilterTest {

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
        public TextRegion getRegion() {
            return TextRegion.fromBothOffsets(0, text.length());
        }

        @Override
        public boolean isEof() {
            return text == null;
        }

        @Override
        public String getImageCs() {
            return text;
        }

        @Override
        public FileLocation getReportLocation() {
            return FileLocation.range(FileId.UNKNOWN, TextRange2d.range2d(1, 1, 1, 1));
        }

        @Override
        public int compareTo(StringToken o) {
            return text.compareTo(o.text);
        }

        @Override
        public int getKind() {
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
    void testRemainingTokensFunctionality1() {
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
    void testRemainingTokensFunctionality2() {
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

    @Test
    void testRemainingTokensFunctionality3() {
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
        assertThrows(NoSuchElementException.class, () -> it1.next());
    }

    @Test
    void testRemainingTokensFunctionality4() {
        final TokenManager<StringToken> tokenManager = new StringTokenManager();
        final DummyTokenFilter<StringToken> tokenFilter = new DummyTokenFilter<>(tokenManager);
        final StringToken firstToken = tokenFilter.getNextToken();
        assertEquals("a", firstToken.getImage());
        final Iterable<StringToken> iterable = tokenFilter.getRemainingTokens();
        final Iterator<StringToken> it1 = iterable.iterator();
        final StringToken secondToken = tokenFilter.getNextToken();
        assertEquals("b", secondToken.getImage());
        assertThrows(ConcurrentModificationException.class, () -> it1.next());
    }

}

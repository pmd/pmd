/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.HashMap;
import java.util.Map;

/**
 * Pools strings that are equal.
 * Use case: you have a char buffer and want to create a string from a slice.
 * Most strings have duplicates and you want to share them without creating intermediary strings.
 */
public final class StringPool {

    private final Map<Chars, String> pool = new HashMap<>();

    public CharSequence pooledCharSeq(Chars seq) {
        return new PooledCharSeq(seq);
    }


    class PooledCharSeq implements CharSequence {

        private final Chars seq;
        private String toString;


        PooledCharSeq(Chars seq) {
            this.seq = seq;
        }

        @Override
        public int length() {
            return seq.length();
        }

        @Override
        public char charAt(int index) {
            return seq.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new PooledCharSeq(seq.subSequence(start, end));
        }

        @Override
        public String toString() {
            if (toString == null) {
                toString = pool.computeIfAbsent(seq, CharSequence::toString);
            }
            return toString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PooledCharSeq)) {
                return false;
            }
            PooledCharSeq that = (PooledCharSeq) o;
            return seq.equals(that.seq);
        }

        @Override
        public int hashCode() {
            return seq.hashCode();
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;


import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.WeakHashMap;

import net.sourceforge.pmd.util.document.Chars;

/**
 * Simple pooling mechanism. Two {@link Chars} are equal if their
 * charsequence is equal. Note that most {@link Chars} instances
 * backing tokens hold a strong reference to the file's entire string
 * representation, so a pool must not be reused between several files.
 */
public final class StringPool {

    // This will be constant-folded by the JIT, use false in production
    private static final boolean COLLECT_STATS = false;
    private static final Stats STATS = new Stats();

    private static final String[] SINGLE_CHARS;

    static {
        // all ascii characters
        SINGLE_CHARS = new String[128];
        for (char i = 0; i < 128; i++) {
            SINGLE_CHARS[i] = String.valueOf(i);
        }
    }


    private final Map<Chars, String> pool = new WeakHashMap<>();

    String toString(CharSequence c, boolean doPool) {
        if (c.length() == 1) {
            char fst = c.charAt(0);
            if (fst < 128) {
                if (COLLECT_STATS) {
                    STATS.addCacheHit(1);
                }
                return SINGLE_CHARS[fst];
            }
        }

        if (doPool && c instanceof Chars) {
            if (COLLECT_STATS) {
                return pool.compute(
                    (Chars) c,
                    (chars, s) -> {
                        if (s != null) {
                            STATS.addCacheHit(s.length());
                            return s;
                        } else {
                            STATS.addCacheMiss(chars.length());
                            return chars.toString();
                        }
                    });
            } else {
                return pool.computeIfAbsent((Chars) c, Chars::toString);
            }
        }
        if (COLLECT_STATS) {
            STATS.addPass(c);
        }
        return c.toString();
    }

    public static void printStats() {
        if (COLLECT_STATS) {
            STATS.print();
        }
    }

    private static class Stats {

        long hits;
        long miss;
        final LongSummaryStatistics poolContents = new LongSummaryStatistics();
        long hitLen;
        long totalTotal;

        long notPooledAlloc;

        public void print() {
            System.err.println("String pool stats");
            System.err.println("=================");
            System.err.println("Hits: " + hits + " (" + (hits * 100 / (total() + 1)) + "% of " + total() + ")");
            System.err.println("Hit length (net savings): " + hitLen + " (" + toSize(hitLen) + ")");
            System.err.println("Total pool size: " + poolContents.getCount() + " strings (" + toSize(poolContents.getSum()) + ")");
            System.err.println("Avg pooled string length: " + poolContents.getAverage() + " chars");
            System.err.println("Unpooled string length: " + notPooledAlloc + " chars (" + toSize(notPooledAlloc) + ")");
        }

        private String toSize(long charLen) {
            // Assuming all pooled chars are latin-1, so 1B in a compressed string (byte[], not char[])
            // Before Java 9, it's twice as much
            if (charLen > (1 << 20)) {
                return (charLen >> 20) + " MB";
            } else if (charLen > (1 << 10)) {
                return (charLen >> 10) + " kB";
            }
            return charLen + " B";
        }

        void addCacheMiss(int len) {
            miss++;
            poolContents.accept(len);
        }

        void addCacheHit(int len) {
            hits++;
            hitLen += len;
        }

        long total() {
            return hits + miss;
        }

        void addPass(CharSequence c) {
            totalTotal++;
            if (!(c instanceof String)) {
                notPooledAlloc += c.length();
            }
        }
    }
}

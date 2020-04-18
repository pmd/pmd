/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static java.lang.Integer.max;

import java.io.EOFException;

import net.sourceforge.pmd.util.document.Chars;

/**
 * Records where escapes occurred in the input document. This is optimal
 * for the case where there are few/no escapes.
 *
 * <p>This implementation can handle arbitrary length differences between
 * the escape and its translation, provided the translation is always smaller
 * than the escape.
 * - C++ translates newline escapes (1 or 2 chars) to zero chars (an important corner case)
 * - Java translates arbitrary-length unicode escapes (>= 6 chars) to 1 char
 *
 * <p>This class is tightly coupled to what {@link EscapeAwareReader}
 * does with its buffer.
 */
class EscapeTracker {

    private static final int[] EMPTY = new int[0];
    private static final int RECORD_SIZE = 3;
    static final EOFException EOF = new EOFException();

    /*
     * Escapes are encoded as tuples (off, after, invalid) where
     * - off is the offset in the source file where the escape occurred
     * - after is the index of the char following the escape in the input file
     * - invalid is the last offset in the buffer which contains the translated chars (exclusive)
     *
     * Eg for "_\u00a0_" (translates as "_ _"), the buffer looks like
     * [_ u00a0_]
     *   ^       (off)               this char has been replaced with the translated value of the escape
     *    ^^^^^  (after - invalid)   these characters are only present in the input, we jump over them when reading
     *    ^      (invalid)           offset at which to jump to 'after'
     *         ^ (after)             first char after the escape
     *   ^^^^^^  (after - off)       total length of the escape in the input
     *
     * The escape record is (1,7,2)
     *
     * When reading the buffer we'll copy two blocks
     * * "a "
     * * then jump over "u00a0" and copy "b"
     *
     * In general to read until an escape means reading until its 'invalid'
     * field, and once that is reached, jump to off + len.
     *
     */
    private int[] escapeRecords = EMPTY;
    /** Index of the next write in the {@link #escapeRecords}. */
    private int nextFreeIdx = 0;


    /**
     * Calls to this method must occur in source order (ie param
     * offsetInInput increases monotonically).
     */
    void recordEscape(int offsetInInput, int lengthInInput, int lengthInOutput) {
        if (nextFreeIdx + 1 >= escapeRecords.length) {
            // add 1 to not stay stuck at zero, needs to remain a multiple of RECORD_SIZE
            int[] newOffsets = new int[(escapeRecords.length + 1) * RECORD_SIZE];
            System.arraycopy(escapeRecords, 0, newOffsets, 0, escapeRecords.length);
            this.escapeRecords = newOffsets;
        }

        escapeRecords[nextFreeIdx++] = offsetInInput;
        escapeRecords[nextFreeIdx++] = offsetInInput + lengthInInput;
        escapeRecords[nextFreeIdx++] = offsetInInput + lengthInOutput;
    }

    int inOff(int idx) {
        assert idx < nextFreeIdx;
        return escapeRecords[idx];
    }

    int indexAfter(int idx) {
        assert idx < nextFreeIdx;
        return escapeRecords[idx + 1];
    }


    int invalidIdx(int idx) {
        assert idx < nextFreeIdx;
        return escapeRecords[idx + 2];
    }

    int inLen(int idx) {
        assert idx < nextFreeIdx;
        return indexAfter(idx) - inOff(idx);
    }

    /**
     * Convert an offset in the translated file into an offset in
     * the untranslated input.
     */
    int inputOffsetAt(int translatedOffset) {
        // basically accumulate the lengths of all escapes occurring before the given translatedOffset
        int sum = translatedOffset;
        for (int i = 0; i < maxEscape(); i += RECORD_SIZE) {
            if (inOff(i) < sum) {
                sum += inLen(i);
            } else {
                break;
            }
        }
        return sum;
    }

    int maxEscape() {
        return nextFreeIdx;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Escape set {");
        for (int i = 0; i < maxEscape(); i += RECORD_SIZE) {
            res.append("(at=").append(inOff(i))
               .append(", inlen=").append(inLen(i))
               .append(", invalidAt=").append(invalidIdx(i))
               .append("), ");
        }

        return res.append('}').toString();
    }

    /** Backend for a CharStream. */
    class Cursor {

        /**
         * This is the index in buf of the next char to read, it always
         * holds that buf[pos] is a valid character.
         */
        private int pos;

        /**
         * Index in {@link #escapeRecords} of the next escape that occurred
         * in the source (this means, the buffer is discontinuous at
         * inputOffsets[nextEscape])
         */
        private int nextEscape = 0;

        /**
         * This is the current offset in the translated document, it
         * is shifted from pos by the total length of the escapes that
         * occurred before pos.
         */
        private int outOffset = 0;

        /**
         * A char buffer, which has discontinuities at the indexes
         * identified by the {@link #escapeRecords}. It must hold
         * that buf.length is the original source length.
         */
        private final Chars buf;

        private int mark = Integer.MAX_VALUE;
        private int markEscape;
        private int markOutOffset;

        Cursor(Chars buf) {
            this.buf = buf;
        }

        public char next() throws EOFException {
            if (pos == buf.length()) {
                throw EOF;
            }
            char c;

            if (nextEscape < maxEscape() && pos == invalidIdx(nextEscape)) {
                int pos = indexAfter(nextEscape); // jump past escape
                c = buf.charAt(pos);
                this.pos = pos + 1;
                this.nextEscape += RECORD_SIZE;
            } else {
                c = buf.charAt(pos);
                pos++;
            }
            outOffset++;
            return c;
        }


        public void backup(int numChars) {
            ensureMarked();
            if (numChars > markLength()) {
                throw new IllegalArgumentException(
                    "Cannot backup " + numChars + " chars, only " + markLength() + " are saved");
            }

            outOffset -= numChars;

            if (nextEscape <= 0) {
                pos -= numChars; // then there were no escapes before the 'pos'
            } else if (numChars == 1) {
                // fast path, very common
                int esc = nextEscape - RECORD_SIZE; // >= 0 because of condition above
                if (indexAfter(esc) == pos) {       // jump back over the escape
                    pos = invalidIdx(esc) - 1;
                    nextEscape = esc;
                } else {
                    pos--;
                }
            } else {
                int newOff = pos;
                for (int i = nextEscape - RECORD_SIZE; i >= 0 && numChars > 0; i -= RECORD_SIZE) {
                    // aa __|||bb
                    //      ^    invalid
                    //      ^^^  jumped
                    //    ^      inOff
                    //    ^^     translated

                    int nc = numChars;
                    numChars -= newOff - indexAfter(i);
                    newOff = max(indexAfter(i), newOff - nc);
                    if (numChars <= 0) { // skip "bb", ie everything after the escape
                        break;
                    }

                    newOff = invalidIdx(i) - 1; // jump back over the escape |||
                    numChars--;
                    nextEscape = i;

                    if (numChars <= 0) {
                        break;
                    }
                }
                if (numChars < 0) {
                    pos = newOff; // newOff was already clipped
                } else {
                    pos = newOff - numChars; // numChars is the remainder
                }
            }
        }

        public void mark() {
            this.mark = pos;
            this.markEscape = nextEscape;
            this.markOutOffset = outOffset;
        }

        public void appendMarkSuffix(StringBuilder sb, int suffixLen) {
            ensureMarked();
            assert suffixLen <= markLength();


            if (markEscape == nextEscape) {
                // no escape in the marked range
                buf.appendChars(sb, pos - suffixLen, suffixLen);
            } else {
                // fallback inefficient implementation
                getMarkImage().appendChars(sb, markLength() - suffixLen, suffixLen);
            }
        }

        public Chars getMarkImage() {
            if (markEscape == nextEscape) {
                // no escape in the marked range, this is the fast path
                return buf.slice(mark, markLength());
            }

            StringBuilder sb = new StringBuilder(markLength());

            int cur = mark;
            int esc = markEscape;
            while (cur < pos && esc < nextEscape) {
                buf.appendChars(sb, cur, invalidIdx(esc) - cur);
                cur = indexAfter(esc);
                esc += RECORD_SIZE;
            }
            // no more escape in the range, append everything until the pos
            buf.appendChars(sb, cur, pos - cur);
            assert sb.length() == markLength() : sb + " should have length " + markLength();
            return Chars.wrap(sb, true);
        }

        private void ensureMarked() {
            if (mark == Integer.MAX_VALUE) {
                throw new IllegalStateException("Mark is not set");
            }
            assert mark <= pos : "Wrong mark";
            assert markEscape <= nextEscape : "Wrong mark";
            assert markEscape <= escapeRecords.length : "Wrong escape mark";
        }

        public int curOutOffset() {
            return outOffset;
        }

        public int markOutOffset() {
            return markOutOffset;
        }

        public int markLength() {
            return outOffset - markOutOffset;
        }
    }
}

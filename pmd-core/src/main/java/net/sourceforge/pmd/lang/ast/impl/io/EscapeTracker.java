/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.io;

import java.io.EOFException;

import net.sourceforge.pmd.util.document.Chars;

/**
 * Records where escapes occurred in the input document. This is quite
 * an inefficient way to deal with it, yet in the common case where there
 * are few/no escapes, it's enough I think.
 */
class EscapeTracker {

    private static final int[] EMPTY = new int[0];
    private static final int RECORD_SIZE = 3;

    /*
     * Offsets in the input buffer where a unicode escape occurred.
     * Represented as tuples (off, len, invalid) where
     * - off is the offset in the source file where the escape occurred
     * - len is the length of the escape in the input file, eg for \ u 00a0 will be 6
     * - invalid is the last offset in the buffer which contains the translated chars (exclusive)
     *
     * Eg for "a\u00a0b" (translates as "a b"), the buffer looks like
     * [a u00a0b]
     *   ^       this char has been replaced with the translated value of the escape
     *    ^^^^^  these characters are only present in the input, we jump over them when reading
     *   ^       off
     *    ^      invalid
     *         ^ off + len
     *
     * The escape record is (1,6,2)
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
            // add 1 to not stay stuck at zero
            int[] newOffsets = new int[(escapeRecords.length + 1) * RECORD_SIZE];
            System.arraycopy(escapeRecords, 0, newOffsets, 0, escapeRecords.length);
            this.escapeRecords = newOffsets;
        }

        escapeRecords[nextFreeIdx++] = offsetInInput;
        escapeRecords[nextFreeIdx++] = lengthInInput;
        escapeRecords[nextFreeIdx++] = offsetInInput + lengthInOutput;
    }

    private int inOff(int idx) {
        return escapeRecords[idx];
    }

    private int inLen(int idx) {
        return escapeRecords[idx + 1];
    }

    private int invalidIdx(int idx) {
        return escapeRecords[idx + 2];
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

        char next() throws EOFException {
            if (pos == buf.length()) {
                throw new EOFException();
            }
            char c;

            if (nextEscape < maxEscape() && pos == invalidIdx(nextEscape)) {
                pos += inLen(nextEscape); // add escape length
                c = buf.charAt(pos);
                this.nextEscape += RECORD_SIZE;
            } else {
                c = buf.charAt(pos);
                pos++;
            }
            outOffset++;
            return c;
        }

        void backup(int numChars) {
            ensureMarked();
            if (numChars > markLength()) {
                throw new IllegalArgumentException(
                    "Cannot backup " + numChars + " chars, only " + markLength() + " are saved");
            }

            outOffset -= numChars;

            if (nextEscape <= 0) {
                pos -= numChars; // then there were no escapes before the 'pos'
            } else {
                int inoff = pos;
                for (int i = maxEscape() - RECORD_SIZE; i >= 0 && numChars > 0; i -= RECORD_SIZE) {
                    int esc = inOff(i);
                    if (esc == inoff) {
                        inoff -= inLen(i);
                    } else if (esc > inoff) {
                        // then the current escape was before what we're looking at
                        break;
                    } else {
                        inoff--;
                    }
                    numChars--;
                }
                pos = inoff - numChars;
            }
        }

        void mark() {
            this.mark = pos;
            this.markEscape = nextEscape;
            this.markOutOffset = outOffset;
        }

        void markToString(StringBuilder sb) {
            ensureMarked();

            int prevLength = sb.length();

            if (markEscape == nextEscape) {
                // no escape in the marked range
                sb.append(buf, mark, pos);
            } else {
                sb.ensureCapacity(markLength());

                int cur = mark;
                int esc = markEscape;
                while (cur < pos && esc < nextEscape) {
                    sb.append(buf, cur, invalidIdx(esc));
                    cur = inOff(esc) + inLen(esc);
                    esc += RECORD_SIZE;
                }
                // no more escape in the range, append everything until the pos
                sb.append(buf, cur, pos + 1);
                assert sb.length() - prevLength == markLength() : sb + " should have length " + markLength();
            }
        }

        private void ensureMarked() {
            if (mark == Integer.MAX_VALUE) {
                throw new IllegalStateException("Mark is not set");
            }
            assert mark <= pos : "Wrong mark";
            assert markEscape <= nextEscape : "Wrong mark";
            assert markEscape <= escapeRecords.length : "Wrong escape mark";
        }

        int curOutOffset() {
            return outOffset;
        }

        int markOutOffset() {
            return markOutOffset;
        }

        int markLength() {
            return outOffset - markOutOffset;
        }
    }
}

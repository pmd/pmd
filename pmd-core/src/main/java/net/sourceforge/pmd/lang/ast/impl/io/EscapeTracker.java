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

    /**
     * Offsets in the input buffer where a unicode escape occurred.
     * Represented as pairs [off, len] where
     * - off is the offset in the source file where the escape occurred
     * - len is the length in characters of the escape (which is translated to a single char).
     */
    private int[] escapeRecords = EMPTY;
    /** Index of the next write in the {@link #escapeRecords}. */
    private int nextFreeIdx = 0;

    /**
     * Calls to this method must occur in source order (ie param
     * offsetInInput increases monotonically).
     */
    void recordEscape(int offsetInInput, int len) {
        if (nextFreeIdx + 1 >= escapeRecords.length) {
            // double capacity, add 1 to not stay stuck at zero
            int[] newOffsets = new int[(escapeRecords.length + 1) * 2];
            System.arraycopy(escapeRecords, 0, newOffsets, 0, escapeRecords.length);
            this.escapeRecords = newOffsets;
        }

        escapeRecords[nextFreeIdx++] = offsetInInput;
        escapeRecords[nextFreeIdx++] = len - 1; // -1 because the translated escape has length 1
    }

    /**
     * Convert an offset in the translated file into an offset in
     * the untranslated input.
     */
    public int inputOffsetAt(int translatedOffset) {
        // basically accumulate the lengths of all escapes occurring before the given translatedOffset
        int sum = translatedOffset;
        for (int i = 0; i < nextFreeIdx; i += 2) {
            if (escapeRecords[i] < sum) {
                sum += escapeRecords[i + 1];
            } else {
                break;
            }
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("Escape set {");
        for (int i = 0; i < nextFreeIdx; i += 2) {
            res.append("(at=").append(escapeRecords[i]).append(", len=").append(escapeRecords[i + 1]).append("), ");
        }

        return res.append('}').toString();
    }

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

            char c = buf.charAt(pos);

            if (nextEscape < escapeRecords.length && pos == escapeRecords[nextEscape]) {
                pos += escapeRecords[nextEscape + 1]; // add escape length
                this.nextEscape += 2;
            } else {
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
                int off = pos;
                for (int i = nextEscape - 2; i >= 0 && numChars > 0; i -= 2) {
                    int esc = escapeRecords[i];
                    if (esc == off) {
                        off -= escapeRecords[i + 1];
                    } else if (esc > off) {
                        // then the current escape was before what we're looking at
                        break;
                    } else {
                        off--;
                    }
                    numChars--;
                }
                pos = off - numChars;
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
                    int escapeOff = escapeRecords[esc];
                    assert escapeOff < pos;
                    sb.append(buf, cur, escapeOff + 1);
                    cur = escapeOff + escapeRecords[esc + 1];
                    esc += 2;
                }
                // no more escape in the range, append everything until the pos
                sb.append(buf, cur, pos + 1);
                assert sb.length() - prevLength == markLength();
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

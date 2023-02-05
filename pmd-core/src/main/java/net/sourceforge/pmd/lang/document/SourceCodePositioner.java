/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.util.Arrays;

import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Wraps a piece of text, and converts absolute offsets to line/column
 * coordinates, and back. This is used by the {@link TextDocument} implementation.
 *
 * <p>This used to be public. We don't need it anymore, {@link TextDocument}
 * is a higher level abstraction.
 */
final class SourceCodePositioner {

    // Idea from:
    // http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/SourceFile.java

    /**
     * Each entry is the inclusive start offset of a line (zero based). Never empty.
     * The last entry has the offset of the EOF, to avoid overflows.
     */
    private final int[] lineOffsets;
    private final int sourceCodeLength;

    private SourceCodePositioner(int[] offsets, int len) {
        this.lineOffsets = offsets;
        this.sourceCodeLength = len;
    }

    // test only
    int[] getLineOffsets() {
        return lineOffsets;
    }

    TextPos2d lineColFromOffset(int offset, boolean inclusive) {
        AssertionUtil.requireInInclusiveRange("offset", offset, 0, sourceCodeLength);

        int line = searchLineOffset(offset);

        int lineIdx = line - 1; // zero-based

        if (lineIdx != 0 && offset == lineOffsets[lineIdx] && !inclusive) {
            // we're precisely on the start of a line
            // if inclusive, prefer the position at the end of the previous line
            // This is a subtlety that the other methods for offset -> line do not
            // handle. This is because an offset may be interpreted as the index
            // of a character, or the caret position between two characters. This
            // is relevant when building text regions, to respect inclusivity, etc.
            return TextPos2d.pos2d(lineIdx, getLastColumnOfLine(lineIdx));
        }

        return TextPos2d.pos2d(line, 1 + offset - lineOffsets[lineIdx]);
    }

    /**
     * Returns the line number of the character at the given offset.
     *
     * @param offset Offset in the document (zero-based)
     *
     * @return Line number (1-based), or -1
     *
     * @throws IndexOutOfBoundsException If the offset is invalid in this document
     */
    public int lineNumberFromOffset(final int offset) {
        AssertionUtil.requireIndexNonNegative("offset", offset);
        if (offset > sourceCodeLength) {
            return -1;
        }

        return searchLineOffset(offset);
    }

    private int searchLineOffset(int offset) {
        int search = Arrays.binarySearch(lineOffsets, 0, lineOffsets.length - 1, offset);
        return search >= 0 ? search + 1 : ~search;
    }

    /**
     * Returns the column number of the character at the given offset.
     * The offset is not relative to the line (the line number is just
     * a hint). If the column number does not exist (on the given line),
     * returns -1.
     *
     * @param lineNumber   Line number (1-based)
     * @param globalOffset Global offset in the document (zero-based)
     *
     * @return Column number (1-based), or -1
     *
     * @throws IndexOutOfBoundsException If the line number does not exist
     */
    public int columnFromOffset(final int lineNumber, final int globalOffset) {
        AssertionUtil.requireInPositiveRange("Line number", lineNumber, lineOffsets.length);

        int lineIndex = lineNumber - 1;

        if (globalOffset > lineOffsets[lineNumber]) {
            // throw new IllegalArgumentException("Column " + (col + 1) + " does not exist on line " + lineNumber);
            return -1;
        }

        return globalOffset - lineOffsets[lineIndex] + 1; // 1-based column offsets
    }

    /**
     * Finds the offset of a position given (line,column) coordinates.
     * Returns -1 if the parameters don't identify a caret position in
     * the wrapped text.
     *
     * @param line   Line number (1-based)
     * @param column Column number (1-based)
     *
     * @return Text offset (zero-based), or -1
     */
    public int offsetFromLineColumn(final int line, final int column) {
        if (!isValidLine(line)) {
            if (line == lineOffsets.length && column == 1) {
                return sourceCodeLength;
            }
            return -1;
        }

        final int lineIdx = line - 1;
        int bound = offsetOfEndOfLine(line);
        int off = lineOffsets[lineIdx] + column - 1;
        return off > bound ? -1 : off;
    }

    /**
     * Returns the offset of the end of the given line. This is the caret
     * position that follows the last character on the line (which includes
     * the line terminator if any). This is the caret position at the
     * start of the next line, except if the line is the last in the document.
     *
     * @param line Line number (1-based)
     *
     * @return Text offset
     *
     * @throws IndexOutOfBoundsException If the line is invalid
     */
    public int offsetOfEndOfLine(final int line) {
        if (!isValidLine(line)) {
            throw new IndexOutOfBoundsException(
                line + " is not a valid line number, expected at most " + lineOffsets.length);
        }

        return lineOffsets[line];
    }

    boolean isValidLine(int line) {
        return line >= 1 && line <= getLastLine();
    }

    /**
     * Returns the number of lines, which is also the ordinal of the
     * last line.
     */
    public int getLastLine() {
        return lineOffsets.length - 1;
    }

    public int getNumLines() {
        return getLastLine();
    }

    /**
     * Returns the last column number of the last line in the document.
     */
    public int getLastLineColumn() {
        return getLastColumnOfLine(getLastLine());
    }

    private int getLastColumnOfLine(int line) {
        if (line == 0) {
            return 1 + lineOffsets[line];
        } else {
            return 1 + lineOffsets[line] - lineOffsets[line - 1];
        }
    }

    /**
     * Builds a new positioner for the given char sequence.
     * The char sequence should have its newline delimiters normalized
     * to {@link TextFileContent#NORMALIZED_LINE_TERM}.
     * The char sequence should not change state (eg a {@link StringBuilder})
     * after construction, otherwise this positioner becomes unreliable.
     *
     * @param charSeq Text to wrap
     */
    public static SourceCodePositioner create(CharSequence charSeq) {
        final int len = charSeq.length();
        Builder builder = new Builder();

        int off = 0;
        while (off < len) {
            char c = charSeq.charAt(off);
            if (c == '\n') {
                builder.addLineEndAtOffset(off + 1);
            }
            off++;
        }

        return builder.build(len);
    }

    static final class Builder {

        private int[] buf;
        private int count = 1; // note the first element of the buffer is always 0 (the offset of the first line)
        private int lastLineOffset = 0;

        Builder(int bufSize) {
            buf = new int[Math.max(1, bufSize)];
        }

        Builder() {
            this(400);
        }

        /**
         * Record a line ending. The parameter must be monotonically increasing.
         *
         * @param offset The index of the character right after the line
         *               terminator in the source text. Eg for {@code \r\n}
         *               or {@code \n}, it's the index of the {@code \n}, plus 1.
         */
        public void addLineEndAtOffset(int offset) {
            addLineImpl(offset, false);
        }

        private void addLineImpl(int offset, boolean isEof) {
            if (offset < 0 || offset < lastLineOffset || offset == lastLineOffset && !isEof) {
                throw new IllegalArgumentException(
                    "Invalid offset " + offset + " (last offset " + lastLineOffset + ")"
                );
            }
            lastLineOffset = offset;
            if (count >= buf.length) {
                buf = Arrays.copyOf(buf, buf.length * 2 + 1);
            }
            buf[count] = offset;
            count++;
        }

        public SourceCodePositioner build(int eofOffset) {
            addLineImpl(eofOffset, true);
            int[] finalOffsets = Arrays.copyOf(buf, count);
            return new SourceCodePositioner(finalOffsets, eofOffset);
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.internal.util.AssertionUtil;

/**
 * Wraps a piece of text, and converts absolute offsets to line/column
 * coordinates, and back. This is used by some language implementations
 * (JS, XML, Apex) and by the {@link TextDocument} implementation.
 */
public final class SourceCodePositioner {

    // Idea from:
    // http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/SourceFile.java

    /** Each entry is the inclusive start offset of a line (zero based). Never empty. */
    private final int[] lineOffsets;
    private final int sourceCodeLength;

    /**
     * Builds a new positioner for the given char sequence.
     * The char sequence should not change state (eg a {@link StringBuilder})
     * after construction, otherwise this positioner becomes unreliable.
     *
     * @param sourceCode Text to wrap
     */
    public SourceCodePositioner(CharSequence sourceCode) {
        int len = sourceCode.length();
        this.lineOffsets = makeLineOffsets(sourceCode, len);
        this.sourceCodeLength = len;
    }

    // test only
    int[] getLineOffsets() {
        return lineOffsets;
    }

    /**
     * Returns the line number of the character at the given offset.
     * Returns -1 if the offset is not valid in this document.
     *
     * @param offset Offset in the document (zero-based)
     *
     * @return Line number (1-based), or -1
     *
     * @throws IllegalArgumentException If the offset is negative
     */
    public int lineNumberFromOffset(final int offset) {
        AssertionUtil.requireNonNegative("offset", offset);

        if (offset > sourceCodeLength) {
            return -1;
        }

        int search = Arrays.binarySearch(lineOffsets, offset);
        return search >= 0 ? search + 1 : ~search;
    }

    /**
     * Returns the column number at the given offset. The offset is not
     * relative to the line (the line number is just a hint). If the
     * column number does not exist (on the given line), returns -1.
     *
     * @param lineNumber   Line number (1-based)
     * @param globalOffset Global offset in the document (zero-based)
     *
     * @return Column number (1-based), or -1
     *
     * @throws IllegalArgumentException If the line number does not exist
     */
    public int columnFromOffset(final int lineNumber, final int globalOffset) {
        int lineIndex = lineNumber - 1;
        if (lineIndex < 0 || lineIndex >= lineOffsets.length) {
            throw new IllegalArgumentException("Line " + lineNumber + " does not exist");
        }

        int bound = lineIndex + 1 < lineOffsets.length ? lineOffsets[lineIndex + 1]
                                                       : sourceCodeLength;

        if (globalOffset > bound) {
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
        final int lineIdx = line - 1;

        if (lineIdx < 0 || lineIdx >= lineOffsets.length) {
            return -1;
        }

        int bound = line == lineOffsets.length  // last line?
                    ? sourceCodeLength
                    : lineOffsets[line];

        int off = lineOffsets[lineIdx] + column - 1;
        return off > bound ? -1 : off;
    }

    /**
     * Returns the number of lines, which is also the ordinal of the
     * last line.
     */
    public int getLastLine() {
        return lineOffsets.length;
    }

    /**
     * Returns the last column number of the last line in the document.
     */
    public int getLastLineColumn() {
        return columnFromOffset(getLastLine(), sourceCodeLength - 1);
    }

    private static int[] makeLineOffsets(CharSequence sourceCode, int len) {
        List<Integer> buffer = new ArrayList<>();
        buffer.add(0); // first line

        int off = 0;
        while (off < len) {
            char c = sourceCode.charAt(off);
            off++;
            if (c == '\n') {
                buffer.add(off);
            }
        }

        int[] lineOffsets = new int[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            lineOffsets[i] = buffer.get(i);
        }
        return lineOffsets;
    }
}

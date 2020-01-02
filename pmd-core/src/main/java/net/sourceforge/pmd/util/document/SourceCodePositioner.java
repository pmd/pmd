/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.input.CharSequenceReader;

import net.sourceforge.pmd.internal.util.AssertionUtil;

/**
 * Wraps a piece of text, and converts absolute offsets to line/column coordinates, and back.
 * This is used by some language implementations (JS, XML, Apex) and by
 * the {@link TextDocument} implementation.
 */
public final class SourceCodePositioner {

    // Idea from:
    // http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/SourceFile.java

    /**
     * This list has one entry for each line, denoting the start offset of the line.
     * The start offset of the next line includes the length of the line terminator
     * (1 for \r|\n, 2 for \r\n).
     */
    private final List<Integer> lineOffsets = new ArrayList<>();
    private final int sourceCodeLength;
    private final CharSequence sourceCode;

    /**
     * Builds a new source code positioner for the given char sequence.
     * The char sequence should not change state (eg a {@link StringBuilder})
     * after construction, otherwise this positioner becomes unreliable.
     *
     * @param sourceCode Text to wrap
     */
    public SourceCodePositioner(CharSequence sourceCode) {
        sourceCodeLength = sourceCode.length();
        this.sourceCode = sourceCode;

        try (Scanner scanner = new Scanner(new CharSequenceReader(sourceCode))) {
            int currentGlobalOffset = 0;

            while (scanner.hasNextLine()) {
                lineOffsets.add(currentGlobalOffset);
                currentGlobalOffset += getLineLengthWithLineSeparator(scanner);
            }
        }

        // empty text, consider it a single empty line
        if (lineOffsets.isEmpty()) {
            lineOffsets.add(0);
        }
    }

    /**
     * Returns the source passed as parameter.
     */
    public CharSequence getText() {
        return sourceCode;
    }

    // test only
    List<Integer> getLineOffsets() {
        return lineOffsets;
    }

    /**
     * Sums the line length without the line separation and the characters which matched the line separation pattern
     *
     * @param scanner the scanner from which to read the line's length
     *
     * @return the length of the line with the line separator.
     */
    private int getLineLengthWithLineSeparator(final Scanner scanner) {
        int lineLength = scanner.nextLine().length();
        final String lineSeparationMatch = scanner.match().group(1);

        if (lineSeparationMatch != null) {
            lineLength += lineSeparationMatch.length();
        }

        return lineLength;
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

        int search = Collections.binarySearch(lineOffsets, offset);
        return search >= 0 ? search + 1 // 1-based line numbers
                           : -(search + 1); // see spec of binarySearch
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
        if (lineIndex < 0 || lineIndex >= lineOffsets.size()) {
            throw new IllegalArgumentException("Line " + lineNumber + " does not exist");
        }

        int bound = lineIndex + 1 < lineOffsets.size() ? lineOffsets.get(lineIndex + 1)
                                                       : sourceCodeLength;

        if (globalOffset > bound) {
            // throw new IllegalArgumentException("Column " + (col + 1) + " does not exist on line " + lineNumber);
            return -1;
        }

        return globalOffset - lineOffsets.get(lineIndex) + 1; // 1-based column offsets
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

        if (lineIdx < 0 || lineIdx >= lineOffsets.size()) {
            return -1;
        }

        int bound = line == lineOffsets.size()  // last line?
                    ? sourceCodeLength
                    : lineOffsets.get(line);

        int off = lineOffsets.get(lineIdx) + column - 1;
        return off > bound ? -1 : off;
    }

    /**
     * Returns the number of lines, which is also the ordinal of the
     * last line.
     */
    public int getLastLine() {
        return lineOffsets.size();
    }

    /**
     * Returns the last column number of the last line in the document.
     */
    public int getLastLineColumn() {
        return columnFromOffset(getLastLine(), sourceCodeLength - 1);
    }
}

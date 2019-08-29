/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Calculates from an absolute offset in the source file the line/column
 * coordinate. This is needed as Rhino only offers absolute positions for each
 * node. Some other languages like XML and Apex use this, too.
 *
 * Idea from:
 * http://code.google.com/p/closure-compiler/source/browse/trunk/src/com/google/javascript/jscomp/SourceFile.java
 */
public class SourceCodePositioner {

    /**
     * This list has one entry for each line, denoting the start offset of the line.
     * The start offset of the next line includes the length of the line terminator
     * (1 for \r|\n, 2 for \r\n).
     */
    private final List<Integer> lineOffsets = new ArrayList<>();
    private final int sourceCodeLength;
    private final String sourceCode;

    public SourceCodePositioner(String sourceCode) {
        sourceCodeLength = sourceCode.length();
        this.sourceCode = sourceCode;

        try (Scanner scanner = new Scanner(sourceCode)) {
            int currentGlobalOffset = 0;

            while (scanner.hasNextLine()) {
                lineOffsets.add(currentGlobalOffset);
                currentGlobalOffset += getLineLengthWithLineSeparator(scanner);
            }
        }
    }

    /** Returns the full source. */
    public CharSequence getSourceCode() {
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

    public int lineNumberFromOffset(int offset) {
        int search = Collections.binarySearch(lineOffsets, offset);
        return search >= 0 ? search + 1 // 1-based line numbers
                           : -(search + 1); // see spec of binarySearch
    }

    public int columnFromOffset(int lineNumber, int offset) {
        int lineIndex = lineNumber - 1;
        if (lineIndex < 0 || lineIndex >= lineOffsets.size()) {
            // no line number found...
            return 0;
        }
        int columnOffset = offset - lineOffsets.get(lineNumber - 1);
        return columnOffset + 1; // 1-based column offsets
    }

    public int offsetFromLineColumn(int line, int column) {
        line--;

        if (line < 0 || line >= lineOffsets.size()) {
            return -1;
        }

        int bound = line == lineOffsets.size() - 1  // last line?
                    ? sourceCodeLength
                    : lineOffsets.get(line + 1);

        int off = lineOffsets.get(line) + column - 1;
        return off > bound ? -1 // out of bounds!
                           : off;
    }

    /** Returns the number of lines, which is also the ordinal of the last line. */
    public int getLastLine() {
        return lineOffsets.size();
    }

    public int getLastLineColumn() {
        return columnFromOffset(getLastLine(), sourceCodeLength - 1);
    }
}

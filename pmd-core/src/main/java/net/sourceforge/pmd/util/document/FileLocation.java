/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.Comparator;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * A kind of {@link TextRegion} used for reporting. This provides access
 * to the line and column positions, as well as the text file. Instances
 * can be obtained from a {@link TextRegion} with {@link TextDocument#toLocation(TextRegion) TextDocument::toLocation}.
 *
 * <p>This admittedly should replace the text coordinates methods in {@link Node},
 * {@link GenericToken}, and {@link RuleViolation} at least.
 *
 * TODO the end line/end column are barely used, mostly ignored even by
 *  renderers. Maybe these could be optional, or replaced by just a length
 *  in case a renderer wants to cut out a piece of the file.
 */
public final class FileLocation {

    public static final Comparator<FileLocation> COORDS_COMPARATOR =
        Comparator.comparingInt(FileLocation::getBeginLine)
                  .thenComparingInt(FileLocation::getBeginColumn)
                  .thenComparingInt(FileLocation::getEndLine)
                  .thenComparingInt(FileLocation::getEndColumn);


    public static final Comparator<FileLocation> COMPARATOR =
        Comparator.comparing(FileLocation::getFileName).thenComparing(COORDS_COMPARATOR);

    private final int beginLine;
    private final int endLine;
    private final int beginColumn;
    private final int endColumn;
    private final String fileName;
    private final @Nullable TextRegion region;

    FileLocation(String fileName, int beginLine, int beginColumn, int endLine, int endColumn) {
        this(fileName, beginLine, beginColumn, endLine, endColumn, null);
    }

    FileLocation(String fileName, int beginLine, int beginColumn, int endLine, int endColumn, @Nullable TextRegion region) {
        this.fileName = Objects.requireNonNull(fileName);
        this.beginLine = AssertionUtil.requireOver1("Begin line", beginLine);
        this.endLine = AssertionUtil.requireOver1("End line", endLine);
        this.beginColumn = AssertionUtil.requireOver1("Begin column", beginColumn);
        this.endColumn = AssertionUtil.requireOver1("End column", endColumn);
        this.region = region;

        requireLinesCorrectlyOrdered();
    }

    private void requireLinesCorrectlyOrdered() {
        if (beginLine > endLine) {
            throw AssertionUtil.mustBe("endLine", endLine, ">= beginLine (= " + beginLine + ")");
        } else if (beginLine == endLine && beginColumn > endColumn) {
            throw AssertionUtil.mustBe("endColumn", endColumn, ">= beginColumn (= " + beginColumn + ")");
        }
    }

    /**
     * File name of this position.
     */
    public String getFileName() {
        return fileName;
    }

    /** Inclusive, 1-based line number. */
    public int getBeginLine() {
        return beginLine;
    }

    /** Inclusive, 1-based line number. */
    public int getEndLine() {
        return endLine;
    }

    /** Inclusive, 1-based column number. */
    public int getBeginColumn() {
        return beginColumn;
    }

    /** <b>Exclusive</b>, 1-based column number. */
    public int getEndColumn() {
        return endColumn;
    }

    /** Returns the region in the file, or null if this was not available. */
    public @Nullable TextRegion getRegionInFile() {
        return region;
    }

    /**
     * Formats the start position as e.g. {@code "line 1, column 2"}.
     */
    public String startPosToString() {
        return "line " + getBeginLine() + ", column " + getBeginColumn();
    }


    /**
     * Formats the start position as e.g. {@code "/path/to/file:1:2"}.
     */
    public String startPosToStringWithFile() {
        return getFileName() + ":" + getBeginLine() + ":" + getBeginColumn();
    }

    /**
     * Creates a new location for a range of text.
     *
     * @throws IllegalArgumentException If the file name is null
     * @throws IllegalArgumentException If any of the line/col parameters are strictly less than 1
     * @throws IllegalArgumentException If the line and column are not correctly ordered
     * @throws IllegalArgumentException If the start offset or length are negative
     */
    public static FileLocation range(String fileName, int beginLine, int beginColumn, int endLine, int endColumn) {
        return new FileLocation(fileName, beginLine, beginColumn, endLine, endColumn);
    }

    /**
     * Returns a new location that starts and ends at the same position.
     *
     * @param fileName File name
     * @param line     Line number
     * @param column   Column number
     *
     * @return A new location
     *
     * @throws IllegalArgumentException See {@link #range(String, int, int, int, int)}
     */
    public static FileLocation caret(String fileName, int line, int column) {
        return new FileLocation(fileName, line, column, line, column);
    }


    @Override
    public String toString() {
        return "!debug only! " + startPosToStringWithFile();
    }
}

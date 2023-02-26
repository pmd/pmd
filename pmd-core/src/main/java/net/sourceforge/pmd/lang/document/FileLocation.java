/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.util.Comparator;
import java.util.Objects;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.reporting.Reportable;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Represents the coordinates of a text region, used for reporting. This provides access
 * to the line and column positions, as well as the text file. Instances
 * can be obtained from a {@link TextRegion} with {@link TextDocument#toLocation(TextRegion) TextDocument::toLocation}.
 *
 * <p>This should replace the text coordinates methods in {@link Node},
 * {@link GenericToken}, and {@link RuleViolation} at least (see {@link Reportable}).
 *
 * TODO the end line/end column are barely used, mostly ignored even by
 *  renderers. Maybe these could be optional, or replaced by just a length
 *  in case a renderer wants to cut out a piece of the file.
 */
public final class FileLocation {

    public static final Comparator<FileLocation> COORDS_COMPARATOR =
        Comparator.comparing(FileLocation::getStartPos)
                  .thenComparing(FileLocation::getEndPos);


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
     * File name of this position. This is a display name, it shouldn't
     * be parsed as a Path.
     */
    public String getFileName() {
        return fileName;
    }

    /** Inclusive, 1-based line number. */
    public int getStartLine() {
        return beginLine;
    }

    /** Inclusive, 1-based line number. */
    public int getEndLine() {
        return endLine;
    }

    /** Inclusive, 1-based column number. */
    public int getStartColumn() {
        return beginColumn;
    }

    /** <b>Exclusive</b>, 1-based column number. */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Returns the start position.
     */
    public TextPos2d getStartPos() {
        return TextPos2d.pos2d(beginLine, beginColumn);
    }


    /**
     * Returns the end position.
     */
    public TextPos2d getEndPos() {
        return TextPos2d.pos2d(endLine, endColumn);
    }

    /**
     * Turn this into a range country.
     */
    public TextRange2d toRange2d() {
        return TextRange2d.range2d(beginLine, beginColumn, endLine, endColumn);
    }

    /** Returns the region in the file, or null if this was not available. */
    public @Nullable TextRegion getRegionInFile() {
        return region;
    }

    /**
     * Formats the start position as e.g. {@code "line 1, column 2"}.
     */
    public String startPosToString() {
        return getStartPos().toDisplayStringInEnglish();
    }


    /**
     * Formats the start position as e.g. {@code "/path/to/file:1:2"}.
     */
    public String startPosToStringWithFile() {
        return getFileName() + ":" + getStartPos().toDisplayStringWithColon();
    }

    /**
     * Creates a new location for a range of text.
     *
     * @throws IllegalArgumentException If the file name is null
     * @throws IllegalArgumentException If any of the line/col parameters are strictly less than 1
     * @throws IllegalArgumentException If the line and column are not correctly ordered
     * @throws IllegalArgumentException If the start offset or length are negative
     */
    public static FileLocation range(String fileName, TextRange2d range2d) {
        TextPos2d start = range2d.getStartPos();
        TextPos2d end = range2d.getEndPos();
        return new FileLocation(fileName,
                                start.getLine(),
                                start.getColumn(),
                                end.getLine(),
                                end.getColumn());
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

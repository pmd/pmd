/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.lang.document.RootTextDocument.checkInRange;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Represents a textual document, providing methods to edit it incrementally
 * and address regions of text. A text document delegates IO operations
 * to a {@link TextFile}. It reflects some in-memory snapshot of the file,
 * though the file may still be edited externally.
 *
 * <p>TextDocument is meant to replace CPD's {@link SourceCode} and PMD's
 * {@link DataSource}, though the abstraction level of {@link DataSource}
 * is the {@link TextFile}.
 *
 * <p>Note that the backing {@link TextFile} is purposefully not accessible
 * from a text document. Exposing it here could lead to files being written
 * to from within rules, while we want to eventually build an API that allows
 * file edition based on AST manipulation.
 */
public interface TextDocument extends Closeable {
    // todo logical sub-documents, to support embedded languages
    //  ideally, just slice the text, and share the positioner

    // todo text edition (there are some reverted commits in the branch
    //  with part of this, including a lot of tests)

    /*
        Summary of different coordinate systems:
        Coordinate system:   Line/column            Offset
        ==============================================================
        Position:            TextPos2d              int >= 0
        Range:               TextRange2d            TextRegion

        (FileLocation is similar to TextRange2d in terms of position info)

        Conversions:
          line/column -> offset: offsetAtLineColumn
          offset -> line/column: lineColumnAtOffset
        Range conversions:
          TextRegion  -> TextRange2d: toRegion
          TextRange2d -> TextRegion: toRange2d

          TextRegion -> FileLocation: toLocation
          TextRange2d -> FileLocation: toLocation
     */

    /**
     * Returns the language version that should be used to parse this file.
     */
    LanguageVersion getLanguageVersion();

    /**
     * Returns {@link TextFile#getPathId()} for the text file backing this document.
     */
    String getPathId();

    /**
     * Returns {@link TextFile#getDisplayName()} for the text file backing this document.
     */
    String getDisplayName();


    /**
     * Returns the current text of this document. Note that this doesn't take
     * external modifications to the {@link TextFile} into account.
     *
     * <p>Line endings are normalized to {@link TextFileContent#NORMALIZED_LINE_TERM}.
     *
     * @see TextFileContent#getNormalizedText()
     */
    default Chars getText() {
        return getContent().getNormalizedText();
    }

    /**
     * Returns a region of the {@linkplain #getText() text} as a character sequence.
     */
    Chars sliceText(TextRegion region);


    /**
     * Returns the current contents of the text file. See also {@link #getText()}.
     */
    TextFileContent getContent();

    /**
     * Returns a reader over the text of this document.
     */
    default Reader newReader() {
        return getText().newReader();
    }


    /**
     * Returns the length in characters of the {@linkplain #getText() text}.
     */
    default int getLength() {
        return getText().length();
    }

    /**
     * Returns a text region that corresponds to the entire document.
     */
    default TextRegion getEntireRegion() {
        return TextRegion.fromOffsetLength(0, getLength());
    }

    /**
     * Returns a region that spans the text of all the given lines.
     * This is intended to provide a replacement for {@link SourceCode#getSlice(int, int)}.
     *
     * @param startLineInclusive Inclusive start line number (1-based)
     * @param endLineInclusive   Inclusive end line number (1-based)
     *
     * @throws IndexOutOfBoundsException If the arguments do not identify
     *                                   a valid region in this document
     */
    TextRegion createLineRange(int startLineInclusive, int endLineInclusive);


    /**
     * Turn a text region into a {@link FileLocation}. This computes
     * the line/column information for both start and end offset of
     * the region.
     *
     * @return A new file position
     *
     * @throws IndexOutOfBoundsException If the argument is not a valid region in this document
     */
    FileLocation toLocation(TextRegion region);

    /**
     * Turn a text region into a {@link FileLocation}. The file name is
     * the display name of this document.
     *
     * @return A new file position
     *
     * @throws IndexOutOfBoundsException If the argument is not a valid region in this document
     */
    default FileLocation toLocation(TextRange2d range) {
        int startOffset = offsetAtLineColumn(range.getEndPos());
        if (startOffset < 0) {
            throw new IndexOutOfBoundsException("Region out of bounds: " + range.displayString());
        }
        TextRegion region = TextRegion.caretAt(startOffset);
        checkInRange(region, this.getLength());
        return FileLocation.location(getDisplayName(), range);
    }

    /**
     * Turn a text region to a {@link TextRange2d}.
     */
    default TextRange2d toRange2d(TextRegion region) {
        TextPos2d start = lineColumnAtOffset(region.getStartOffset(), true);
        TextPos2d end = lineColumnAtOffset(region.getEndOffset(), false);
        return TextRange2d.range2d(start, end);
    }

    /**
     * Turn a {@link TextRange2d} into a {@link TextRegion}.
     */
    default TextRegion toRegion(TextRange2d region) {
        return TextRegion.fromBothOffsets(
            offsetAtLineColumn(region.getStartPos()),
            offsetAtLineColumn(region.getEndPos())
        );
    }


    /**
     * Returns the offset at the given line and column number.
     *
     * @param line   Line number (1-based)
     * @param column Column number (1-based)
     *
     * @return an offset (0-based)
     */
    int offsetAtLineColumn(int line, int column);

    /**
     * Returns true if the position is valid in this document.
     */
    boolean isInRange(TextPos2d textPos2d);

    /**
     * Returns the offset at the line and number.
     */
    default int offsetAtLineColumn(TextPos2d pos2d) {
        return offsetAtLineColumn(pos2d.getLine(), pos2d.getColumn());
    }

    /**
     * Returns the line and column at the given offset (inclusive).
     *
     * @param offset A source offset (0-based), can range in {@code [0, length]}.
     *
     * @throws IndexOutOfBoundsException if the offset is out of bounds
     */
    default TextPos2d lineColumnAtOffset(int offset) {
        return lineColumnAtOffset(offset, true);
    }

    /**
     * Returns the line and column at the given offset (inclusive).
     *
     * @param offset    A source offset (0-based), can range in {@code [0, length]}.
     * @param inclusive If the offset falls right after a line terminator,
     *                  two behaviours are possible. If the parameter is true,
     *                  choose the position at the start of the next line.
     *                  Otherwise choose the offset at the end of the line.
     *
     * @throws IndexOutOfBoundsException if the offset is out of bounds
     */
    TextPos2d lineColumnAtOffset(int offset, boolean inclusive);



    /**
     * Closing a document closes the underlying {@link TextFile}.
     * New editors cannot be produced after that, and the document otherwise
     * remains in its current state.
     *
     * @throws IOException           If {@link TextFile#close()} throws
     * @throws IllegalStateException If an editor is currently open. In this case
     *                               the editor is rendered ineffective before the
     *                               exception is thrown. This indicates a programming
     *                               mistake.
     */
    @Override
    void close() throws IOException;


    static TextDocument create(TextFile textFile) throws IOException {
        return new RootTextDocument(textFile);
    }

    /**
     * Returns a read-only document for the given text.
     *
     * @see TextFile#forCharSeq(CharSequence, String, LanguageVersion)
     */
    static TextDocument readOnlyString(final CharSequence source, LanguageVersion lv) {
        return readOnlyString(source, TextFile.UNKNOWN_FILENAME, lv);
    }

    /**
     * Returns a read-only document for the given text. This works as
     * if by calling {@link TextDocument#create(TextFile)} on a textfile
     * produced by {@link TextFile#forCharSeq(CharSequence, String, LanguageVersion) forString},
     * but doesn't throw {@link IOException}, as such text files will
     * not throw.
     *
     * @see TextFile#forCharSeq(CharSequence, String, LanguageVersion)
     */
    @SuppressWarnings("PMD.CloseResource")
    static TextDocument readOnlyString(@NonNull CharSequence source, @NonNull String filename, @NonNull LanguageVersion lv) {
        TextFile textFile = TextFile.forCharSeq(source, filename, lv);
        try {
            return create(textFile);
        } catch (IOException e) {
            throw new AssertionError("String text file should never throw IOException", e);
        }
    }

}

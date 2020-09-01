/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.io.PmdFiles;
import net.sourceforge.pmd.util.document.io.TextFile;
import net.sourceforge.pmd.util.document.io.TextFileContent;

/**
 * Represents a textual document, providing methods to edit it incrementally
 * and address regions of text. A text document delegates IO operations
 * to a {@link TextFile}. It reflects some snapshot of the file,
 * though the file may still be edited externally.
 *
 * <p>TextDocument is meant to replace CPD's {@link SourceCode} and PMD's
 * {@link DataSource}, though the abstraction level of {@link DataSource}
 * is the {@link TextFile}.
 */
public interface TextDocument extends Closeable {
    // todo logical sub-documents, to support embedded languages
    //  ideally, just slice the text, and share the positioner

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
     */
    Chars getText();

    /**
     * Returns a checksum for the file text. See {@link TextFileContent#getCheckSum()}.
     */
    long getChecksum();

    /**
     * Returns a reader over the text of this document.
     */
    Reader newReader();


    /**
     * Returns the length in characters of the {@linkplain #getText() text}.
     */
    int getLength();


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
     * Turn a text region into a {@link FileLocation}.
     *
     * @return A new file position
     *
     * @throws IndexOutOfBoundsException If the argument is not a valid region in this document
     */
    FileLocation toLocation(TextRegion region);

    /**
     * Determines the line number at the given offset (inclusive).
     *
     * @return the line number at the given index
     *
     * @throws IndexOutOfBoundsException If the argument is not a valid offset in this document
     */
    default int lineNumberAt(int offset) {
        return toLocation(TextRegion.fromOffsetLength(offset, 0)).getBeginLine();
    }


    /**
     * Returns a region of the {@linkplain #getText() text} as a character sequence.
     *
     * <p>Line endings are normalized to {@link TextFileContent#NORMALIZED_LINE_TERM}.
     */
    Chars slice(TextRegion region);


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
        return new TextDocumentImpl(textFile);
    }

    /**
     * Returns a read-only document for the given text.
     *
     * @see PmdFiles#forString(CharSequence, String, LanguageVersion)
     */
    static TextDocument readOnlyString(final CharSequence source, LanguageVersion lv) {
        return readOnlyString(source, TextFile.UNKNOWN_FILENAME, lv);
    }

    /**
     * Returns a read-only document for the given text. This works as
     * if by calling {@link TextDocument#create(TextFile)} on a textfile
     * produced by {@link PmdFiles#forString(CharSequence, String, LanguageVersion) forString},
     * but doesn't throw {@link IOException}, as such text files will
     * not throw.
     *
     * @see PmdFiles#forString(CharSequence, String, LanguageVersion)
     */
    static TextDocument readOnlyString(@NonNull CharSequence source, @NonNull String filename, @NonNull LanguageVersion lv) {
        TextFile textFile = PmdFiles.forString(source, filename, lv);
        try {
            return create(textFile);
        } catch (IOException e) {
            throw new AssertionError("String text file should never throw IOException", e);
        }
    }

}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.document.io.TextFile;

/**
 * Represents a textual document, providing methods to edit it incrementally
 * and address regions of text. A text document delegates IO operations
 * to a {@link TextFile}. It reflects some snapshot of the file,
 * though the file may still be edited externally. We do not poll for
 * external modifications, instead {@link TextFile} provides a
 * very simple stamping system to avoid overwriting external modifications
 * (by failing in {@link TextEditor#close()}).
 *
 * <p>TextDocument is meant to replace CPD's {@link SourceCode} and PMD's
 * {@link DataSource}, though the abstraction level of {@link DataSource}
 * is the {@link TextFile}.
 *
 * <p>TODO should TextDocument normalize line separators?
 */
public interface TextDocument extends Closeable {

    /**
     * Returns the language version that should be used to parse this file.
     */
    LanguageVersion getLanguageVersion();

    /**
     * Returns the name of the {@link TextFile} backing this instance.
     */
    String getDisplayName();


    /**
     * Returns the current text of this document. Note that this doesn't take
     * external modifications to the {@link TextFile} into account.
     */
    Chars getText();


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
     * Returns a region of the {@linkplain #getText() text} as a character sequence.
     */
    default Chars slice(TextRegion region) {
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }


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


    /**
     * Returns a read-only document for the given text.
     * FIXME for the moment, the language version may be null (for CPD languages).
     *  this may be fixed when CPD and PMD languages are merged
     */
    static TextDocument readOnlyString(final String source, LanguageVersion lv) {
        TextFile textFile = TextFile.readOnlyString(source, "n/a", lv);
        try {
            return new TextDocumentImpl(textFile, lv);
        } catch (IOException e) {
            throw new AssertionError("String text file should never throw IOException", e);
        }
    }

}

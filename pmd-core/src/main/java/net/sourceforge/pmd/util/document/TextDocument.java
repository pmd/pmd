/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;
import net.sourceforge.pmd.util.document.io.StringTextFile;
import net.sourceforge.pmd.util.document.io.TextFile;

/**
 * A view over a {@link TextFile}, providing methods to edit it incrementally
 * and address regions of text.
 *
 * <p>A text document wraps a snapshot of the underlying {@link TextFile}.
 * The text file is
 * It may be edited with a {@linkplain TextEditor} (see {@link #newEditor()})
 */
public interface TextDocument {


    /**
     * Create a new region based on offset coordinates.
     *
     * @param offset 0-based, inclusive offset
     * @param length Length of the region
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    TextRegion createRegion(int offset, int length);


    /**
     * Add line information to the given region. Only the start and end
     * offsets are considered, if the region is already a {@link RegionWithLines},
     * that information is discarded.
     *
     * @return A new region with line information
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionWithLines addLineInfo(TextRegion region);


    /**
     * Returns the current text of this document.
     */
    CharSequence getText();


    /** Returns a region of the {@link #getText() text} as a character sequence. */
    CharSequence subSequence(TextRegion region);


    /**
     * Returns true if this document cannot be written to. In that case,
     * {@link #newEditor()} will throw an exception.
     */
    boolean isReadOnly();


    /**
     * Produce a new editor mutating this file.
     *
     * @return A new editor
     *
     * @throws IOException                   If external modifications were detected
     * @throws UnsupportedOperationException If this document is read-only
     */
    TextEditor newEditor() throws IOException;


    /**
     * Returns a document backed by the given text "file".
     *
     * @throws IOException If an error occurs eg while reading the file
     */
    static TextDocument create(TextFile textFile) throws IOException {
        return new TextDocumentImpl(textFile);
    }


    /**
     * Returns a read-only document for the given text.
     */
    static TextDocument readonlyString(final CharSequence source) {
        try {
            return new TextDocumentImpl(new StringTextFile(source));
        } catch (IOException e) {
            throw new AssertionError("String text source should never throw IOException", e);
        }
    }

}

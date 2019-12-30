/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;
import net.sourceforge.pmd.util.document.io.PhysicalTextSource;
import net.sourceforge.pmd.util.document.io.StringTextSource;

/**
 * Represents a text document. A document provides methods to identify
 * regions of text and to convert between lines and columns.
 *
 * <p>The default document implementations do *not* normalise line endings.
 */
public interface TextDocument {

    /**
     * Create a new region based on line coordinates.
     *
     * @param beginLine   1-based inclusive index (>= 1)
     * @param beginColumn 1-based inclusive index (>= 1)
     * @param endLine     1-based inclusive index (>= 1)
     * @param endColumn   1-based <b>exclusive</b> index (>= 1)
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionWithLines createRegion(int beginLine, int beginColumn, int endLine, int endColumn);


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
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    RegionWithLines addLineInfo(TextRegion region);


    /** Returns the text of this document. */
    CharSequence getText();


    /** Returns a region of the {@link #getText() text} as a character sequence. */
    CharSequence subSequence(TextRegion region);


    /**
     * Returns true if this source cannot be written to. In that case,
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


    static TextDocument forFile(final Path path, final Charset charset) throws IOException {
        return new TextDocumentImpl(PhysicalTextSource.forFile(path, charset));
    }


    static TextDocument forCode(final CharSequence source) {
        try {
            return new TextDocumentImpl(new StringTextSource(source));
        } catch (IOException e) {
            throw new AssertionError("String text source should never throw IOException", e);
        }
    }

}

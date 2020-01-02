/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.Closeable;
import java.io.IOException;
import java.util.ConcurrentModificationException;

import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;
import net.sourceforge.pmd.util.document.io.ReadOnlyFileException;
import net.sourceforge.pmd.util.document.io.ReadOnlyStringBehavior;
import net.sourceforge.pmd.util.document.io.TextFileBehavior;

/**
 * Represents a textual document, providing methods to edit it incrementally
 * and address regions of text. A text document delegates IO operations
 * to a {@link TextFileBehavior}. It reflects some snapshot of the file,
 * though the file may still be edited externally. We do not poll for
 * external modifications, instead {@link TextFileBehavior} provides a
 * very simple stamping system to avoid overwriting external modifications
 * (by failing in {@link TextEditor#close()}).
 */
public interface TextDocument extends Closeable {


    /**
     * Returns the current text of this document. Note that this can only
     * be updated through {@link #newEditor()} and that this doesn't take
     * external modifications to the {@link TextFileBehavior} into account.
     */
    CharSequence getText();


    /**
     * Returns the length in characters of the {@linkplain #getText() text}.
     */
    int getLength();


    /**
     * Create a new region based on its start offset and length. The
     * parameters must identify a valid region in the document. Valid
     * start offsets range from 0 to {@link #getLength()} (inclusive).
     * The sum {@code startOffset + length} must range from {@code startOffset}
     * to {@link #getLength()} (inclusive).
     *
     * <p>Those rules make the region starting at {@link #getLength()}
     * with length 0 a valid region (the caret position at the end of the document).
     *
     * @param startOffset 0-based, inclusive offset for the start of the region
     * @param length      Length of the region in characters.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in this document
     */
    TextRegion createRegion(int startOffset, int length);


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
     * Returns a region of the {@linkplain #getText() text} as a character sequence.
     */
    CharSequence subSequence(TextRegion region);


    /**
     * Returns true if this document cannot be edited. In that case,
     * {@link #newEditor()} will throw an exception. In the general case,
     * nothing prevents this method's result from changing from one
     * invocation to another.
     */
    boolean isReadOnly();


    /**
     * Produce a new editor to edit this file. An editor records modifications
     * and finally commits them with {@link TextEditor#close() close}. After the
     * {@code close} method is called, the {@linkplain #getText() text} of this
     * document is updated. That may render existing text regions created by this
     * document invalid (they won't address the same text, or could be out-of-bounds).
     * Before then, all text regions created by this document stay valid, even after
     * some updates.
     *
     * <p>Only a single editor may be open at a time.
     *
     * @return A new editor
     *
     * @throws IOException                     If an IO error occurs
     * @throws ReadOnlyFileException           If this document is read-only
     * @throws ConcurrentModificationException If an editor is already open for this document
     */
    TextEditor newEditor() throws IOException;


    /**
     * Returns a document backed by the given text "file".
     *
     * @throws IOException If an error occurs eg while reading the file contents
     */
    static TextDocument create(TextFileBehavior textFileBehavior) throws IOException {
        return new TextDocumentImpl(textFileBehavior);
    }


    /**
     * Returns a read-only document for the given text.
     */
    static TextDocument readOnlyString(final String source) {
        try {
            return new TextDocumentImpl(new ReadOnlyStringBehavior(source));
        } catch (IOException e) {
            throw new AssertionError("ReadonlyStringBehavior should never throw IOException", e);
        }
    }

}

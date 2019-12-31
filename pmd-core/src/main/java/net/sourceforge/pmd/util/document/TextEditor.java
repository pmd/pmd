/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.io.ExternalModificationException;
import net.sourceforge.pmd.util.document.io.TextFile;

/**
 * An editor over a {@linkplain TextDocument text document}.
 * Instances of this interface have those responsibilities:
 * <ul>
 * <li>Buffering updates to delay IO interaction;
 * <li>Maintaining a coordinate system consistent with the state of
 * the underlying document. This makes it so, that {@link TextRegion}s
 * created by the text document address the same portion of text even
 * after some updates.
 * </ul>
 *
 * <p>For example, take a document containing the text "a".
 * You insert "k " at index 0. The document is now "k a". If you
 * now insert "g " at index 0, the document is now "k g a", instead
 * of "g k a", meaning that the index 0 is still relative to the old "a"
 * document.
 *
 * <p>Consider that all mutation operations shift the coordinate system
 * transparently.
 */
public interface TextEditor extends AutoCloseable {


    /**
     * Replace a region with some new text.
     *
     * @throws IllegalStateException If this editor has been closed
     */
    void replace(TextRegion region, String textToReplace);


    /**
     * Insert some text in the document.
     *
     * @throws IllegalStateException If this editor has been closed
     */
    void insert(int offset, String textToInsert);


    /**
     * Delete a region in the document.
     *
     * @throws IllegalStateException If this editor has been closed
     */
    void delete(TextRegion region);


    /**
     * Commit the document. The {@linkplain TextDocument#getText() text}
     * of the associated document is updated to reflect the changes. The
     * physical file may be updated as well. This editor becomes unusable
     * after being closed.
     *
     * @throws IOException                   If an IO exception occurs, eg while writing to a file
     * @throws ExternalModificationException If external modifications were detected,
     *                                       in which case the {@link TextFile} is not
     *                                       overwritten
     */
    @Override
    void close() throws IOException;

}

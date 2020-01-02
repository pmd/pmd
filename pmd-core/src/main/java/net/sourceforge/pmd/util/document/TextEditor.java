/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.io.ExternalModificationException;
import net.sourceforge.pmd.util.document.io.TextFileBehavior;
import net.sourceforge.pmd.util.document.util.ZeroBased;

/**
 * Used to update regions of a {@link TextDocument}.
 * The text regions given to all methods here are taken to be in the
 * coordinate system of the underlying document's initial state, and
 * not of the updated state. For that reason, an editor cannot edit
 * overlapping text regions.
 *
 * <p>For example, take a document containing the text {@code "a"}.
 * You insert {@code "k"} at index 0. The document is now {@code "ka"}. If you
 * now insert {@code "g"} at index 0, the document is now {@code "kga"}, instead
 * of {@code "gka"}, meaning that the index 0 is still relative to the old "a"
 * document.
 *
 * <p>Consider that all mutation operations shift the coordinate system
 * transparently.
 */
public interface TextEditor extends AutoCloseable {


    /**
     * Replace a region with some new text.
     *
     * @throws IllegalStateException          If this editor has been closed
     * @throws OverlappingOperationsException If the region overlaps other regions
     *                                        that have been modified by this editor
     */
    void replace(TextRegion region, String textToReplace);


    /**
     * Insert some text in the document.
     *
     * @throws IllegalStateException          If this editor has been closed
     * @throws OverlappingOperationsException If the offset is contained in some region
     *                                        that has been modified by this editor
     */
    void insert(@ZeroBased int offset, String textToInsert);


    /**
     * Delete a region in the document.
     *
     * @throws IllegalStateException          If this editor has been closed
     * @throws OverlappingOperationsException If the region overlaps other regions
     *                                        that have been modified by this editor
     */
    void delete(TextRegion region);


    /**
     * Commit the document. The {@linkplain TextDocument#getText() text}
     * of the associated document is updated to reflect the changes. The
     * {@link TextFileBehavior} is written to. This editor becomes unusable
     * after being closed.
     *
     * @throws IOException                   If an IO exception occurs, eg while writing to a file
     * @throws ExternalModificationException If external modifications were detected,
     *                                       in which case the {@link TextFileBehavior} is not
     *                                       overwritten
     */
    @Override
    void close() throws IOException;


    /**
     * Signals that an operation of a {@link TextEditor} modifies a text
     * region that has already been modified. This means, that the text
     * region doesn't identify the same text in the original document and
     * the document being edited.
     * The text may have been changed, or even deleted.
     */
    class OverlappingOperationsException extends IllegalArgumentException {

        /** Region that has already been modified. */
        public final TextRegion older;

        /** Region for which the modification has been attempted and aborted. */
        public final TextRegion newer;


        public OverlappingOperationsException(TextRegion older, TextRegion newer) {
            super("Regions " + older + " and " + newer + " overlap on " + older.intersect(newer));
            this.older = older;
            this.newer = newer;
        }
    }

}

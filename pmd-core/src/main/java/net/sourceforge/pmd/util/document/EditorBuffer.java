/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.TextDocument.EditorCommitHandler;
import net.sourceforge.pmd.util.document.io.ExternalModificationException;
import net.sourceforge.pmd.util.document.io.ReadOnlyFileException;
import net.sourceforge.pmd.util.document.io.VirtualFile;

/**
 * Helper that buffers operations of a {@link TextEditor} to delay IO
 * interaction.
 */
class EditorBuffer {

    private final VirtualFile backend;
    private final long originalStamp;
    private final CharSequence original;
    private final EditorCommitHandler handler;
    private StringBuilder buffer;


    /** @throws ReadOnlyFileException If the backend is read-only */
    EditorBuffer(CharSequence sequence, long stamp, final VirtualFile backend, EditorCommitHandler handler) {
        if (backend.isReadOnly()) {
            throw new ReadOnlyFileException(backend + " is readonly");
        }

        this.handler = handler;
        this.original = sequence;
        this.backend = backend;
        this.buffer = new StringBuilder(sequence);
        this.originalStamp = stamp;
    }

    void reset() {
        buffer = new StringBuilder(original);
    }


    void replace(final TextRegion region, final String textToReplace) {
        buffer.replace(region.getStartOffset(), region.getEndOffset(), textToReplace);
    }


    void close(TextDocumentImpl sink) throws IOException {
        long timeStamp = backend.fetchStamp();
        if (timeStamp != originalStamp) {
            throw new ExternalModificationException(backend);
        }

        handler.commitNewContents(backend, buffer);

        sink.closeEditor(buffer, backend.fetchStamp());
    }

}

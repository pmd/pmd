/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.io.ExternalModificationException;
import net.sourceforge.pmd.util.document.io.TextFileBehavior;

/**
 * Helper that buffers operations of a {@link TextEditor} to delay IO
 * interaction.
 */
class IoBuffer {

    private final TextFileBehavior backend;
    private final long originalStamp;
    private final StringBuilder buffer;


    IoBuffer(CharSequence sequence, long stamp, final TextFileBehavior backend) {
        if (backend.isReadOnly()) {
            throw new UnsupportedOperationException(backend + " is readonly");
        }

        this.backend = backend;
        this.buffer = new StringBuilder(sequence);
        this.originalStamp = stamp;
    }


    void replace(final TextRegion region, final String textToReplace) {
        buffer.replace(region.getStartOffset(), region.getEndOffset(), textToReplace);
    }


    void close(TextDocumentImpl sink) throws IOException {
        long timeStamp = backend.fetchStamp();
        if (timeStamp != originalStamp) {
            throw new ExternalModificationException(backend);
        }

        backend.writeContents(buffer);

        // Stamp must be fetched after writing
        sink.closeEditor(buffer, backend.fetchStamp());
    }

}

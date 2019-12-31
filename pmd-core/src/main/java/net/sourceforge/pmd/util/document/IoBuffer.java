/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.io.TextFile;

/**
 * Helper that buffers operations of a {@link TextEditor} to delay IO
 * interaction.
 */
class IoBuffer {

    private final TextFile backend;
    private final long originalStamp;
    private final StringBuilder buffer;


    IoBuffer(CharSequence sequence, long stamp, final TextFile writer) {
        if (writer.isReadOnly()) {
            throw new UnsupportedOperationException(writer + " is readonly");
        }

        this.backend = writer;
        this.buffer = new StringBuilder(sequence);
        this.originalStamp = stamp;
    }


    void replace(final TextRegion region, final String textToReplace) {
        buffer.replace(region.getStartOffset(), region.getEndOffset(), textToReplace);
    }


    void close(TextDocumentImpl sink) throws IOException {
        long timeStamp = backend.fetchStamp();
        if (timeStamp != originalStamp) {
            throw new IOException(backend + " was modified externally");
        }

        backend.writeContents(buffer);

        // Stamp must be fetched after writing
        sink.setText(buffer, backend.fetchStamp());
    }

}

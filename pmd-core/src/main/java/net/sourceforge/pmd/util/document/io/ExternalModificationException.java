/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.document.io;

import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextEditor;

/**
 * Thrown when a {@link TextDocument} or {@link TextEditor} detects that
 * an external modification to its underlying {@link TextFile} occurred.
 *
 * <p>This is not meant to be handled below the top-level file parsing
 * loop. External modifications are rare and can be considered unrecoverable
 * for our use case.
 */
public class ExternalModificationException extends RuntimeException {

    // TODO better detection of modifications, eg use WatchService API?

    private final TextFile backend;

    public ExternalModificationException(TextFile backend) {
        super(backend + " was modified externally");
        this.backend = backend;
    }

    /** Returns the file for which external modification occurred. */
    public TextFile getFile() {
        return backend;
    }
}

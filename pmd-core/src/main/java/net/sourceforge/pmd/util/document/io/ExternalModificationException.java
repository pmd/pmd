/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.util.document.io;

import java.io.IOException;

import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextEditor;

/**
 * Thrown when a {@link TextDocument} or {@link TextEditor} detects that
 * {@link TextFileBehavior} has been externally modified.
 *
 * <p>This is not meant to be handled below the top-level file parsing
 * loop. External modifications are rare and can be considered unrecoverable
 * for our use case.
 */
public class ExternalModificationException extends IOException {

    private final TextFileBehavior backend;

    public ExternalModificationException(TextFileBehavior backend) {
        super(backend + " was modified externally");
        this.backend = backend;
    }

    /** Returns the file for which the external modification occurred. */
    public TextFileBehavior getFile() {
        return backend;
    }
}

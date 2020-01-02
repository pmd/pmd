/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

/**
 * Thrown when an attempt to write through a {@link TextFileBehavior}
 * fails because the file is read-only.
 */
public class ReadOnlyFileException extends UnsupportedOperationException {

    public ReadOnlyFileException(String message) {
        super(message);
    }
}

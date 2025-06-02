/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

/**
 * Thrown when an attempt to write through a {@link TextFile}
 * fails because the file is read-only.
 */
public class ReadOnlyFileException extends UnsupportedOperationException {

    public ReadOnlyFileException(TextFile textFile) {
        super("Read only: " + textFile.getFileId().getAbsolutePath());
    }

}

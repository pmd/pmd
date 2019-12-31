/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Physical backend of a {@link TextDocument}, providing read-write
 * access to some location containing text data. Despite the name, this
 * is not necessarily backed by a local file: it may be eg a file system
 * file, an archive entry, an in-memory buffer, etc.
 *
 * <p>This interface provides only block IO access, while {@link TextDocument}
 * adds logic about incremental edition (eg replacing a single region of text).
 *
 * <p>Note that this doesn't have the generality of a {@link File},
 * because it cannot represent binary files.
 */
public interface TextFile {

    /**
     * Returns true if this source cannot be written to. In that case,
     * {@link #writeContents(CharSequence)} will throw an exception.
     */
    boolean isReadOnly();


    /**
     * Writes the given content to the file.
     *
     * @param charSequence Content to write
     *
     * @throws IOException                   If an error occurs
     * @throws UnsupportedOperationException If this text source is read-only
     */
    void writeContents(CharSequence charSequence) throws IOException;


    /**
     * Reads the contents of the underlying character source.
     *
     * @return The most up-to-date content
     */
    CharSequence readContents() throws IOException;


    /**
     * Returns a number identifying the state of the underlying physical
     * record. Every time a text file is modified (either through an instance
     * of this interface or through external filesystem operations), it
     * should change stamps. This however doesn't mandate a pattern for
     * the stamps over time, eg they don't need to increase, or really
     * represent anything.
     */
    long fetchStamp() throws IOException;


    /**
     * Returns an instance of this interface reading & writing to a file.
     * The returned instance may be readonly.
     *
     * @throws IOException If the file is not a regular file
     */
    static TextFile forPath(final Path path, final Charset charset) throws IOException {
        return new FsTextFile(path, charset);
    }


}

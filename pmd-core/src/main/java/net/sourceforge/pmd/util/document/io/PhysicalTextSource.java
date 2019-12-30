/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;

import net.sourceforge.pmd.util.document.TextDocument;

/**
 * This interface presents the file operations needed by {@link TextDocument}
 * to support read/write access. This represents the "backend" of a text document,
 * eg a local file, or an in-memory buffer.
 */
public interface PhysicalTextSource {

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
     * Returns a number identifying the revision. Every time a physical
     * document is modified, it should change stamps. This however doesn't
     * mandate a pattern for the individual stamps, it's less restrictive
     * than eg a "last modified date".
     */
    long fetchStamp() throws IOException;


    /**
     * Returns an instance of this interface reading & writing to a file.
     * The returned instance may be readonly.
     *
     * @throws IOException If the file is not a regular file
     */
    static PhysicalTextSource forFile(final Path path, final Charset charset) throws IOException {
        return new FilePhysicalTextSource(path, charset);
    }


}

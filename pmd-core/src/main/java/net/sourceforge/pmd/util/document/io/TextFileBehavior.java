/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Strategy backing a {@link TextDocument}, providing read-write
 * access to some location containing text data. This interface only
 * provides block IO operations, while {@link TextDocument} adds logic
 * about incremental edition (eg replacing a single region of text).
 */
public interface TextFileBehavior {

    /**
     * Returns true if this file cannot be written to. In that case,
     * {@link #writeContents(CharSequence)} will throw an exception.
     * In the general case, nothing prevents this method's result from
     * changing from one invocation to another.
     */
    boolean isReadOnly();


    /**
     * Writes the given content to the underlying character store.
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
     * Returns an instance of this interface reading and writing to a file.
     * The returned instance may be read-only.
     *
     * @param path    Path to the file
     * @param charset Encoding to use
     *
     * @throws IOException If the file is not a regular file (see {@link Files#isRegularFile(Path, LinkOption...)})
     */
    static TextFileBehavior forPath(final Path path, final Charset charset) throws IOException {
        return new FsTextFileBehavior(path, charset);
    }


    /**
     * Returns a read-only instance of this interface reading from the
     * given dataSource.
     *
     * @param dataSource Data source
     * @param charset    Encoding to use
     */
    static TextFileBehavior forDataSource(final DataSource dataSource, final Charset charset) {
        return new ReadOnlyDataSourceBehavior(dataSource, charset);
    }


}

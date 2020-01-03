/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Represents some location containing character data. Despite the name,
 * it's not necessarily backed by a file in the file-system: it may be
 * eg an in-memory buffer, or a zip entry.
 *
 * <p>Text files must provide read access, and may provide write access.
 * This interface only provides block IO operations, while {@link TextDocument} adds logic
 * about incremental edition (eg replacing a single region of text).
 *
 * <p>This interface is meant to replace {@link DataSource}. "DataSource"
 * is not an appropriate name for a file which can be written to, also,
 * the "data" it should provide is text.
 */
public interface TextFile extends Closeable {

    /**
     * Returns the full file name of the file. This name is used for
     * reporting and should not be interpreted.
     */
    @NonNull
    String getFileName();


    /**
     * Returns the name of this file, relative to one of the given file
     * names. If none of the given file names is a prefix, returns the
     * {@link #getFileName()}. This is only useful for reporting.
     *
     * @param baseFileNames A list of directory prefixes that should be truncated
     *
     * @throws NullPointerException If the parameter is null
     */
    @NonNull
    default String getShortFileName(List<String> baseFileNames) {
        AssertionUtil.requireParamNotNull("baseFileNames", baseFileNames);
        return getFileName();
    }


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
     * @throws IOException           If this instance is closed
     * @throws IOException           If an error occurs
     * @throws ReadOnlyFileException If this text source is read-only
     */
    void writeContents(CharSequence charSequence) throws IOException;


    /**
     * Reads the contents of the underlying character source.
     *
     * @return The most up-to-date content
     *
     * @throws IOException If this instance is closed
     * @throws IOException If reading causes an IOException
     */
    CharSequence readContents() throws IOException;


    /**
     * Returns a number identifying the state of the underlying physical
     * record. Every time a text file is modified (either through an instance
     * of this interface or through external filesystem operations), it
     * should change stamps. This however doesn't mandate a pattern for
     * the stamps over time, eg they don't need to increase, or really
     * represent anything.
     *
     * @throws IOException If this instance is closed
     * @throws IOException If reading causes an IOException
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
    static TextFile forPath(final Path path, final Charset charset) throws IOException {
        return new FileSysTextFile(path, charset);
    }


    /**
     * Returns a read-only instance of this interface reading from a string.
     *
     * @param source Text of the file
     */
    static TextFile readOnlyString(String source) {
        return new StringFile(source, null);
    }


    /**
     * Returns a read-only instance of this interface reading from a string.
     *
     * @param source Text of the file
     * @param name   File name to use
     */
    static TextFile readOnlyString(String source, String name) {
        return new StringFile(source, name);
    }
}

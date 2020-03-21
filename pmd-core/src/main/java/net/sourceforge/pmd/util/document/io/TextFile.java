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

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Represents some location containing character data. Despite the name,
 * it's not necessarily backed by a file in the file-system: it may be
 * eg an in-memory buffer, or a zip entry, ie it's an abstraction. Text
 * files are the input which PMD and CPD process.
 *
 * <p>Text files must provide read access, and may provide write access.
 * This interface only provides block IO operations, while {@link TextDocument} adds logic
 * about incremental edition (eg replacing a single region of text).
 *
 * <p>This interface is meant to replace {@link DataSource} and {@link SourceCode.CodeLoader}.
 * "DataSource" is not an appropriate name for a file which can be written
 * to, also, the "data" it provides is text, not bytes.
 */
public interface TextFile extends Closeable {

    /**
     * Returns the full file name of the file. This name is used for
     * reporting and should not be interpreted.
     */
    @NonNull
    String getDisplayName();


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

    // <editor-fold desc="Creator methods" collapsed-by-default="true">

    /**
     * Returns an instance of this interface reading and writing to a file.
     * The returned instance may be read-only.
     *
     * @param path    Path to the file
     * @param charset Encoding to use
     *
     * @throws IOException If the file is not a regular file (see {@link Files#isRegularFile(Path, LinkOption...)})
     * @throws NullPointerException if the path or the charset is null
     */
    static TextFile forPath(final Path path, final Charset charset) throws IOException {
        return new NioTextFile(path, charset);
    }


    /**
     * Returns a read-only instance of this interface reading from a string.
     *
     * @param source Text of the file
     * @param name   File name to use
     *
     * @throws NullPointerException If the source text or the name is null
     */
    static TextFile readOnlyString(String source, String name, LanguageVersion lv) {
        return new StringTextFile(source, name, lv);
    }

    // </editor-fold>

}

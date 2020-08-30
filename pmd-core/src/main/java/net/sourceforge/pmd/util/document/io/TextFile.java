/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;
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
     * Returns the language version which should be used to parse this
     * file. It's the text file's responsibility, so that the {@linkplain #getDisplayName() display name}
     * is never interpreted as a file name, which may not be true.
     *
     * @param discoverer Object which knows about language versions selected per-language
     *
     * @return A language version
     */
    default @NonNull LanguageVersion getLanguageVersion(LanguageVersionDiscoverer discoverer) {
        // TODO remove this, when listeners have been refactored, etc.
        return discoverer.getDefaultLanguageVersionForFile(getDisplayName());
    }

    default boolean matches(Predicate<File> filter) {
        return filter.test(new File(getDisplayName()));
    }


    /**
     * Returns an identifier for the path of this file. This should not
     * be interpreted as a {@link File}, it may not be a file on this
     * filesystem. The only requirement for this method, is that two
     * distinct text files should have distinct path IDs, and that from
     * one analysis to the next, the path ID of logically identical files
     * be the same.
     *
     * <p>Basically this may be implemented as a URL, or a file path. It
     * is used to index violation caches.
     */
    String getPathId();


    /**
     * Returns a display name for the file. This name is used for
     * reporting and should not be interpreted. It may be relative
     * to a directory or so. Use {@link #getPathId()} when you want
     * an identifier.
     */
    @NonNull
    String getDisplayName();


    /**
     * Returns true if this file cannot be written to. In that case,
     * {@link #writeContents(TextFileContent)} will throw an exception.
     * In the general case, nothing prevents this method's result from
     * changing from one invocation to another.
     */
    boolean isReadOnly();


    /**
     * Writes the given content to the underlying character store.
     *
     * @param content Content to write, with lines separated by the given line separator
     *
     * @throws IOException           If this instance is closed
     * @throws IOException           If an error occurs
     * @throws ReadOnlyFileException If this text source is read-only
     */
    void writeContents(TextFileContent content) throws IOException;


    /**
     * Reads the contents of the underlying character source.
     *
     * @return The most up-to-date content
     *
     * @throws IOException If this instance is closed
     * @throws IOException If reading causes an IOException
     */
    TextFileContent readContents() throws IOException;


    /**
     * Release resources associated with this text file. Is a noop if
     * it is called several times.
     *
     * @throws IOException If an IO exception occurs
     */
    @Override
    void close() throws IOException;


    @Deprecated
    default DataSource asDataSource() {
        return new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getNiceFileName(boolean shortNames, String inputFileName) {
                return getDisplayName();
            }

            @Override
            public void close() throws IOException {
                TextFile.this.close();
            }
        };
    }

}

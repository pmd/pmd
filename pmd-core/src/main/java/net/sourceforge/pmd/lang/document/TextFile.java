/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.internal.util.BaseCloseable;
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.TextFileBuilder.ForCharSeq;
import net.sourceforge.pmd.lang.document.TextFileBuilder.ForNio;
import net.sourceforge.pmd.lang.document.TextFileBuilder.ForReader;
import net.sourceforge.pmd.util.datasource.DataSource;

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
     * The name used for a file that has no name. This is mostly only
     * relevant for unit tests.
     */
    String UNKNOWN_FILENAME = "(unknown file)";


    /**
     * Returns the language version which should be used to process this
     * file. This is a property of the file, which allows sources for
     * several different language versions to be processed in the same
     * PMD run. It also makes it so, that the file extension is not interpreted
     * to find out the language version after the initial file collection
     * phase.
     *
     * @return A language version
     */
    @NonNull
    LanguageVersion getLanguageVersion();


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
     * to a directory or so, it may also not be normalized. Use
     * {@link #getPathId()} when you want an identifier.
     */
    @NonNull
    String getDisplayName();


    /**
     * Returns true if this file cannot be written to. In that case,
     * {@link #writeContents(TextFileContent)} will throw an exception.
     * In the general case, nothing prevents this method's result from
     * changing from one invocation to another.
     */
    default boolean isReadOnly() {
        return true;
    }


    /**
     * Writes the given content to the underlying character store.
     *
     * @param content Content to write, with lines separated by the given line separator
     *
     * @throws IOException           If this instance is closed
     * @throws IOException           If an error occurs
     * @throws ReadOnlyFileException If this text source is read-only
     */
    default void writeContents(TextFileContent content) throws IOException {
        throw new ReadOnlyFileException(this);
    }


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


    /**
     * Text file equality is implementation-defined. The only constraint
     * is that equal text files should have equal path IDs (and the usual
     * properties mandated by {@link Object#equals(Object)}).
     */
    // currently:
    // - Path-based TextFiles compare their path for equality, where the path is not normalized.
    // - Reader- and String-based TextFiles use identity semantics.
    @Override
    boolean equals(Object o);

    // factory methods

    /**
     * Returns an instance of this interface reading and writing to a file.
     * See {@link #builderForPath(Path, Charset, LanguageVersion) builderForPath}
     * for more info.
     *
     * @param path            Path to the file
     * @param charset         Encoding to use
     * @param languageVersion Language version to use
     *
     * @throws NullPointerException If any parameter is null
     */
    static TextFile forPath(Path path, Charset charset, LanguageVersion languageVersion) {
        return builderForPath(path, charset, languageVersion)
                .withDisplayName(path.toString())
                .build();
    }

    /**
     * Returns a builder for a textfile that reads and write to the file.
     * The returned instance may be read-only. If the file is not a regular
     * file (eg, a directory), or does not exist, then {@link TextFile#readContents()}
     * will throw.
     *
     * <p>The display name is by default the given path (without normalization),
     * while the path id is the absolute path.
     *
     * @param path            Path to the file
     * @param charset         Encoding to use
     * @param languageVersion Language version to use
     *
     * @throws NullPointerException If any parameter is null
     */
    static TextFileBuilder builderForPath(Path path, Charset charset, LanguageVersion languageVersion) {
        return new ForNio(languageVersion, path, charset);
    }

    /**
     * Returns a read-only TextFile reading from a string.
     * Note that this will normalize the text, in such a way that {@link TextFile#readContents()}
     * may not produce exactly the same char sequence.
     *
     * @param charseq         Text of the file
     * @param pathId          File name to use as path id
     * @param languageVersion Language version
     *
     * @throws NullPointerException If any parameter is null
     */
    static TextFile forCharSeq(CharSequence charseq, String pathId, LanguageVersion languageVersion) {
        return builderForCharSeq(charseq, pathId, languageVersion)
                .withDisplayName(pathId)
                .build();
    }

    /**
     * Returns a read-only TextFile reading from a string.
     * Note that this will normalize the text, in such a way that {@link TextFile#readContents()}
     * may not produce exactly the same char sequence.
     *
     * @param charseq         Text of the file
     * @param pathId          File name to use as path id
     * @param languageVersion Language version
     *
     * @throws NullPointerException If any parameter is null
     */
    static TextFileBuilder builderForCharSeq(CharSequence charseq, String pathId, LanguageVersion languageVersion) {
        return new ForCharSeq(charseq, pathId, languageVersion);
    }

    /**
     * Returns a read-only instance of this interface reading from a reader.
     * The reader is first read when {@link TextFile#readContents()} is first
     * called, and is closed when that method exits. Note that this may
     * only be called once, afterwards, {@link TextFile#readContents()} will
     * throw an {@link IOException}.
     *
     * @param reader          Text of the file
     * @param pathId          File name to use as path id
     * @param languageVersion Language version
     *
     * @throws NullPointerException If any parameter is null
     */
    static TextFile forReader(Reader reader, String pathId, LanguageVersion languageVersion) {
        return builderForReader(reader, pathId, languageVersion)
                .withDisplayName(pathId)
                .build();
    }

    /**
     * Returns a read-only builder reading from a reader.
     * The reader is first read when {@link TextFile#readContents()} is first
     * called, and is closed when that method exits. Note that this may
     * only be called once, afterwards, {@link TextFile#readContents()} will
     * throw an {@link IOException}.
     *
     * @param reader          Text of the file
     * @param pathId          File name to use as path id
     * @param languageVersion Language version
     *
     * @throws NullPointerException If any parameter is null
     */
    static TextFileBuilder builderForReader(Reader reader, String pathId, LanguageVersion languageVersion) {
        return new ForReader(languageVersion, reader, pathId);
    }

    /**
     * Wraps the given {@link DataSource} (provided for compatibility).
     * Note that data sources are only usable once (even {@link DataSource#forString(String, String)}),
     * so calling {@link TextFile#readContents()} twice will throw the second time.
     *
     * @deprecated This is deprecated until PMD 7 is out, after which
     *     {@link DataSource} will be removed.
     */
    @Deprecated
    @DeprecatedUntil700
    static TextFile dataSourceCompat(DataSource ds, PMDConfiguration config) {
        String pathId = ds.getNiceFileName(false, null);
        LanguageVersion languageVersion = config.getLanguageVersionOfFile(pathId);
        if (languageVersion == null) {
            throw new NullPointerException("no language version detected for " + pathId);
        }
        String shortPaths = config.getInputPathList().stream().map(Path::toString).collect(Collectors.joining(","));
        class DataSourceTextFile extends BaseCloseable implements TextFile {

            @Override
            public @NonNull LanguageVersion getLanguageVersion() {
                return languageVersion;
            }

            @Override
            public String getPathId() {
                return pathId;
            }

            @Override
            public @NonNull String getDisplayName() {
                return ds.getNiceFileName(false, shortPaths);
            }

            @Override
            public TextFileContent readContents() throws IOException {
                ensureOpen();
                try (InputStream is = ds.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(is, config.getSourceEncoding()))) {
                    String contents = IOUtil.readToString(reader);
                    return TextFileContent.fromCharSeq(contents);
                }
            }

            @Override
            protected void doClose() throws IOException {
                ds.close();
            }
        }

        return new DataSourceTextFile();
    }
}

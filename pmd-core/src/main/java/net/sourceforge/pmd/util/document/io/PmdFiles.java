/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.internal.util.BaseCloseable;
import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;

/**
 * Utilities to create and manipulate {@link TextFile} instances.
 */
public final class PmdFiles {

    private PmdFiles() {
        // utility class
    }


    /**
     * See {@linkplain #forPath(Path, Charset, LanguageVersion, String, ReferenceCountedCloseable) the main overload}.
     * This defaults the display name to the file path, and has no dependency
     * on a closeable file system.
     */
    public static TextFile forPath(final Path path, final Charset charset, LanguageVersion langVersion) {
        return forPath(path, charset, langVersion, null);
    }

    /**
     * See {@linkplain #forPath(Path, Charset, LanguageVersion, String, ReferenceCountedCloseable) the main overload}.
     * This defaults the display name to the file path.
     */
    public static TextFile forPath(final Path path,
                                   final Charset charset,
                                   LanguageVersion langVersion,
                                   @Nullable ReferenceCountedCloseable fileSystemCloseable) {
        return forPath(path, charset, langVersion, null, fileSystemCloseable);
    }

    /**
     * Returns an instance of this interface reading and writing to a file.
     * The returned instance may be read-only. If the file is not a regular
     * file (eg, a directory), or does not exist, then {@link TextFile#readContents()}
     * will throw.
     *
     * @param path                Path to the file
     * @param charset             Encoding to use
     * @param langVersion         Language version to use
     * @param displayName         A custom display name. If null it will default to the file path
     * @param fileSystemCloseable A closeable that must be closed after the new file is closed itself.
     *                            This is used to close zip archives after all the textfiles within them
     *                            are closed. In this case, the reference counted closeable tracks a ZipFileSystem.
     *                            If null, then the new text file doesn't have a dependency.
     *
     * @throws NullPointerException if the path, the charset, or the language version are null
     */
    public static TextFile forPath(final Path path,
                                   final Charset charset,
                                   final LanguageVersion langVersion,
                                   final @Nullable String displayName,
                                   final @Nullable ReferenceCountedCloseable fileSystemCloseable) {
        return new NioTextFile(path, charset, langVersion, displayName, fileSystemCloseable);
    }

    /**
     * Returns a read-only TextFile reading from a string.
     * Note that this will normalize the text, in such a way that {@link TextFile#readContents()}
     * may not produce exactly the same char sequence.
     *
     * @param source Text of the file
     * @param name   File name to use (both as display name and path ID)
     * @param lv     Language version
     *
     * @throws NullPointerException If the source text or the name is null
     */
    public static TextFile forString(@NonNull String source, @NonNull String name, @NonNull LanguageVersion lv) {
        return new StringTextFile(source, name, lv);
    }

    /**
     * Returns a read-only instance of this interface reading from a reader.
     * The reader is first read when {@link TextFile#readContents()} is first
     * called, and is closed when that method exits. Note that this may
     * only be called once, afterwards, {@link TextFile#readContents()} will
     * throw an {@link IOException}.
     *
     * @param reader Text of the file
     * @param name   File name to use
     * @param lv     Language version, which overrides the default language associations given by the file extension
     *
     * @throws NullPointerException If any parameter is null
     */
    public static TextFile forReader(@NonNull Reader reader, @NonNull String name, @NonNull LanguageVersion lv) {
        return new ReaderTextFile(reader, name, lv);
    }

    /**
     * Wraps the given {@link DataSource} (provided for compatibility).
     * Note that data sources are only usable once (even {@link DataSource#forString(String, String)}),
     * so calling {@link TextFile#readContents()} twice will throw the second time.
     *
     * @deprecated This is only a transitional API for the PMD 7 branch
     */
    @Deprecated
    public static TextFile dataSourceCompat(DataSource ds, PMDConfiguration config) {
        class DataSourceTextFile extends BaseCloseable implements TextFile {

            @Override
            public @NonNull LanguageVersion getLanguageVersion() {
                return config.getLanguageVersionOfFile(getPathId());
            }

            @Override
            public String getPathId() {
                return ds.getNiceFileName(false, null);
            }

            @Override
            public @NonNull String getDisplayName() {
                return ds.getNiceFileName(config.isReportShortNames(), config.getInputPaths());
            }

            @Override
            public boolean isReadOnly() {
                return true;
            }

            @Override
            public void writeContents(TextFileContent content) throws IOException {
                throw new ReadOnlyFileException();
            }

            @Override
            public TextFileContent readContents() throws IOException {
                try (InputStream is = ds.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(is, config.getSourceEncoding()))) {
                    String contents = IOUtils.toString(reader);
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


    /** The language version must be non-null. */
    @Deprecated
    private static final Language DUMMY_CPD_LANG = new BaseLanguageModule("cpd", "cpd", "cpd", "cpd") {
        {
            addDefaultVersion("0", parserOptions -> task -> {
                throw new UnsupportedOperationException();
            });
        }

    };

    @Deprecated
    public static LanguageVersion dummyCpdVersion() {
        return DUMMY_CPD_LANG.getDefaultVersion();
    }

    /**
     * Bridges {@link SourceCode} with {@link TextFile}. This allows
     * javacc tokenizers to work on text documents.
     *
     * @deprecated This is only a transitional API for the PMD 7 branch
     */
    @Deprecated
    public static TextFile cpdCompat(SourceCode sourceCode) {
        return new StringTextFile(
            sourceCode.getCodeBuffer().toString(),
            sourceCode.getFileName(),
            dummyCpdVersion()
        );
    }
}

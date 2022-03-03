/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.File;
import java.io.IOException;

import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;
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
 *
 * <h2>Experimental</h2>
 * This interface will change in PMD 7 to support read/write operations
 * and other things. You don't need to use it in PMD 6, as {@link FileCollector}
 * decouples you from this. A file collector is available through {@link PmdAnalysis#files()}.
 */
@Experimental
public interface TextFile {

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
     * to a directory, may use platform-specific path separators,
     * may not be normalized. Use {@link #getPathId()} when you
     * want an identifier.
     */
    String getDisplayName();


    /**
     * Reads the contents of the underlying character source.
     *
     * @return The most up-to-date content
     *
     * @throws IOException If this instance is closed
     * @throws IOException If reading causes an IOException
     */
    String readContents() throws IOException;

    /**
     * Compatibility with {@link DataSource} (pmd internals still use DataSource in PMD 6).
     */
    @Deprecated
    DataSource toDataSourceCompat();

}

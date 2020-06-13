/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;

import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.lang.LanguageVersion;

/**
 * Utilities to create and manipulate {@link TextFile} instances.
 */
public final class PmdFiles {

    private PmdFiles() {
        // utility class
    }


    /**
     * Returns an instance of this interface reading and writing to a file.
     * The returned instance may be read-only.
     *
     * @param path    Path to the file
     * @param charset Encoding to use
     *
     * @throws IOException          If the file is not a regular file (see {@link Files#isRegularFile(Path,
     *                              LinkOption...)})
     * @throws NullPointerException if the path or the charset is null
     */
    public static TextFile forPath(final Path path, final Charset charset) throws IOException {
        return new NioTextFile(path, charset);
    }

    /**
     * Returns a read-only instance of this interface reading from a string.
     *
     * @param source Text of the file
     *
     * @throws NullPointerException If the source text is null
     */
    public static TextFile readOnlyString(String source) {
        return readOnlyString(source, "n/a", null);
    }

    /**
     * Returns a read-only instance of this interface reading from a string.
     *
     * @param source Text of the file
     * @param name   File name to use
     *
     * @throws NullPointerException If the source text or the name is null
     */
    public static TextFile readOnlyString(String source, String name, LanguageVersion lv) {
        return new StringTextFile(source, name, lv);
    }

    /**
     * Wraps the given {@link SourceCode} (provided for compatibility).
     */
    public static TextFile cpdCompat(SourceCode sourceCode) {
        return new StringTextFile(sourceCode.getCodeBuffer(), sourceCode.getFileName(), null);
    }
}

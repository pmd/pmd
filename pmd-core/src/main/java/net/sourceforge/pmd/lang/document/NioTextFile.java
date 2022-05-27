/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

/**
 * A {@link TextFile} backed by a file in some {@link FileSystem}.
 */
@Experimental
class NioTextFile implements TextFile {

    private final Path path;
    private final Charset charset;
    private final LanguageVersion languageVersion;
    private final String displayName;
    private final String pathId;

    NioTextFile(Path path, Charset charset, LanguageVersion languageVersion, String displayName) {
        AssertionUtil.requireParamNotNull("path", path);
        AssertionUtil.requireParamNotNull("charset", charset);
        AssertionUtil.requireParamNotNull("language version", languageVersion);

        this.displayName = displayName;
        this.path = path;
        this.charset = charset;
        this.languageVersion = languageVersion;
        this.pathId = path.toAbsolutePath().toString();
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getPathId() {
        return pathId;
    }


    @Override
    public String readContents() throws IOException {

        if (!Files.isRegularFile(path)) {
            throw new IOException("Not a regular file: " + path);
        }

        try (BufferedReader br = Files.newBufferedReader(path, charset)) {
            return IOUtil.readToString(br);
        }
    }

    @Override
    public DataSource toDataSourceCompat() {
        return new FileDataSource(path.toFile());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NioTextFile that = (NioTextFile) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathId);
    }

    @Override
    public String toString() {
        return getPathId();
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.datasource.FileDataSource;

/**
 * Collects files to analyse before a PMD run. This API allows opening
 * zip files and makes sure they will be closed at the end of a run.
 *
 * @author Clément Fournier
 */
@Experimental
public final class NioTextFile implements TextFile {

    private final Path path;
    private final Charset charset;
    private final String displayName;
    private final LanguageVersion version;
    private final String pathId;

    public NioTextFile(Path path, Charset charset, String displayName, LanguageVersion version) {
        this.path = path;
        this.charset = charset;
        this.displayName = displayName;
        this.version = version;
        this.pathId = path.toAbsolutePath().toString();
    }

    @Override
    public String getPathId() {
        return pathId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return version;
    }

    @Override
    public String readContents() throws IOException {
        try (BufferedReader br = Files.newBufferedReader(path, charset)) {
            return IOUtils.toString(br);
        }
    }

    @Override
    public DataSource toDataSourceCompat() {
        return new FileDataSource(path.toFile());
    }
}

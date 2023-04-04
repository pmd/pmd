/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.datasource.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.internal.util.ShortFilenameUtil;

/**
 * DataSource implementation to read data from a {@link java.nio.file.Path}.
 * This can also be a Path inside a zip file.
 */
//TODO This class (and all other DataSources) can be probably removed with PMD 7 in favor of TextFile
public class PathDataSource extends AbstractDataSource {
    private final String displayName;

    private final Path path;

    /**
     * @param path the file to read
     */
    public PathDataSource(Path path) {
        this(path, null);
    }

    /**
     * @param path the file to read
     */
    public PathDataSource(Path path, String displayName) {
        this.path = path;
        this.displayName = displayName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return Files.newInputStream(path);
    }

    @Override
    public String getNiceFileName(boolean shortNames, String inputPaths) {
        return glomName(shortNames, inputPaths);
    }

    private String getAbsoluteFilePath() {
        if ("jar".equals(path.toUri().getScheme())) {
            return new File(URI.create(path.toUri().getSchemeSpecificPart()).getPath()).toString();
        }
        return path.toFile().getAbsolutePath();
    }

    private String getSimpleFilePath() {
        if ("jar".equals(path.toUri().getScheme())) {
            String[] zipAndFile = URI.create(path.toUri().getSchemeSpecificPart()).getPath().split("!");
            return new File(zipAndFile[0]).getName() + "!" + new File(zipAndFile[1]).getName();
        }
        return path.toFile().getName();
    }

    private String glomName(boolean shortNames, String inputPaths) {
        if (displayName != null) {
            return displayName;
        }
        if (shortNames) {
            if (inputPaths != null) {
                List<String> inputPathPrefixes = Arrays.asList(inputPaths.split(","));
                final String absoluteFilePath = getAbsoluteFilePath();
                return ShortFilenameUtil.determineFileName(inputPathPrefixes, absoluteFilePath);
            } else {
                // if the 'master' file is not specified, just use the file name
                return getSimpleFilePath();
            }
        }
        try {
            return path.toFile().getCanonicalFile().getAbsolutePath();
        } catch (Exception e) {
            // Exception might occur when symlinks can't be resolved (permission problem)
            // or when path is inside a jar/zip file
            return getAbsoluteFilePath();
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(this.getClass().getSimpleName())
                .append('[')
                .append(path.toUri())
                .append(']')
                .toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("PMD.CloseResource")
        PathDataSource other = (PathDataSource) obj;
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }


}

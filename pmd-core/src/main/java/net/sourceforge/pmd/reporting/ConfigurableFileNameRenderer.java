/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;

/**
 * @author Clément Fournier
 */
public class ConfigurableFileNameRenderer implements FileNameRenderer {

    private final List<Path> relativizeRootPaths = new ArrayList<>();

    /**
     * Add a prefix that is used to relativize file paths as their display name.
     * For instance, when adding a file {@code /tmp/src/main/java/org/foo.java},
     * and relativizing with {@code /tmp/src/}, the registered {@link TextFile}
     * will have a path id of {@code /tmp/src/main/java/org/foo.java}, and a
     * display name of {@code main/java/org/foo.java}.
     *
     * <p>This only matters for files added from a {@link Path} object.
     *
     * @param path Path with which to relativize
     */
    public void relativizeWith(Path path) {
        this.relativizeRootPaths.add(Objects.requireNonNull(path));
        this.relativizeRootPaths.sort(Comparator.naturalOrder());
    }

    @Override
    public String getDisplayName(@NonNull FileId fileId) {
        String localDisplayName = getLocalDisplayName(fileId);
        FileId parent = fileId.getParentFsPath();
        if (parent != null) {
            return getDisplayName(parent) + "!" + localDisplayName;
        }
        return localDisplayName;
    }

    private String getLocalDisplayName(FileId file) {
        if (!relativizeRootPaths.isEmpty()) {
            return getDisplayName(file, relativizeRootPaths);
        }
        return file.getOriginalPath();
    }

    private static int countSegments(String best) {
        return StringUtils.countMatches(best, File.separatorChar);
    }

    private static final Pattern PATH_SEP_PAT = Pattern.compile("[/\\\\]");

    // both input paths represent absolute paths
    private static String relativizePath(Path base, String other) {
        assert base.isAbsolute() : "Expected an absolute path: " + base;

        // If the second path starts with C:\, remove the C: part for
        // consistency.
        int windowsDriveEndIndex = other.indexOf(':');
        if (windowsDriveEndIndex != -1 && windowsDriveEndIndex < other.length() - 1) {
            other = other.substring(windowsDriveEndIndex + 1);
        }
        String[] otherSegments = PATH_SEP_PAT.split(other);
        int prefixLength = 0;
        // We remove 1 because since 'other' is absolute it always starts
        // with the empty string.
        int maxi = Math.min(base.getNameCount(), otherSegments.length - 1);
        while (prefixLength < maxi
            // here we add 1 for the same reason                          vvvvvvvvvvvvvvvv
            && base.getName(prefixLength).toString().equals(otherSegments[prefixLength + 1])) {
            prefixLength++;
        }

        if (prefixLength == 0) {
            return other;
        }

        List<String> relative = new ArrayList<>();
        for (int i = prefixLength; i < base.getNameCount(); i++) {
            relative.add("..");
        }
        relative.addAll(Arrays.asList(otherSegments).subList(prefixLength + 1, otherSegments.length));
        return String.join(File.separator, relative);
    }

    /** Return whether the path is the root path (/). */
    private static boolean isFileSystemRoot(Path root) {
        return root.isAbsolute() && root.getNameCount() == 0;
    }

    /**
     * Return the textfile's display name. Takes the shortest path we
     * can construct from the relativize roots.
     *
     * <p>package private for test only</p>
     */
    static String getDisplayName(FileId file, List<Path> relativizeRoots) {
        final String fileAbsPath = file.getAbsolutePath();
        String best = fileAbsPath;
        for (Path root : relativizeRoots) {
            if (isFileSystemRoot(root)) {
                // Absolutize the path. Since the relativize roots are
                // sorted by ascending length, this should be the first in the list
                // (so another root can override it).
                best = fileAbsPath;
                continue;
            }

            String relative = relativizePath(root.toAbsolutePath(), fileAbsPath);
            if (countSegments(relative) < countSegments(best)) {
                best = relative;
            }
        }
        return best;
    }
}

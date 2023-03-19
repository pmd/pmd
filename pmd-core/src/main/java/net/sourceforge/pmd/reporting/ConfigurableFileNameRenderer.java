/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.PmdAnalysis;
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
    public String getDisplayName(FileId fileId) {
        String localDisplayName = getLocalDisplayName(fileId);
        if (fileId.getParentFsPath() != null) {
            return getDisplayName(fileId.getParentFsPath()) + "!" + localDisplayName;
        }
        return localDisplayName;
    }

    private String getLocalDisplayName(FileId file) {
        if (!relativizeRootPaths.isEmpty()) {
            return PmdAnalysis.getDisplayName(file, relativizeRootPaths);
        }
        return file.getOriginalPath();
    }
}

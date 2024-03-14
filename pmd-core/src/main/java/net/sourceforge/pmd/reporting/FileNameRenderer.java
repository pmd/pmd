/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;

/**
 * Strategy to render a {@link FileId} into a display name. This is used
 * to prettify file names in renderers using relative paths eg.
 *
 * @author Clément Fournier
 */
public interface FileNameRenderer {


    /**
     * Return a display name for the given file id.
     * @param fileId A file id
     * @return A display name
     */
    String getDisplayName(@NonNull FileId fileId);


    default String getDisplayName(@NonNull TextFile textFile) {
        return getDisplayName(textFile.getFileId());
    }

}

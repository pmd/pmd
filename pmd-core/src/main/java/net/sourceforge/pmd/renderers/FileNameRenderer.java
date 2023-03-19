/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.lang.document.PathId;
import net.sourceforge.pmd.lang.document.TextFile;

/**
 * Renders a {@link PathId} into a display name.
 *
 * @author Clément Fournier
 */
public interface FileNameRenderer {


    String getDisplayName(PathId pathId);

    default String getDisplayName(TextFile textFile) {
        return getDisplayName(textFile.getPathId());
    }

}

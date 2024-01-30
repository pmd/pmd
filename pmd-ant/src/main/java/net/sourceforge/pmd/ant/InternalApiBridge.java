/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import java.io.IOException;

import org.apache.tools.ant.Project;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

/**
 * Gives access to package private methods.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {
        // utility class
    }

    public static GlobalAnalysisListener newListener(Formatter formatter, Project project) throws IOException {
        return formatter.newListener(project);
    }
}

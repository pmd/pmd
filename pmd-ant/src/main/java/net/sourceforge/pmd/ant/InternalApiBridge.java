/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import java.io.IOException;

import org.apache.tools.ant.Project;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static GlobalAnalysisListener newListener(Formatter formatter, Project project) throws IOException {
        return formatter.newListener(project);
    }
}

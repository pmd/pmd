/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexLanguageProperties;

/**
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {
        // utility class
    }

    public static ApexMultifileAnalysis createApexMultiFileAnalysis(ApexLanguageProperties properties) {
        return new ApexMultifileAnalysis(properties);
    }
}

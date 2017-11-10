/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.RuleSet;

public final class RuleSetUtils {

    private RuleSetUtils() {
        // Utility class
    }

    /**
     * Gets the sanitized base name of the ruleset.
     * For some reason, the filename might contain some newlines, which are removed.
     * @param ruleset
     * @return
     */
    public static String getRuleSetFilename(RuleSet ruleset) {
        return getRuleSetFilename(ruleset.getFileName());
    }

    public static String getRuleSetFilename(String rulesetFileName) {
        return FilenameUtils.getBaseName(StringUtils.chomp(rulesetFileName));
    }

}

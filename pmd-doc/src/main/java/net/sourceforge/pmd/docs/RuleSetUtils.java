/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.docs;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleReference;

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

    /**
     * A ruleset is considered deprecated, if it only contains rule references
     * and all rule references are deprecated.
     *
     * @param ruleset
     * @return
     */
    public static boolean isRuleSetDeprecated(RuleSet ruleset) {
        boolean result = true;
        for (Rule rule : ruleset.getRules()) {
            if (!(rule instanceof RuleReference) || !rule.isDeprecated()) {
                result = false;
                break;
            }
        }
        return result;
    }

    public static String getRuleSetClasspath(RuleSet ruleset) {
        final String RESOURCES_PATH = "/resources/";
        String filename = FilenameUtils.normalize(StringUtils.chomp(ruleset.getFileName()), true);
        int startIndex = filename.lastIndexOf(RESOURCES_PATH);
        if (startIndex > -1) {
            return filename.substring(startIndex + RESOURCES_PATH.length());
        } else {
            return filename;
        }
    }

    /**
     * Recursively resolves rule references until the last reference.
     * The last reference is returned.
     * If the given rule not a reference, the rule is returned.
     *
     * @param rule
     * @return
     */
    public static Rule resolveRuleReferences(Rule rule) {
        Rule result = rule;
        Rule ref = rule;
        while (ref instanceof RuleReference) {
            // remember the last reference
            result = ref;
            ref = ((RuleReference) ref).getRule();
        }
        return result;
    }
}

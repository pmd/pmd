/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSets;

/**
 * A mapper from rule class names to rule instances for cached rules.
 */
public class CachedRuleMapper {

    private final Map<String, Rule> ruleByClassName = new HashMap<>();

    /**
     * Finds a rule instance for the given rule class name
     * @param className The name of the rule class that generated the cache entry
     * @return The requested rule
     */
    public Rule getRuleForClass(final String className) {
        return ruleByClassName.get(className);
    }

    /**
     * Initialize the mapper with the given rulesets.
     * @param rs The rulesets from which to retrieve rules.
     */
    public void initialize(final RuleSets rs) {
        for (final Rule r : rs.getAllRules()) {
            ruleByClassName.put(r.getRuleClass(), r);
        }
    }
}

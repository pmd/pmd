/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cache.internal;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;

/**
 * A mapper from rule class names to rule instances for cached rules.
 */
class CachedRuleMapper {

    private final Map<String, Rule> cachedRulesInstances = new HashMap<>();

    /**
     * Finds a rule instance for the given rule class name, name and target language
     * @param className The name of the rule class that generated the cache entry
     * @param ruleName The name of the rule that generated the cache entry
     * @param languageName The terse name of the language for which the rule applies
     * @return The requested rule
     */
    public Rule getRuleForClass(final String className, final String ruleName, final String languageName) {
        return cachedRulesInstances.get(getRuleKey(className, ruleName, languageName));
    }

    /**
     * Initialize the mapper with the given rulesets.
     * @param rs The rulesets from which to retrieve rules.
     */
    public void initialize(final RuleSets rs) {
        for (final Rule r : rs.getAllRules()) {
            cachedRulesInstances.put(getRuleKey(r.getRuleClass(), r.getName(), r.getLanguage().getId()), r);
        }
    }

    private String getRuleKey(final String className, final String ruleName, final String languageName) {
        return className + "$$" + ruleName + "$$" + languageName;
    }
}

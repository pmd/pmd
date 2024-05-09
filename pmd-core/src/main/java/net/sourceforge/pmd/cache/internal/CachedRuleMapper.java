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
     * Finds a rule instance for the given rule key consisting of class name, name and target language
     * 
     * @param ruleKey The Rulekey for the rule
     * @return The requested rule
     */
    public Rule getRuleForClass(final RuleKey ruleKey) {
        return cachedRulesInstances.get(ruleKey.toString());
    }

    /**
     * Initialize the mapper with the given rulesets.
     * @param rs The rulesets from which to retrieve rules.
     */
    public void initialize(final RuleSets rs) {
        for (final Rule r : rs.getAllRules()) {
            cachedRulesInstances.put(getRuleKey(r.getRuleClass(), r.getName(), r.getLanguage().getId()).toString(), r);
        }
    }

    static class RuleKey {
        /**
         * The name of the rule class that generated the cache entry
         */
        private final String className;
        /**
         * The name of the rule that generated the cache entry
         */
        private final String ruleName;
        /**
         * The terse name of the language for which the rule applies
         */
        private final String languageName;

        RuleKey(String className, String ruleName, String languageName) {
            this.className = className;
            this.ruleName = ruleName;
            this.languageName = languageName;
        }

        @Override
        public String toString() {
            return className + "$$" + ruleName + "$$" + languageName;
        }
    }

    private RuleKey getRuleKey(final String className, final String ruleName, final String languageName) {
        return new RuleKey(className, ruleName, languageName);
    }
}

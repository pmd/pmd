/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.benchmark.Benchmark;
import net.sourceforge.pmd.benchmark.Benchmarker;

public final class RulesetsFactoryUtils {

	private static final Logger LOG = Logger.getLogger(RulesetsFactoryUtils.class.getName());

	private RulesetsFactoryUtils() {}

    /**
     * Creates a new rulesets with the given string. The resulting rulesets will contain
     * all referenced rulesets.
     * @param rulesets the string with the rulesets to load
     * @param factory the ruleset factory
     * @return the rulesets
     * @throws IllegalArgumentException if rulesets is empty (means, no rules have been found) or if a
     * ruleset couldn't be found.
     */
    public static RuleSets getRuleSets(String rulesets, RuleSetFactory factory) {
        RuleSets ruleSets = null;
        try {
            factory.setWarnDeprecated(true);
            ruleSets = factory.createRuleSets(rulesets);
            factory.setWarnDeprecated(false);
            printRuleNamesInDebug(ruleSets);
            if (ruleSets.ruleCount() == 0) {
                String msg = "No rules found. Maybe you mispelled a rule name? (" + rulesets + ")";
                LOG.log(Level.SEVERE, msg);
                throw new IllegalArgumentException(msg);
            }
        } catch (RuleSetNotFoundException rsnfe) {
            LOG.log(Level.SEVERE, "Ruleset not found", rsnfe);
            throw new IllegalArgumentException(rsnfe);
        }
        return ruleSets;
    }

    /**
     * See {@link #getRuleSets(String, RuleSetFactory)}. In addition, the loading of the rules
     * is benchmarked.
     * @param rulesets the string with the rulesets to load
     * @param factory the ruleset factory
     * @return the rulesets
     * @throws IllegalArgumentException if rulesets is empty (means, no rules have been found) or if a
     * ruleset couldn't be found.
     */
    public static RuleSets getRuleSetsWithBenchmark(String rulesets, RuleSetFactory factory) {
        long loadRuleStart = System.nanoTime();
        RuleSets ruleSets = null;
        try {
            ruleSets = getRuleSets(rulesets, factory);
        } finally {
            long endLoadRules = System.nanoTime();
            Benchmarker.mark(Benchmark.LoadRules, endLoadRules - loadRuleStart, 0);
        }
        return ruleSets;
    }

	public static RuleSetFactory getRulesetFactory(PMDConfiguration configuration) {
		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		ruleSetFactory.setMinimumPriority(configuration.getMinimumPriority());
		if (!configuration.isRuleSetFactoryCompatibilityEnabled()) {
		    ruleSetFactory.disableCompatibilityFilter();
		}
		return ruleSetFactory;
	}

	/**
	 * If in debug modus, print the names of the rules.
	 *
	 * @param rulesets     the RuleSets to print
	 */
	private static void printRuleNamesInDebug(RuleSets rulesets) {
		if (LOG.isLoggable(Level.FINER)) {
			for (Rule r : rulesets.getAllRules()) {
				LOG.finer("Loaded rule " + r.getName());
			}
		}
	}
}

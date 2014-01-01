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

	public static RuleSets getRuleSets(String rulesets, RuleSetFactory factory, long loadRuleStart) {
		RuleSets ruleSets = null;

		try {
			ruleSets = factory.createRuleSets(rulesets);
			factory.setWarnDeprecated(false);
			printRuleNamesInDebug(ruleSets);
			long endLoadRules = System.nanoTime();
			Benchmarker.mark(Benchmark.LoadRules, endLoadRules - loadRuleStart, 0);
		} catch (RuleSetNotFoundException rsnfe) {
			LOG.log(Level.SEVERE, "Ruleset not found", rsnfe);
			throw new IllegalArgumentException(rsnfe);
		}
		return ruleSets;
	}

	public static RuleSetFactory getRulesetFactory(PMDConfiguration configuration) {
		RuleSetFactory ruleSetFactory = new RuleSetFactory();
		ruleSetFactory.setMinimumPriority(configuration.getMinimumPriority());
		ruleSetFactory.setWarnDeprecated(true);
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

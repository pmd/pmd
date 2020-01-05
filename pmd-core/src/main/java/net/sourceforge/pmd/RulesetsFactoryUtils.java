/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.util.ResourceLoader;

public final class RulesetsFactoryUtils {

    private static final Logger LOG = Logger.getLogger(RulesetsFactoryUtils.class.getName());

    private RulesetsFactoryUtils() {
    }

    /**
     * Creates a new rulesets with the given string. The resulting rulesets will
     * contain all referenced rulesets.
     *
     * @param rulesets
     *            the string with the rulesets to load
     * @param factory
     *            the ruleset factory
     * @return the rulesets
     * @throws IllegalArgumentException
     *             if rulesets is empty (means, no rules have been found) or if
     *             a ruleset couldn't be found.
     */
    public static RuleSets getRuleSets(String rulesets, RuleSetFactory factory) {
        RuleSets ruleSets = null;
        try {
            ruleSets = factory.createRuleSets(rulesets);
            printRuleNamesInDebug(ruleSets);
            if (ruleSets.ruleCount() == 0) {
                String msg = "No rules found. Maybe you mispelled a rule name? (" + rulesets + ')';
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
     * See {@link #getRuleSets(String, RuleSetFactory)}. In addition, the
     * loading of the rules is benchmarked.
     *
     * @param rulesets
     *            the string with the rulesets to load
     * @param factory
     *            the ruleset factory
     * @return the rulesets
     * @throws IllegalArgumentException
     *             if rulesets is empty (means, no rules have been found) or if
     *             a ruleset couldn't be found.
     * @deprecated Is internal API
     */
    @InternalApi
    @Deprecated
    public static RuleSets getRuleSetsWithBenchmark(String rulesets, RuleSetFactory factory) {
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.LOAD_RULES)) {
            return getRuleSets(rulesets, factory);
        }
    }

    /**
     * @deprecated Use {@link #createFactory(PMDConfiguration)} or {@link #createFactory(PMDConfiguration, ClassLoader)}
     */
    @InternalApi
    @Deprecated
    public static RuleSetFactory getRulesetFactory(final PMDConfiguration configuration,
                                                   final ResourceLoader resourceLoader) {
        return new RuleSetFactory(resourceLoader, configuration.getMinimumPriority(), true,
                                  configuration.isRuleSetFactoryCompatibilityEnabled());
    }

    /**
     * Returns a ruleset factory which uses the classloader for PMD
     * classes to resolve resource references.
     *
     * @param configuration PMD configuration, contains info about the
     *                      factory parameters
     *
     * @return A ruleset factory
     *
     * @see #createFactory(PMDConfiguration, ClassLoader)
     */
    public static RuleSetFactory createFactory(final PMDConfiguration configuration) {
        return createFactory(configuration, RulesetsFactoryUtils.class.getClassLoader());
    }

    /**
     * Returns a ruleset factory with default parameters. It doesn't prune
     * rules based on priority, and doesn't warn for deprecations.
     *
     * @return A ruleset factory
     *
     * @see #createFactory(PMDConfiguration, ClassLoader)
     */
    public static RuleSetFactory defaultFactory() {
        return new RuleSetFactory();
    }

    /**
     * Returns a ruleset factory which uses the provided {@link ClassLoader}
     * to resolve resource references. It warns for deprecated rule usages.
     *
     * @param configuration PMD configuration, contains info about the
     *                      factory parameters
     * @param classLoader   Class loader to load resources
     *
     * @return A ruleset factory
     *
     * @see #createFactory(PMDConfiguration)
     */
    public static RuleSetFactory createFactory(final PMDConfiguration configuration, ClassLoader classLoader) {
        return createFactory(classLoader,
                             configuration.getMinimumPriority(),
                             true,
                             configuration.isRuleSetFactoryCompatibilityEnabled());
    }

    /**
     * Returns a ruleset factory which uses the provided {@link ClassLoader}
     * to resolve resource references.
     *
     * @param minimumPriority     Minimum priority for rules to be included
     * @param warnDeprecated      If true, print warnings when deprecated rules are included
     * @param enableCompatibility If true, rule references to moved rules are mapped to their
     *                            new location if they are known
     * @param classLoader         Class loader to load resources
     *
     * @return A ruleset factory
     *
     * @see #createFactory(PMDConfiguration)
     */
    public static RuleSetFactory createFactory(ClassLoader classLoader,
                                               RulePriority minimumPriority,
                                               boolean warnDeprecated,
                                               boolean enableCompatibility) {

        return new RuleSetFactory(new ResourceLoader(classLoader), minimumPriority, warnDeprecated, enableCompatibility);
    }

    /**
     * Returns a ruleset factory which uses the classloader for PMD
     * classes to resolve resource references.
     *
     * @param minimumPriority     Minimum priority for rules to be included
     * @param warnDeprecated      If true, print warnings when deprecated rules are included
     * @param enableCompatibility If true, rule references to moved rules are mapped to their
     *                            new location if they are known
     *
     * @return A ruleset factory
     *
     * @see #createFactory(PMDConfiguration)
     */
    public static RuleSetFactory createFactory(RulePriority minimumPriority,
                                               boolean warnDeprecated,
                                               boolean enableCompatibility) {

        return new RuleSetFactory(new ResourceLoader(), minimumPriority, warnDeprecated, enableCompatibility);
    }

    /**
     * If in debug modus, print the names of the rules.
     *
     * @param rulesets the RuleSets to print
     */
    private static void printRuleNamesInDebug(RuleSets rulesets) {
        if (LOG.isLoggable(Level.FINER)) {
            for (Rule r : rulesets.getAllRules()) {
                LOG.finer("Loaded rule " + r.getName());
            }
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.util.ResourceLoader;

/**
 * Configurable ruleset parser. Note that this replaces the API of {@link RulesetsFactoryUtils}
 * and {@link RuleSetFactory}. This can be configured using a fluent
 * API, see eg {@link #warnDeprecated(boolean)}. To create a list of
 * rulesets, use {@link #parseFromResourceReference(String)}.
 */
public final class RuleSetParser {

    ResourceLoader resourceLoader = new ResourceLoader(RuleSetParser.class.getClassLoader());
    RulePriority minimumPriority = RulePriority.LOW;
    boolean warnDeprecated = true;
    boolean enableCompatibility = true;
    boolean includeDeprecatedRuleReferences = false;

    /**
     * Create a new config with the default values.
     */
    public RuleSetParser() {

    }

    /**
     * Specify that the given classloader should be used to resolve
     * paths to external ruleset references. The default uses PMD's
     * own classpath.
     */
    public RuleSetParser loadResourcesWith(ClassLoader classLoader) {
        this.resourceLoader = new ResourceLoader(classLoader);
        return this;
    }

    // internal
    RuleSetParser loadResourcesWith(ResourceLoader loader) {
        this.resourceLoader = loader;
        return this;
    }

    /**
     * Filter loaded rules to only those that match or are above
     * the given priority. The default is {@link RulePriority#LOW},
     * ie, no filtering occurs.
     * @return This instance, modified
     */
    public RuleSetParser filterAbovePriority(RulePriority minimumPriority) {
        this.minimumPriority = minimumPriority;
        return this;
    }

    /**
     * Log a warning when referencing a deprecated rule.
     * This is enabled by default.
     * @return This instance, modified
     */
    public RuleSetParser warnDeprecated(boolean warn) {
        this.warnDeprecated = warn;
        return this;
    }

    /**
     * Enable translating old rule references to newer ones, if they have
     * been moved or renamed. This is enabled by default, if disabled,
     * unresolved references will not be translated and will produce an
     * error.
     * @return This instance, modified
     */
    public RuleSetParser enableCompatibility(boolean enable) {
        this.enableCompatibility = enable;
        return this;
    }

    /**
     * Follow deprecated rule references. By default this is off,
     * and those references will be ignored (with a warning depending
     * on {@link #enableCompatibility(boolean)}).
     *
     * @return This instance, modified
     */
    public RuleSetParser includeDeprecatedRuleReferences(boolean enable) {
        this.includeDeprecatedRuleReferences = enable;
        return this;
    }

    /**
     * Create a new rule set factory, if you have to (that class is deprecated).
     * That factory will use the configuration that was set using the setters of this.
     */
    @Deprecated
    public RuleSetFactory toFactory() {
        return new RuleSetFactory(this);
    }


    /**
     * Create a RuleSets from a comma separated list of RuleSet reference IDs.
     * This is a convenience method which calls
     * {@link RuleSetReferenceId#parse(String)}, and then calls
     * {@link #createRuleSets(List)}. The currently configured ResourceLoader is
     * used.
     *
     * @param rulesetResourcePaths A comma separated list of RuleSet reference IDs.
     *
     * @return The new RuleSets.
     */
    public List<RuleSet> parseFromResourceReference(String rulesetResourcePaths) throws RuleSetNotFoundException {
        return createRuleSets(RuleSetReferenceId.parse(rulesetResourcePaths));
    }

    private List<RuleSet> createRuleSets(List<RuleSetReferenceId> ruleSetReferenceIds) throws RuleSetNotFoundException {
        List<RuleSet> ruleSets = new ArrayList<>();
        for (RuleSetReferenceId ruleSetReferenceId : ruleSetReferenceIds) {
            RuleSet ruleSet = createRuleSet(ruleSetReferenceId);
            ruleSets.add(ruleSet);
        }
        return ruleSets;
    }


    // package private
    RuleSet createRuleSet(RuleSetReferenceId ruleSetReferenceId) throws RuleSetNotFoundException {
        return new RuleSetFactory(this).createRuleSet(ruleSetReferenceId);
    }


    /**
     * Configure a new ruleset factory builder according to the parameters
     * of the given PMD configuration.
     */
    public static RuleSetParser fromPmdConfig(PMDConfiguration configuration) {
        return new RuleSetParser().filterAbovePriority(configuration.getMinimumPriority())
                                  .enableCompatibility(configuration.isRuleSetFactoryCompatibilityEnabled());
    }
}

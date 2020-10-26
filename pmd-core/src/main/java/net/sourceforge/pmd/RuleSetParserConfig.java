/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.util.ResourceLoader;

/**
 * Configuration of a {@link RuleSetFactory}. This is a fluent builder
 * pattern. Use this instead of the constructors of RuleSetFactory.
 */
public final class RuleSetParserConfig {

    ResourceLoader resourceLoader = new ResourceLoader(RuleSetParserConfig.class.getClassLoader());
    RulePriority minimumPriority = RulePriority.LOW;
    boolean warnDeprecated = true;
    boolean enableCompatibility = true;
    boolean includeDeprecatedRuleReferences = false;

    /**
     * Create a new config with the default values.
     */
    public RuleSetParserConfig() {

    }

    /**
     * Specify that the given classloader should be used to resolve
     * paths to external ruleset references. The default uses PMD's
     * own classpath.
     */
    public RuleSetParserConfig loadResourcesWith(ClassLoader classLoader) {
        this.resourceLoader = new ResourceLoader(classLoader);
        return this;
    }

    // internal
    RuleSetParserConfig loadResourcesWith(ResourceLoader loader) {
        this.resourceLoader = loader;
        return this;
    }

    /**
     * Filter loaded rules to only those that match or are above
     * the given priority. The default is {@link RulePriority#LOW},
     * ie, no filtering occurs.
     */
    public RuleSetParserConfig filterAbovePriority(RulePriority minimumPriority) {
        this.minimumPriority = minimumPriority;
        return this;
    }

    /**
     * Log a warning when referencing a deprecated rule.
     * This is enabled by default.
     */
    public RuleSetParserConfig warnDeprecated(boolean warn) {
        this.warnDeprecated = warn;
        return this;
    }

    /**
     * Enable translating old rule references to newer ones, if they have
     * been moved or renamed. This is enabled by default, if disabled,
     * unresolved references will not be translated and will produce an
     * error.
     */
    public RuleSetParserConfig enableCompatibility(boolean enable) {
        this.enableCompatibility = enable;
        return this;
    }

    /**
     * Follow deprecated rule references. By default this is off,
     * and those references will be ignored (with a warning depending
     * on {@link #enableCompatibility(boolean)}).
     */
    public RuleSetParserConfig includeDeprecatedRuleReferences(boolean enable) {
        this.includeDeprecatedRuleReferences = enable;
        return this;
    }

    public RuleSetFactory createFactory() {
        return new RuleSetFactory(this);
    }

    /**
     * Configure a new ruleset factory builder according to the parameters
     * of the given PMD configuration.
     */
    public static RuleSetParserConfig fromPmdConfig(PMDConfiguration configuration) {
        return new RuleSetParserConfig().filterAbovePriority(configuration.getMinimumPriority())
                                        .enableCompatibility(configuration.isRuleSetFactoryCompatibilityEnabled());
    }
}

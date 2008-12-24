/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

/**
 * This is the basic Rule interface for PMD rules.
 */
//FUTURE Implement Cloneable and clone()
public interface Rule {

    /**
     * The property descriptor to universally suppress violations with messages matching a regular expression.
     */
    StringProperty VIOLATION_SUPPRESS_REGEX_DESCRIPTOR = new StringProperty("violationSuppressRegex",
	    "Suppress violations with messages matching a regular expression", null, Integer.MAX_VALUE - 1);

    /**
     * Name of the property to universally suppress violations on nodes which match a given relative XPath expression.
     */
    StringProperty VIOLATION_SUPPRESS_XPATH_DESCRIPTOR = new StringProperty("violationSuppressXPath",
	    "Suppress violations on nodes which match a given relative XPath expression.", null, Integer.MAX_VALUE - 2);

    /**
     * Get the Language of this Rule.
     */
    Language getLanguage();

    /**
     * Set the Language of this Rule.
     */
    void setLanguage(Language language);

    /**
     * Get the minimum LanguageVersion to which this Rule applies.  If this
     * value is <code>null</code> it indicates there is no minimum bound.
     */
    LanguageVersion getMinimumLanguageVersion();

    /**
     * Set the minimum LanguageVersion to which this Rule applies.
     */
    void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion);

    /**
     * Get the maximum LanguageVersion to which this Rule applies.  If this
     * value is <code>null</code> it indicates there is no maximum bound.
     */
    LanguageVersion getMaximumLanguageVersion();

    /**
     * Set the maximum LanguageVersion to which this Rule applies.
     */
    void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion);

    /**
     * Gets whether this Rule is deprecated.  A deprecated Rule is one which:
     * <ul>
     * <li>is scheduled for removal in a future version of PMD</li>
     * <li>or, has been removed and replaced with a non-functioning place-holder
     * and will be completely removed in a future version of PMD</li>
     * <li>or, has been renamed/moved and the old name will be completely
     * removed in a future version of PMD</li>
     * <ul>
     */
    boolean isDeprecated();

    /**
     * Sets whether this Rule is deprecated.
     */
    void setDeprecated(boolean deprecated);

    /**
     * Get the name of this Rule.
     */
    String getName();

    /**
     * Set the name of this Rule.
     */
    void setName(String name);

    /**
     * Get the version of PMD in which this Rule was added.
     * Return <code>null</code> if not applicable.
     */
    String getSince();

    /**
     * Set the version of PMD in which this Rule was added.
     */
    void setSince(String since);

    /**
     * Get the class of this Rule.
     */
    String getRuleClass();

    /**
     * Set the class of this Rule.
     */
    void setRuleClass(String ruleClass);

    /**
     * Get the name of the RuleSet containing this Rule.
     *
     * @see RuleSet
     */
    String getRuleSetName();

    /**
     * Set the name of the RuleSet containing this Rule.
     *
     * @see RuleSet
     */
    void setRuleSetName(String name);

    /**
     * Get the message to show when this Rule identifies a violation.
     */
    String getMessage();

    /**
     * Set the message to show when this Rule identifies a violation.
     */
    void setMessage(String message);

    /**
     * Get the description of this Rule.
     */
    String getDescription();

    /**
     * Set the description of this Rule.
     */
    void setDescription(String description);

    /**
     * Get the list of examples for this Rule.
     */
    List<String> getExamples();

    /**
     * Add a single example for this Rule.
     */
    void addExample(String example);

    /**
     * Get a URL for external information about this Rule.
     */
    String getExternalInfoUrl();

    /**
     * Set a URL for external information about this Rule.
     */
    void setExternalInfoUrl(String externalInfoUrl);

    /**
     * Get the priority of this Rule.
     */
    RulePriority getPriority();

    /**
     * Set the priority of this Rule.
     */
    void setPriority(RulePriority priority);

    /**
     * Define a new property via a PropertyDescriptor.
     * 
     * @param propertyDescriptor The property descriptor.
     * @throws IllegalArgumentException If there is already a property defined the same name.
     */
    void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) throws IllegalArgumentException;

    /**
     * Get the PropertyDescriptor for the given property name.
     * 
     * @param name The name of the property.
     * @return The PropertyDescriptor for the named property, <code>null</code> if there is no such property defined.
     */
    PropertyDescriptor<?> getPropertyDescriptor(String name);

    /**
     * Get the PropertyDescriptors for all defined properties.  The properties
     * are returned sorted by UI order.
     * 
     * @return The PropertyDescriptors in UI order.
     */
    List<PropertyDescriptor<?>> getPropertyDescriptors();

    /**
     * Get the typed value for the given property.
     * 
     * @param <T> The underlying type of the property descriptor.
     * @param propertyDescriptor The property descriptor.
     * @return The property value.
     */
    <T> T getProperty(PropertyDescriptor<T> propertyDescriptor);

    /**
     * Set the property value specified (will be type-checked)
     * 
     * @param <T> The underlying type of the property descriptor.
     * @param propertyDescriptor The property descriptor.
     * @param value The value to set.
     */
    <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value);

    /**
     * Returns all the current property values for the receiver or an
     * immutable empty map if none are specified.
     */
    Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor();

    /**
     * Returns whether the descriptor is present on the receiver.
     * 
     * @param descriptor
     * @return boolean
     */
    boolean hasDescriptor(PropertyDescriptor<?> descriptor);
    
    /**
     * Returns whether the rule uses the default operating parameters.
     * @return boolean
     */
    boolean usesDefaultValues();
    /**
     * Sets whether this Rule uses Data Flow Analysis.
     */
    // FUTURE Use JavaBean conventions for boolean attributes
    void setUsesDFA();

    /**
     * Gets whether this Rule uses Data Flow Analysis.
     */
    // FUTURE Use JavaBean conventions for boolean attributes
    boolean usesDFA();

    /**
     * Sets whether this Rule uses Type Resolution.
     */
    // FUTURE Use JavaBean conventions for boolean attributes
    void setUsesTypeResolution();

    /**
     * Gets whether this Rule uses Type Resolution.
     */
    // FUTURE Use JavaBean conventions for boolean attributes
    boolean usesTypeResolution();

    /**
     * Gets whether this Rule uses the RuleChain.
     */
    // FUTURE Use JavaBean conventions for boolean attributes
    boolean usesRuleChain();

    /**
     * Gets the collection of AST node names visited by the Rule on the
     * RuleChain.
     */
    List<String> getRuleChainVisits();

    /**
     * Adds an AST node by class to be visited by the Rule on the RuleChain.
     */
    void addRuleChainVisit(Class<? extends Node> nodeClass);

    /**
     * Adds an AST node by name to be visited by the Rule on the RuleChain.
     */
    void addRuleChainVisit(String astNodeName);

    /**
     * Start processing. Called once, before apply() is first called.
     */
    void start(RuleContext ctx);

    /**
     * Apply this rule to the given collection of nodes, using the
     * given context.
     */
    void apply(List<? extends Node> nodes, RuleContext ctx);

    /**
     * End processing. Called once, after apply() is last called.
     */
    void end(RuleContext ctx);
}

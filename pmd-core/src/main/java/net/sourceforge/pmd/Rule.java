/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * This is the basic Rule interface for PMD rules.
 *
 * <p>
 * <strong>Thread safety:</strong> PMD will create one instance of a rule per
 * thread. The instances are not shared across different threads. However, a
 * single rule instance is reused for analyzing multiple files.
 * </p>
 */
public interface Rule extends PropertySource {

    /**
     * The property descriptor to universally suppress violations with messages
     * matching a regular expression.
     */
    // TODO 7.0.0 use PropertyDescriptor<Optional<Pattern>>
    StringProperty VIOLATION_SUPPRESS_REGEX_DESCRIPTOR = new StringProperty("violationSuppressRegex",
            "Suppress violations with messages matching a regular expression", null, Integer.MAX_VALUE - 1);

    /**
     * Name of the property to universally suppress violations on nodes which
     * match a given relative XPath expression.
     */
    // TODO 7.0.0 use PropertyDescriptor<Optional<String>>
    StringProperty VIOLATION_SUPPRESS_XPATH_DESCRIPTOR = new StringProperty("violationSuppressXPath",
            "Suppress violations on nodes which match a given relative XPath expression.", null, Integer.MAX_VALUE - 2);

    /**
     * Get the Language of this Rule.
     *
     * @return the language
     */
    Language getLanguage();

    /**
     * Set the Language of this Rule.
     *
     * @param language
     *            the language
     */
    void setLanguage(Language language);

    /**
     * Get the minimum LanguageVersion to which this Rule applies. If this value
     * is <code>null</code> it indicates there is no minimum bound.
     *
     * @return the minimum language version
     */
    LanguageVersion getMinimumLanguageVersion();

    /**
     * Set the minimum LanguageVersion to which this Rule applies.
     *
     * @param minimumLanguageVersion
     *            the minimum language version
     */
    void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion);

    /**
     * Get the maximum LanguageVersion to which this Rule applies. If this value
     * is <code>null</code> it indicates there is no maximum bound.
     *
     * @return the maximum language version
     */
    LanguageVersion getMaximumLanguageVersion();

    /**
     * Set the maximum LanguageVersion to which this Rule applies.
     *
     * @param maximumLanguageVersion
     *            the maximum language version
     */
    void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion);

    /**
     * Gets whether this Rule is deprecated. A deprecated Rule is one which:
     * <ul>
     * <li>is scheduled for removal in a future version of PMD</li>
     * <li>or, has been removed and replaced with a non-functioning place-holder
     * and will be completely removed in a future version of PMD</li>
     * <li>or, has been renamed/moved and the old name will be completely
     * removed in a future version of PMD</li>
     * </ul>
     *
     * @return <code>true</code> if this rule is deprecated
     */
    boolean isDeprecated();

    /**
     * Sets whether this Rule is deprecated.
     *
     * @param deprecated
     *            whether this rule is deprecated
     */
    void setDeprecated(boolean deprecated);

    /**
     * Get the name of this Rule.
     *
     * @return the name
     */
    @Override
    String getName();

    /**
     * Set the name of this Rule.
     *
     * @param name
     *            the name
     */
    void setName(String name);

    /**
     * Get the version of PMD in which this Rule was added. Return
     * <code>null</code> if not applicable.
     *
     * @return version of PMD since when this rule was added
     */
    String getSince();

    /**
     * Set the version of PMD in which this Rule was added.
     *
     * @param since
     *            the version of PMD since when this rule was added
     */
    void setSince(String since);

    /**
     * Get the implementation class of this Rule.
     *
     * @return the implementation class name of this rule.
     */
    String getRuleClass();

    /**
     * Set the class of this Rule.
     *
     * @param ruleClass
     *            the class name of this rule.
     */
    void setRuleClass(String ruleClass);

    /**
     * Get the name of the RuleSet containing this Rule.
     *
     * @return the name of th ruleset containing this rule.
     * @see RuleSet
     */
    String getRuleSetName();

    /**
     * Set the name of the RuleSet containing this Rule.
     *
     * @param name
     *            the name of the ruleset containing this rule.
     * @see RuleSet
     */
    void setRuleSetName(String name);

    /**
     * Get the message to show when this Rule identifies a violation.
     *
     * @return the message to show for a violation.
     */
    String getMessage();

    /**
     * Set the message to show when this Rule identifies a violation.
     *
     * @param message
     *            the message to show for a violation.
     */
    void setMessage(String message);

    /**
     * Get the description of this Rule.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Set the description of this Rule.
     *
     * @param description
     *            the description
     */
    void setDescription(String description);

    /**
     * Get the list of examples for this Rule.
     *
     * @return the list of examples for this rule.
     */
    List<String> getExamples();

    /**
     * Add a single example for this Rule.
     *
     * @param example
     *            a single example to add
     */
    void addExample(String example);

    /**
     * Get a URL for external information about this Rule.
     *
     * @return the URL for external information about this rule.
     */
    String getExternalInfoUrl();

    /**
     * Set a URL for external information about this Rule.
     *
     * @param externalInfoUrl
     *            the URL for external information about this rule.
     */
    void setExternalInfoUrl(String externalInfoUrl);

    /**
     * Get the priority of this Rule.
     *
     * @return the priority
     */
    RulePriority getPriority();

    /**
     * Set the priority of this Rule.
     *
     * @param priority
     *            the priority
     */
    void setPriority(RulePriority priority);


    /**
     * Returns the object that selects the nodes to which this rule applies.
     * The selected nodes will be handed to {@link #apply(Node, RuleContext)}.
     */
    RuleTargetSelector getTargetSelector();

    /**
     * Initialize the rule using the language processor if needed.
     *
     * @param languageProcessor The processor for the rule's language
     */
    default void initialize(LanguageProcessor languageProcessor) {
         // by default do nothing
    }

    /**
     * Start processing. Called once per file, before apply() is first called.
     *
     * @param ctx the rule context
     */
    void start(RuleContext ctx);


    /**
     * Process the given node. The nodes that are fed to this method
     * are the nodes selected by {@link #getTargetSelector()}.
     *
     * @param target Node on which to apply the rule
     * @param ctx    Rule context, handling violations
     */
    void apply(Node target, RuleContext ctx);

    /**
     * End processing. Called once per file, after apply() is last called.
     *
     * @param ctx
     *            the rule context
     */
    void end(RuleContext ctx);

    /**
     * Creates a new copy of this rule.
     * @return A new exact copy of this rule
     */
    Rule deepCopy();


}

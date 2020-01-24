/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
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
    // TODO 7.0.0 use PropertyDescriptor<Pattern>
    StringProperty VIOLATION_SUPPRESS_REGEX_DESCRIPTOR = new StringProperty("violationSuppressRegex",
            "Suppress violations with messages matching a regular expression", null, Integer.MAX_VALUE - 1);

    /**
     * Name of the property to universally suppress violations on nodes which
     * match a given relative XPath expression.
     */
    // TODO 7.0.0 use PropertyDescriptor<String>
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
     * Get the parser options for this Rule. Parser options are used to
     * configure the {@link net.sourceforge.pmd.lang.Parser} to create an AST in
     * the form the Rule is expecting. Because ParserOptions are mutable, a Rule
     * should return a new instance on each call.
     *
     * @return the parser options
     */
    ParserOptions getParserOptions();

    /**
     * Sets whether this Rule uses Data Flow Analysis.
     * @deprecated See {@link #isDfa()}
     */
    @Deprecated // To be removed in PMD 7.0.0
    void setUsesDFA();

    /**
     * Sets whether this Rule uses Data Flow Analysis.
     * @deprecated See {@link #isDfa()}
     */
    @Deprecated
    void setDfa(boolean isDfa);

    /**
     * Gets whether this Rule uses Data Flow Analysis.
     *
     * @return <code>true</code> if Data Flow Analysis is used.
     * @deprecated See {@link #isDfa()}
     */
    @Deprecated // To be removed in PMD 7.0.0
    boolean usesDFA();

    /**
     * Gets whether this Rule uses Data Flow Analysis.
     *
     * @return <code>true</code> if Data Flow Analysis is used.
     * @deprecated Optional AST processing stages will be reified in 7.0.0 to factorise common logic.
     *             This method and the similar methods will be removed.
     */
    @Deprecated
    boolean isDfa();

    /**
     * Sets whether this Rule uses Type Resolution.
     * @deprecated See {@link #isTypeResolution()}
     */
    @Deprecated // To be removed in PMD 7.0.0
    void setUsesTypeResolution();

    /**
     * Sets whether this Rule uses Type Resolution.
     * @deprecated See {@link #isTypeResolution()}
     */
    @Deprecated
    void setTypeResolution(boolean usingTypeResolution);

    /**
     * Gets whether this Rule uses Type Resolution.
     *
     * @return <code>true</code> if Type Resolution is used.
     *
     * @deprecated See {@link #isTypeResolution()}
     */
    @Deprecated // To be removed in PMD 7.0.0
    boolean usesTypeResolution();

    /**
     * Gets whether this Rule uses Type Resolution.
     *
     * @return <code>true</code> if Type Resolution is used.
     * @deprecated Optional AST processing stages will be reified in 7.0.0 to factorise common logic.
     *             This method and the similar methods will be removed.
     */
    @Deprecated
    boolean isTypeResolution();

    /**
     * Sets whether this Rule uses multi-file analysis.
     * @deprecated See {@link #isMultifile()}
     */
    @Deprecated // To be removed in PMD 7.0.0
    void setUsesMultifile();

    /**
     * Sets whether this Rule uses multi-file analysis.
     * @deprecated See {@link #isMultifile()}
     */
    @Deprecated
    void setMultifile(boolean multifile);

    /**
     * Gets whether this Rule uses multi-file analysis.
     *
     * @return <code>true</code> if the multi file analysis is used.
     *
     * @deprecated See {@link #isMultifile()}
     */
    @Deprecated // To be removed in PMD 7.0.0
    boolean usesMultifile();

    /**
     * Gets whether this Rule uses multi-file analysis.
     *
     * @return <code>true</code> if the multi file analysis is used.
     * @deprecated Logic for multifile analysis is not implemented yet and probably
     *             won't be implemented this way. Will be removed in 7.0.0.
     */
    @Deprecated
    boolean isMultifile();

    /**
     * Gets whether this Rule uses the RuleChain.
     *
     * @return <code>true</code> if RuleChain is used.
     *
     * @deprecated USe {@link #isRuleChain()} instead.
     */
    @Deprecated // To be removed in PMD 7.0.0
    boolean usesRuleChain();

    /**
     * Gets whether this Rule uses the RuleChain.
     *
     * @return <code>true</code> if RuleChain is used.
     */
    boolean isRuleChain();

    /**
     * Gets the collection of AST node names visited by the Rule on the
     * RuleChain.
     *
     * @return the list of AST node names
     */
    List<String> getRuleChainVisits();

    /**
     * Adds an AST node by class to be visited by the Rule on the RuleChain.
     *
     * @param nodeClass
     *            the AST node to add to the RuleChain visit list
     */
    void addRuleChainVisit(Class<? extends Node> nodeClass);

    /**
     * Adds an AST node by name to be visited by the Rule on the RuleChain.
     *
     * @param astNodeName
     *            the AST node to add to the RuleChain visit list as string
     */
    void addRuleChainVisit(String astNodeName);

    /**
     * Start processing. Called once, before apply() is first called.
     *
     * @param ctx
     *            the rule context
     */
    void start(RuleContext ctx);

    /**
     * Apply this rule to the given collection of nodes, using the given
     * context.
     *
     * @param nodes
     *            the nodes
     * @param ctx
     *            the rule context
     */
    void apply(List<? extends Node> nodes, RuleContext ctx);

    /**
     * End processing. Called once, after apply() is last called.
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

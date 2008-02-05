/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.util.List;
import java.util.Properties;

/**
 * This is the basic Rule interface for PMD rules.
 */
public interface Rule {
	// FUTURE Use enum
	public static final int LOWEST_PRIORITY = 5;

	// FUTURE Use enum
	public static final String[] PRIORITIES = { "High", "Medium High",
			"Medium", "Medium Low", "Low" };

	/**
	 * Get the name of this Rule.
	 */
	String getName();

	/**
	 * Set the name of this Rule.
	 */
	void setName(String name);

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
	 * Still used by the JDeveloper plugin
	 * 
	 * @deprecated use getExamples(), since we now support multiple examples
	 */
	String getExample();

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
	int getPriority();

	/**
	 * Set the priority of this Rule.
	 */
	void setPriority(int priority);

	/**
	 * Get a name for the priority of this Rule.
	 */
	String getPriorityName();

	/**
	 * TODO What is this?
	 * 
	 * @deprecated Don't know what this is for, so deprecating it.
	 */
	boolean include();

	/**
	 * TODO What is this?
	 * 
	 * @deprecated Don't know what this is for, so deprecating it.
	 */
	void setInclude(boolean include);

	/**
	 * Get all properties for this Rule.
	 * 
	 * @return
	 */
	Properties getProperties();

	/**
	 * Add a specific property to this Rule.
	 */
	void addProperty(String name, String property);

	/**
	 * Add a set of properties to this Rule.
	 */
	void addProperties(Properties properties);

	/**
	 * Get whether this Rule has a property of the given name.
	 */
	boolean hasProperty(String name);

	/**
	 * Get the <code>boolean</code> value for the given property.
	 */
	boolean getBooleanProperty(String name);

	/**
	 * Get the <code>int</code> value for the given property.
	 */
	int getIntProperty(String name);

	/**
	 * Get the <code>double</code> value for the given property.
	 */
	double getDoubleProperty(String name);

	/**
	 * Get the <code>java.util.String</code> value for the given property.
	 */
	String getStringProperty(String name);

	/**
	 * Get the PropertyDescriptor for the given property.
	 */
	// FUTURE Rename to getPropertyDescriptor(String)
	PropertyDescriptor propertyDescriptorFor(String name);

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
	 * Adds an AST node name to be visited by the Rule on the RuleChain.
	 */
	void addRuleChainVisit(String astNodeName);

	/**
	 * Apply this rule to the given collection of compilation units, using the
	 * given context.
	 */
	void apply(List<?> astCompilationUnits, RuleContext ctx);
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Basic abstract implementation of all parser-independent methods of the Rule
 * interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
// FUTURE Implement Cloneable and clone()?
public abstract class AbstractRule implements Rule {
    private Language language;
    private LanguageVersion minimumLanguageVersion;
    private LanguageVersion maximumLanguageVersion;
    private boolean deprecated;
    private String name = getClass().getName();
    private String since;
    private String ruleClass = getClass().getName();
    private String ruleSetName;
    private String message;
    private String description;
    private List<String> examples = new ArrayList<String>();
    private String externalInfoUrl;
    private RulePriority priority = RulePriority.LOW;
    private List<PropertyDescriptor<?>> propertyDescriptors = new ArrayList<PropertyDescriptor<?>>();
    // Map of explicitly set property values.
    private Map<PropertyDescriptor<?>, Object> propertyValuesByDescriptor = new HashMap<PropertyDescriptor<?>, Object>();
    private boolean usesDFA;
    private boolean usesTypeResolution;
    private List<String> ruleChainVisits = new ArrayList<String>();
    
    public AbstractRule() {
	definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
	definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
    }

    /**
     * @see Rule#getLanguage()
     */
    public Language getLanguage() {
	return language;
    }

    /**
     * @see Rule#setLanguage(Language)
     */
    public void setLanguage(Language language) {
	if (this.language != null && this instanceof ImmutableLanguage && !this.language.equals(language)) {
	    throw new UnsupportedOperationException("The Language for Rule class " + this.getClass().getName()
		    + " is immutable and cannot be changed.");
	}
	this.language = language;
    }

    /**
     * @see Rule#getMinimumLanguageVersion()
     */
    public LanguageVersion getMinimumLanguageVersion() {
	return minimumLanguageVersion;
    }

    /**
     * @see Rule#setMinimumLanguageVersion(LanguageVersion)
     */
    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
	this.minimumLanguageVersion = minimumLanguageVersion;
    }

    /**
     * @see Rule#getMaximumLanguageVersion()
     */
    public LanguageVersion getMaximumLanguageVersion() {
	return maximumLanguageVersion;
    }

    /**
     * @see Rule#setMaximumLanguageVersion(LanguageVersion)
     */
    public void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion) {
	this.maximumLanguageVersion = maximumLanguageVersion;
    }

    /**
     * @see Rule#isDeprecated()
     */
    public boolean isDeprecated() {
	return deprecated;
    }

    /**
     * @see Rule#setDeprecated(boolean)
     */
    public void setDeprecated(boolean deprecated) {
	this.deprecated = deprecated;
    }

    /**
     * @see Rule#getName()
     */
    public String getName() {
	return name;
    }

    /**
     * @see Rule#setName(String)
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * @see Rule#getSince()
     */
    public String getSince() {
	return since;
    }

    /**
     * @see Rule#setSince(String)
     */
    public void setSince(String since) {
	this.since = since;
    }

    /**
     * @see Rule#getRuleClass()
     */
    public String getRuleClass() {
	return ruleClass;
    }

    /**
     * @see Rule#setRuleClass(String)
     */
    public void setRuleClass(String ruleClass) {
	this.ruleClass = ruleClass;
    }

    /**
     * @see Rule#getRuleSetName()
     */
    public String getRuleSetName() {
	return ruleSetName;
    }

    /**
     * @see Rule#setRuleSetName(String)
     */
    public void setRuleSetName(String ruleSetName) {
	this.ruleSetName = ruleSetName;
    }

    /**
     * @see Rule#getMessage()
     */
    public String getMessage() {
	return message;
    }

    /**
     * @see Rule#setMessage(String)
     */
    public void setMessage(String message) {
	this.message = message;
    }

    /**
     * @see Rule#getDescription()
     */
    public String getDescription() {
	return description;
    }

    /**
     * @see Rule#setDescription(String)
     */
    public void setDescription(String description) {
	this.description = description;
    }

    /**
     * @see Rule#getExamples()
     */
    public List<String> getExamples() {
	// TODO Needs to be externally immutable
	return examples;
    }

    /**
     * @see Rule#addExample(String)
     */
    public void addExample(String example) {
	examples.add(example);
    }

    /**
     * @see Rule#getExternalInfoUrl()
     */
    public String getExternalInfoUrl() {
	return externalInfoUrl;
    }

    /**
     * @see Rule#setExternalInfoUrl(String)
     */
    public void setExternalInfoUrl(String externalInfoUrl) {
	this.externalInfoUrl = externalInfoUrl;
    }

    /**
     * @see Rule#getPriority()
     */
    public RulePriority getPriority() {
	return priority;
    }

    /**
     * @see Rule#setPriority(RulePriority)
     */
    public void setPriority(RulePriority priority) {
	this.priority = priority;
    }

    /**
     * @see Rule#definePropertyDescriptor(PropertyDescriptor)
     */
    public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
	// Check to ensure the property does not already exist.
	for (PropertyDescriptor<?> descriptor : propertyDescriptors) {
	    if (descriptor.name().equals(propertyDescriptor.name())) {
		throw new IllegalArgumentException("There is already a PropertyDescriptor with name '"
			+ propertyDescriptor.name() + "' defined on Rule " + this.getName() + ".");
	    }
	}
	propertyDescriptors.add(propertyDescriptor);
	// Sort in UI order
	Collections.sort(propertyDescriptors);
    }

    /**
     * @see Rule#getPropertyDescriptor(String)
     */
    public PropertyDescriptor<?> getPropertyDescriptor(String name) {
	for (PropertyDescriptor<?> propertyDescriptor : propertyDescriptors) {
	    if (name.equals(propertyDescriptor.name())) {
		return propertyDescriptor;
	    }
	}
	return null;
    }

    /**
     * @see Rule#hasDescriptor(PropertyDescriptor)
     */
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
    	
    	if (propertyValuesByDescriptor.isEmpty()) {
    		getPropertiesByPropertyDescriptor();	// compute it
    	}
    	
    	return propertyValuesByDescriptor.containsKey(descriptor);
    }
    
    /**
     * @see Rule#getPropertyDescriptors()
     */
    public List<PropertyDescriptor<?>> getPropertyDescriptors() {
	return propertyDescriptors;
    }

    /**
     * @see Rule#getProperty(PropertyDescriptor)
     */
    @SuppressWarnings("unchecked")
    public <T> T getProperty(PropertyDescriptor<T> propertyDescriptor) {
	checkValidPropertyDescriptor(propertyDescriptor);
	T value;
	if (propertyValuesByDescriptor.containsKey(propertyDescriptor)) {
	    value = (T) propertyValuesByDescriptor.get(propertyDescriptor);
	} else {
	    value = propertyDescriptor.defaultValue();
	}
	return value;
    }

    /**
     * @see Rule#setProperty(PropertyDescriptor, Object)
     */
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
	checkValidPropertyDescriptor(propertyDescriptor);
	propertyValuesByDescriptor.put(propertyDescriptor, value);
    }

    private void checkValidPropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) {
	if (!propertyDescriptors.contains(propertyDescriptor)) {
	    throw new IllegalArgumentException("Property descriptor not defined for Rule " + this.getName() + ": " + propertyDescriptor);
	}
    }

    /**
     * @see Rule#getPropertiesByPropertyDescriptor()
     */
    public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
	if (propertyDescriptors.isEmpty()) {
	    return Collections.emptyMap();
	}

	Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor = new HashMap<PropertyDescriptor<?>, Object>(
		propertyDescriptors.size());
	// Fill with existing explicitly values
	propertiesByPropertyDescriptor.putAll(this.propertyValuesByDescriptor);

	// Add default values for anything not yet set
	for (PropertyDescriptor<?> propertyDescriptor : this.propertyDescriptors) {
	    if (!propertiesByPropertyDescriptor.containsKey(propertyDescriptor)) {
		propertiesByPropertyDescriptor.put(propertyDescriptor, propertyDescriptor.defaultValue());
	    }
	}

	return propertiesByPropertyDescriptor;
    }

    /**
     * @see Rule#setUsesDFA()
     */
    public void setUsesDFA() {
	this.usesDFA = true;
    }

    /**
     * @see Rule#usesDFA()
     */
    public boolean usesDFA() {
	return this.usesDFA;
    }

    /**
     * @see Rule#setUsesTypeResolution()
     */
    public void setUsesTypeResolution() {
	this.usesTypeResolution = true;
    }

    /**
     * @see Rule#usesTypeResolution()
     */
    public boolean usesTypeResolution() {
	return this.usesTypeResolution;
    }

    /**
     * @see Rule#usesRuleChain()
     */
    public boolean usesRuleChain() {
	return !getRuleChainVisits().isEmpty();
    }

    /**
     * @see Rule#getRuleChainVisits()
     */
    public List<String> getRuleChainVisits() {
	return ruleChainVisits;
    }

    /**
     * @see Rule#addRuleChainVisit(Class)
     */
    public void addRuleChainVisit(Class<? extends Node> nodeClass) {
	if (!nodeClass.getSimpleName().startsWith("AST")) {
	    throw new IllegalArgumentException("Node class does not start with 'AST' prefix: " + nodeClass);
	}
	addRuleChainVisit(nodeClass.getSimpleName().substring("AST".length()));
    }

    /**
     * @see Rule#addRuleChainVisit(String)
     */
    public void addRuleChainVisit(String astNodeName) {
	if (!ruleChainVisits.contains(astNodeName)) {
	    ruleChainVisits.add(astNodeName);
	}
    }

    /**
     * @see Rule#start(RuleContext)
     */
    public void start(RuleContext ctx) {
	// Override as needed
    }

    /**
     * @see Rule#end(RuleContext)
     */
    public void end(RuleContext ctx) {
	// Override as needed
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node)
     */
    public void addViolation(Object data, Node node) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String)
     */
    public void addViolation(Object data, Node node, String arg) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node, arg);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, Object[])
     */
    public void addViolation(Object data, Node node, Object[] args) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node, args);
    }

    /**
     * @see RuleViolationFactory#addViolationWithMessage(RuleContext, Rule, Node, String)
     */
    public void addViolationWithMessage(Object data, Node node, String message) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolationWithMessage(
		ruleContext, this, node, message);
    }

    /**
     * @see RuleViolationFactory#addViolationWithMessage(RuleContext, Rule, Node, String, Object[])
     */
    public void addViolationWithMessage(Object data, Node node, String message, Object[] args) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolationWithMessage(
		ruleContext, this, node, message, args);
    }

    /**
     * Rules are equal if:
     * <ol>
     * <li>They have the same implementation class.</li>
     * <li>They have the same name.</li>
     * <li>They have the same priority.</li>
     * <li>They share the same properties.</li>
     * </ol>
     */
    @Override
    public boolean equals(Object o) {
	if (o == null) {
	    return false; // trivial
	}

	if (this == o) {
	    return true; // trivial
	}

	boolean equality = this.getClass().getName().equals(o.getClass().getName());

	if (equality) {
	    Rule that = (Rule) o;
	    equality = this.getName().equals(that.getName()) && this.getPriority().equals(that.getPriority())
		    && this.getPropertiesByPropertyDescriptor().equals(that.getPropertiesByPropertyDescriptor());
	}

	return equality;
    }

    /**
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
	Object propertyValues = this.getPropertiesByPropertyDescriptor();
	return this.getClass().getName().hashCode() + (this.getName() != null ? this.getName().hashCode() : 0)
		+ this.getPriority().hashCode() + (propertyValues != null ? propertyValues.hashCode() : 0);
    }
}

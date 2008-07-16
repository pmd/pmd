/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
// FUTURE Move PropertyDescriptor APIs up to Rule interface
// FUTURE Implement Cloneable and clone()
public abstract class AbstractRule implements Rule {

    // TODO Remove - Temporary flag during conversion.
    private static final boolean IN_OLD_PROPERTY_MODE = true;

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
    private Properties properties = new Properties();
    private boolean usesDFA;
    private boolean usesTypeResolution;
    private List<String> ruleChainVisits = new ArrayList<String>();

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
	if (this.language != null && this instanceof ImmutableLanguage && !this.language.equals(language)) {
	    throw new UnsupportedOperationException("The Language for Rule class " + this.getClass().getName()
		    + " is immutable and cannot be changed.");
	}
        this.language = language;
    }

    public LanguageVersion getMinimumLanguageVersion() {
        return minimumLanguageVersion;
    }

    public void setMinimumLanguageVersion(LanguageVersion minimumLanguageVersion) {
        this.minimumLanguageVersion = minimumLanguageVersion;
    }

    public LanguageVersion getMaximumLanguageVersion() {
        return maximumLanguageVersion;
    }

    public void setMaximumLanguageVersion(LanguageVersion maximumLanguageVersion) {
        this.maximumLanguageVersion = maximumLanguageVersion;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getSince() {
	return since;
    }

    public void setSince(String since) {
	this.since = since;
    }

    public String getRuleClass() {
	return ruleClass;
    }

    public void setRuleClass(String ruleClass) {
	this.ruleClass = ruleClass;
    }

    public String getRuleSetName() {
	return ruleSetName;
    }

    public void setRuleSetName(String ruleSetName) {
	this.ruleSetName = ruleSetName;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public List<String> getExamples() {
	// TODO Needs to be externally immutable
	return examples;
    }

    public void addExample(String example) {
	examples.add(example);
    }

    public String getExternalInfoUrl() {
	return externalInfoUrl;
    }

    public void setExternalInfoUrl(String externalInfoUrl) {
	this.externalInfoUrl = externalInfoUrl;
    }

    public RulePriority getPriority() {
	return priority;
    }

    public void setPriority(RulePriority priority) {
	this.priority = priority;
    }

    /**
     * @deprecated - retrieve by name using get<type>Property or get<type>Properties
     */
    @Deprecated
    public Properties getProperties() {
	// TODO Needs to be externally immutable
	return properties;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void addProperty(String name, String value) {
	getProperties().setProperty(name, value);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void addProperties(Properties properties) {
	getProperties().putAll(properties);
    }

    /**
     * @deprecated - property values will be guaranteed available via default
     *             values
     */
    @Deprecated
    public boolean hasProperty(String name) {
	return IN_OLD_PROPERTY_MODE ? // TODO -remove
	getProperties().containsKey(name)
		: propertiesByName().containsKey(name);
    }

    /**
     * @deprecated - use getBooleanProperty(PropertyDescriptor) instead
     */
    @Deprecated
    public boolean getBooleanProperty(String name) {
	return Boolean.parseBoolean(getProperties().getProperty(name));
    }

    public boolean getBooleanProperty(PropertyDescriptor descriptor) {

	return ((Boolean) getProperty(descriptor)).booleanValue();
    }

    // TODO
    public boolean[] getBooleanProperties(PropertyDescriptor descriptor) {
	Boolean[] values = (Boolean[]) getProperties(descriptor);
	boolean[] bools = new boolean[values.length];
	for (int i = 0; i < bools.length; i++) {
	    bools[i] = values[i].booleanValue();
	}
	return bools;
    }

    /**
     * @deprecated - use getIntProperty(PropertyDescriptor) instead
     */
    @Deprecated
    public int getIntProperty(String name) {
	return Integer.parseInt(getProperties().getProperty(name));
    }

    public int getIntProperty(PropertyDescriptor descriptor) {

	return ((Number) getProperty(descriptor)).intValue();
    }

    // TODO
    public int[] getIntProperties(PropertyDescriptor descriptor) {
	Number[] values = (Number[]) getProperties(descriptor);
	int[] ints = new int[values.length];
	for (int i = 0; i < ints.length; i++) {
	    ints[i] = values[i].intValue();
	}
	return ints;
    }

    /**
     * @deprecated - use getDoubleProperty(PropertyDescriptor) instead
     */
    @Deprecated
    public double getDoubleProperty(String name) {
	return Double.parseDouble(getProperties().getProperty(name));
    }

    public double getDoubleProperty(PropertyDescriptor descriptor) {
	return ((Number) getProperty(descriptor)).doubleValue();
    }

    // TODO
    public double[] getDoubleProperties(PropertyDescriptor descriptor) {
	Number[] values = (Number[]) getProperties(descriptor);
	double[] doubles = new double[values.length];
	for (int i = 0; i < doubles.length; i++) {
	    doubles[i] = values[i].doubleValue();
	}
	return doubles;
    }

    /**
     * @deprecated - use getStringProperty(PropertyDescriptor) instead
     */
    @Deprecated
    public String getStringProperty(String name) {
	return getProperties().getProperty(name);
    }

    public String getStringProperty(PropertyDescriptor descriptor) {
	return (String) getProperty(descriptor);
    }

    public String[] getStringProperties(PropertyDescriptor descriptor) {
	return (String[]) getProperties(descriptor);
    }

    public char getCharacterProperty(PropertyDescriptor descriptor) {
	return ((Character) getProperty(descriptor)).charValue();
    }

    public Class<?>[] getTypeProperties(PropertyDescriptor descriptor) {
	return (Class[]) getProperties(descriptor);
    }

    public Class<?> getTypeProperty(PropertyDescriptor descriptor) {
	return (Class<?>) getProperty(descriptor);
    }

    private Object getProperty(PropertyDescriptor descriptor) {
	if (descriptor.maxValueCount() > 1) {
	    propertyGetError(descriptor, true);
	}
	String rawValue = getProperties().getProperty(descriptor.name());
	return rawValue == null || rawValue.length() == 0 ? descriptor.defaultValue() : descriptor.valueFrom(rawValue);
    }

    public void setProperty(PropertyDescriptor descriptor, Object value) {
	if (descriptor.maxValueCount() > 1) {
	    propertySetError(descriptor, true);
	}
	getProperties().setProperty(descriptor.name(), descriptor.asDelimitedString(value));
    }

    private Object[] getProperties(PropertyDescriptor descriptor) {
	if (descriptor.maxValueCount() == 1) {
	    propertyGetError(descriptor, false);
	}
	String rawValue = getProperties().getProperty(descriptor.name());
	return rawValue == null || rawValue.length() == 0 ? (Object[]) descriptor.defaultValue()
		: (Object[]) descriptor.valueFrom(rawValue);
    }

    public void setProperties(PropertyDescriptor descriptor, Object[] values) {
	if (descriptor.maxValueCount() == 1) {
	    propertySetError(descriptor, false);
	}
	getProperties().setProperty(descriptor.name(), descriptor.asDelimitedString(values));
    }

    /**
     * Return all the relevant properties for the receiver by overriding in
     * subclasses as necessary.
     *
     * @return Map
     */
    protected Map<String, PropertyDescriptor> propertiesByName() {
	return Collections.emptyMap();
    }

    public PropertyDescriptor propertyDescriptorFor(String name) {
	PropertyDescriptor descriptor = propertiesByName().get(name);
	if (descriptor == null) {
	    throw new IllegalArgumentException("Unknown property: " + name);
	}
	return descriptor;
    }

    private void propertyGetError(PropertyDescriptor descriptor, boolean requestedSingleValue) {

	if (requestedSingleValue) {
	    throw new RuntimeException("Cannot retrieve a single value from a multi-value property field");
	}
	throw new RuntimeException("Cannot retrieve multiple values from a single-value property field");
    }

    private void propertySetError(PropertyDescriptor descriptor, boolean setSingleValue) {

	if (setSingleValue) {
	    throw new RuntimeException("Cannot set a single value within a multi-value property field");
	}
	throw new RuntimeException("Cannot set multiple values within a single-value property field");
    }

    public void setUsesDFA() {
	this.usesDFA = true;
    }

    public boolean usesDFA() {
	return this.usesDFA;
    }

    public void setUsesTypeResolution() {
	this.usesTypeResolution = true;
    }

    public boolean usesTypeResolution() {
	return this.usesTypeResolution;
    }

    public boolean usesRuleChain() {
	return !getRuleChainVisits().isEmpty();
    }

    public List<String> getRuleChainVisits() {
	return ruleChainVisits;
    }

    public void addRuleChainVisit(Class<? extends Node> nodeClass) {
	if (!nodeClass.getSimpleName().startsWith("AST")) {
	    throw new IllegalArgumentException("Node class does not start with 'AST' prefix: " + nodeClass);
	}
	addRuleChainVisit(nodeClass.getSimpleName().substring("AST".length()));
    }

    public void addRuleChainVisit(String astNodeName) {
	if (!ruleChainVisits.contains(astNodeName)) {
	    ruleChainVisits.add(astNodeName);
	}
    }

    public void start(RuleContext ctx) {
	// Override as needed
    }

    public void end(RuleContext ctx) {
	// Override as needed
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node)
     */
    public final void addViolation(Object data, Node node) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String)
     */
    public final void addViolation(Object data, Node node, String arg) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node, arg);
    }

    /**
     * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, Object[])
     */
    public final void addViolation(Object data, Node node, Object[] args) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
		ruleContext, this, node, args);
    }

    /**
     * @see RuleViolationFactory#addViolationWithMessage(RuleContext, Rule, Node, String)
     */
    public final void addViolationWithMessage(Object data, Node node, String message) {
	RuleContext ruleContext = (RuleContext) data;
	ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolationWithMessage(
		ruleContext, this, node, message);
    }

    /**
     * @see RuleViolationFactory#addViolationWithMessage(RuleContext, Rule, Node, String, Object[])
     */
    public final void addViolationWithMessage(Object data, Node node, String message, Object[] args) {
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
		    && this.getProperties().equals(that.getProperties());
	}

	return equality;
    }

    /**
     * @see #equals(Object)
     */
    @Override
    public int hashCode() {
	return this.getClass().getName().hashCode() + (this.getName() != null ? this.getName().hashCode() : 0)
		+ this.getPriority().hashCode() + (this.getProperties() != null ? this.getProperties().hashCode() : 0);
    }

    public static Map<String, PropertyDescriptor> asFixedMap(PropertyDescriptor[] descriptors) {
	Map<String, PropertyDescriptor> descriptorsByName = new HashMap<String, PropertyDescriptor>(descriptors.length);
	for (PropertyDescriptor descriptor : descriptors) {
	    descriptorsByName.put(descriptor.name(), descriptor);
	}
	return Collections.unmodifiableMap(descriptorsByName);
    }

    public static Map<String, PropertyDescriptor> asFixedMap(PropertyDescriptor descriptor) {
	return asFixedMap(new PropertyDescriptor[] { descriptor });
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.AbstractPropertySource;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Basic abstract implementation of all parser-independent methods of the Rule
 * interface.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
// FUTURE Implement Cloneable and clone()?
public abstract class AbstractRule extends AbstractPropertySource implements Rule {

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
	private List<String> examples = new ArrayList<>();
	private String externalInfoUrl;
	private RulePriority priority = RulePriority.LOW;
	private boolean usesDFA;
	private boolean usesTypeResolution;
	private List<String> ruleChainVisits = new ArrayList<>();

	public AbstractRule() {
		definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR);
		definePropertyDescriptor(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR);
	}

	public void deepCopyValuesTo(AbstractRule otherRule) {
		otherRule.language = language;
		otherRule.minimumLanguageVersion = minimumLanguageVersion;
		otherRule.maximumLanguageVersion = maximumLanguageVersion;
		otherRule.deprecated = deprecated;
		otherRule.name = name;
		otherRule.since = since;
		otherRule.ruleClass = ruleClass;
		otherRule.ruleSetName = ruleSetName;
		otherRule.message = message;
		otherRule.description = description;
		otherRule.examples = copyExamples();
		otherRule.externalInfoUrl = externalInfoUrl;
		otherRule.priority = priority;
		otherRule.propertyDescriptors = copyPropertyDescriptors();
		otherRule.propertyValuesByDescriptor = copyPropertyValues();
		otherRule.usesDFA = usesDFA;
		otherRule.usesTypeResolution = usesTypeResolution;
		otherRule.ruleChainVisits = copyRuleChainVisits();
	}

	private List<String> copyExamples() {
		List<String> copy = new ArrayList<>(examples.size());
		copy.addAll(examples);
		return copy;    	
	}

	private List<String> copyRuleChainVisits() {
		List<String> copy = new ArrayList<>(ruleChainVisits.size());
		copy.addAll(ruleChainVisits);
		return copy;
	}

	/**
	 * @see Rule#getLanguage()
	 */
	 public Language getLanguage() {
		return language;
	}

	/**
	 * @see Rule#setLanguage(net.sourceforge.pmd.lang.Language)
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
	  * @see Rule#setMinimumLanguageVersion(net.sourceforge.pmd.lang.LanguageVersion)
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
	  * @see Rule#setMaximumLanguageVersion(net.sourceforge.pmd.lang.LanguageVersion)
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
	  * This implementation returns a new instance of {@link ParserOptions} using default settings.
	  * 
	  * @see Rule#setPriority(RulePriority)
	  */
	 public ParserOptions getParserOptions() {
		 return new ParserOptions();
	 }

	 /**
	  * @see Rule#setUsesDFA()
	  */
	 public void setUsesDFA() {
		 usesDFA = true;
	 }

	 /**
	  * @see Rule#usesDFA()
	  */
	 public boolean usesDFA() {
		 return usesDFA;
	 }

	 /**
	  * @see Rule#setUsesTypeResolution()
	  */
	 public void setUsesTypeResolution() {
		 usesTypeResolution = true;
	 }

	 /**
	  * @see Rule#usesTypeResolution()
	  */
	 public boolean usesTypeResolution() {
		 return usesTypeResolution;
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
	  * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String, Object[])
	  */
	 public void addViolation(Object data, Node node) {
		 RuleContext ruleContext = (RuleContext) data;
		 ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
				 ruleContext, this, node, this.getMessage(), null);
	 }

	 /**
	  * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String, Object[])
	  */
	 public void addViolation(Object data, Node node, String arg) {
		 RuleContext ruleContext = (RuleContext) data;
		 ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
				 ruleContext, this, node, this.getMessage(), new Object[] { arg });
	 }

	 /**
	  * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String, Object[])
	  */
	 public void addViolation(Object data, Node node, Object[] args) {
		 RuleContext ruleContext = (RuleContext) data;
		 ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
				 ruleContext, this, node, this.getMessage(), args);
	 }

	 /**
	  * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String, Object[])
	  */
	 public void addViolationWithMessage(Object data, Node node, String message) {
		 RuleContext ruleContext = (RuleContext) data;
		 ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
				 ruleContext, this, node, message, null);
	 }

	 /**
	  * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String, Object[])
	  */
	 public void addViolationWithMessage(Object data, Node node, String message, int beginLine, int endLine) {
		 RuleContext ruleContext = (RuleContext) data;
		 ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
				 ruleContext, this, node, message, beginLine, endLine, null 
				 );
	 }
	 
	 /**
	  * @see RuleViolationFactory#addViolation(RuleContext, Rule, Node, String, Object[])
	  */
	 public void addViolationWithMessage(Object data, Node node, String message, Object[] args) {
		 RuleContext ruleContext = (RuleContext) data;
		 ruleContext.getLanguageVersion().getLanguageVersionHandler().getRuleViolationFactory().addViolation(
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

		 boolean equality = getClass() == o.getClass();

		 if (equality) {
			 Rule that = (Rule) o;
			 equality = getName().equals(that.getName()) && getPriority().equals(that.getPriority())
			 && getPropertiesByPropertyDescriptor().equals(that.getPropertiesByPropertyDescriptor());
		 }

		 return equality;
	 }

	 /**
	  * @see #equals(Object)
	  */
	 @Override
	 public int hashCode() {
		 Object propertyValues = getPropertiesByPropertyDescriptor();
		 return getClass().getName().hashCode() + (getName() != null ? getName().hashCode() : 0)
		 + getPriority().hashCode() + (propertyValues != null ? propertyValues.hashCode() : 0);
	 }
}

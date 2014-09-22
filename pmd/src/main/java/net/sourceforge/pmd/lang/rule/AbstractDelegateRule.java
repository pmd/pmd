/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for Rule implementations which delegate to another Rule instance.
 */
public abstract class AbstractDelegateRule implements Rule {

	private Rule rule;

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public Rule getRule() {
		return rule;
	}

	public Language getLanguage() {
		return rule.getLanguage();
	}

	public void setLanguage(Language language) {
		rule.setLanguage(language);
	}

	public LanguageVersion getMinimumLanguageVersion() {
		return rule.getMinimumLanguageVersion();
	}

	public void setMinimumLanguageVersion(LanguageVersion minimumlanguageVersion) {
		rule.setMinimumLanguageVersion(minimumlanguageVersion);
	}

	public void setMaximumLanguageVersion(LanguageVersion maximumlanguageVersion) {
		rule.setMaximumLanguageVersion(maximumlanguageVersion);
	}

	public LanguageVersion getMaximumLanguageVersion() {
		return rule.getMaximumLanguageVersion();
	}

	public boolean isDeprecated() {
		return rule.isDeprecated();
	}

	/**
	 * @see PropertySource#dysfunctionReason()
	 */
	public String dysfunctionReason() {
		return rule.dysfunctionReason();
	}

	public Set<PropertyDescriptor<?>> ignoredProperties() {
		return rule.ignoredProperties();
	}

	public void setDeprecated(boolean deprecated) {
		rule.setDeprecated(deprecated);
	}

	public String getName() {
		return rule.getName();
	}

	public void setName(String name) {
		rule.setName(name);
	}

	public String getSince() {
		return rule.getSince();
	}

	public void setSince(String since) {
		rule.setSince(since);
	}

	public String getRuleClass() {
		return rule.getRuleClass();
	}

	public void setRuleClass(String ruleClass) {
		rule.setRuleClass(ruleClass);
	}

	public String getRuleSetName() {
		return rule.getRuleSetName();
	}

	public void setRuleSetName(String name) {
		rule.setRuleSetName(name);
	}

	public String getMessage() {
		return rule.getMessage();
	}

	public void setMessage(String message) {
		rule.setMessage(message);
	}

	public String getDescription() {
		return rule.getDescription();
	}

	public void setDescription(String description) {
		rule.setDescription(description);
	}

	public List<String> getExamples() {
		return rule.getExamples();
	}

	public void addExample(String example) {
		rule.addExample(example);
	}

	public String getExternalInfoUrl() {
		return rule.getExternalInfoUrl();
	}

	public void setExternalInfoUrl(String url) {
		rule.setExternalInfoUrl(url);
	}

	public RulePriority getPriority() {
		return rule.getPriority();
	}

	public void setPriority(RulePriority priority) {
		rule.setPriority(priority);
	}

	public ParserOptions getParserOptions() {
	    return rule.getParserOptions();
	}

	public void definePropertyDescriptor(PropertyDescriptor<?> propertyDescriptor) throws IllegalArgumentException {
	    rule.definePropertyDescriptor(propertyDescriptor);
	}

	public PropertyDescriptor<?> getPropertyDescriptor(String name) {
	    return rule.getPropertyDescriptor(name);
	}

	public List<PropertyDescriptor<?>> getPropertyDescriptors() {
	    return rule.getPropertyDescriptors();
	}

	public <T> T getProperty(PropertyDescriptor<T> propertyDescriptor) {
	    return rule.getProperty(propertyDescriptor);
	}

	public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
	    rule.setProperty(propertyDescriptor, value);
	}

	public Map<PropertyDescriptor<?>, Object> getPropertiesByPropertyDescriptor() {
	    return rule.getPropertiesByPropertyDescriptor();
	}

	public void setUsesDFA() {
		 rule.setUsesDFA();
	 }

	 public boolean usesDFA() {
		 return rule.usesDFA();
	 }

	 public void setUsesTypeResolution() {
		 rule.setUsesTypeResolution();
	 }

	 public boolean usesTypeResolution() {
		 return rule.usesTypeResolution();
	 }

	 public boolean usesRuleChain() {
		 return rule.usesRuleChain();
	 }

	 public List<String> getRuleChainVisits() {
		 return rule.getRuleChainVisits();
	 }

	 public void addRuleChainVisit(Class<? extends Node> nodeClass) {
		 rule.addRuleChainVisit(nodeClass);
	 }

	 public void addRuleChainVisit(String astNodeName) {
		 rule.addRuleChainVisit(astNodeName);
	 }

	 public void start(RuleContext ctx) {
		 rule.start(ctx);
	 }

	 public void apply(List<? extends Node> nodes, RuleContext ctx) {
		 rule.apply(nodes, ctx);
	 }

	 public void end(RuleContext ctx) {
		 rule.end(ctx);
	 }

    /**
     * @see Rule#hasDescriptor(PropertyDescriptor)
     */
    public boolean hasDescriptor(PropertyDescriptor<?> descriptor) {
    	return rule.hasDescriptor(descriptor);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.List;
import java.util.Properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for Rule implementations which delegate to another Rule instance.
 */
public class AbstractDelegateRule implements Rule {
    private Rule rule;

    public AbstractDelegateRule() {
    }

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

    public String getExample() {
	return rule.getExample();
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

    public Properties getProperties() {
	return rule.getProperties();
    }

    public void addProperty(String name, String property) {
	rule.addProperty(name, property);
    }

    public void addProperties(Properties properties) {
	rule.addProperties(properties);
    }

    public boolean hasProperty(String name) {
	return rule.hasProperty(name);
    }

    public boolean getBooleanProperty(String name) {
	return rule.getBooleanProperty(name);
    }

    public int getIntProperty(String name) {
	return rule.getIntProperty(name);
    }

    public double getDoubleProperty(String name) {
	return rule.getDoubleProperty(name);
    }

    public String getStringProperty(String name) {
	return rule.getStringProperty(name);
    }

    public PropertyDescriptor propertyDescriptorFor(String name) {
	return rule.propertyDescriptorFor(name);
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
}

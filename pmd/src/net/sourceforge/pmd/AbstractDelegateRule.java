package net.sourceforge.pmd;

import java.util.List;
import java.util.Properties;

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

	public int getPriority() {
		return rule.getPriority();
	}

	public void setPriority(int priority) {
		rule.setPriority(priority);
	}

	public String getPriorityName() {
		return rule.getPriorityName();
	}

	public boolean include() {
		return rule.include();
	}

	public void setInclude(boolean include) {
		rule.setInclude(include);
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

	public void addRuleChainVisit(String astNodeName) {
		rule.addRuleChainVisit(astNodeName);
	}

	public void start(RuleContext ctx) {
		rule.start(ctx);
	}

	public void apply(List astCompilationUnits, RuleContext ctx) {
		rule.apply(astCompilationUnits, ctx);
	}

	public void end(RuleContext ctx) {
		rule.end(ctx);
	}
}

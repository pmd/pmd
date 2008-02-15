package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * This class represents a Rule which is a reference to Rule defined in another
 * RuleSet. All details of the Rule are delegated to the underlying referenced
 * Rule, but those operations which modify overridden aspects of the rule are
 * explicitly tracked.
 */
public class RuleReference extends AbstractDelegateRule {
	private String name;
	private Properties properties;
	private String message;
	private String description;
	private List<String> examples;
	private String externalInfoUrl;
	private Integer priority;
	private RuleSetReference ruleSetReference;

	public String getOverriddenName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		super.setName(name);
	}

	public Properties getOverriddenProperties() {
		return properties;
	}

	@Override
	public void addProperty(String name, String property) {
		if (properties == null) {
			properties = new Properties();
		}
		properties.put(name, property);
		super.addProperty(name, property);
	}

	@Override
	public void addProperties(Properties properties) {
		if (this.properties == null) {
			this.properties = new Properties();
		}
		this.properties.putAll(properties);
		super.addProperties(properties);
	}

	public String getOverriddenMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
		super.setMessage(message);
	}

	public String getOverriddenDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
		super.setDescription(description);
	}

	public List<String> getOverriddenExamples() {
		return examples;
	}

	@Override
	public void addExample(String example) {
		if (examples == null) {
			examples = new ArrayList<String>(1);
		}
		examples.add(example);
		super.addExample(example);
	}

	public String getOverriddenExternalInfoUrl() {
		return externalInfoUrl;
	}

	@Override
	public void setExternalInfoUrl(String externalInfoUrl) {
		this.externalInfoUrl = externalInfoUrl;
		super.setExternalInfoUrl(externalInfoUrl);
	}

	public Integer getOverriddenPriority() {
		return priority;
	}

	@Override
	public void setPriority(int priority) {
		this.priority = priority;
		super.setPriority(priority);
	}

	public RuleSetReference getRuleSetReference() {
		return ruleSetReference;
	}

	public void setRuleSetReference(RuleSetReference ruleSetReference) {
		this.ruleSetReference = ruleSetReference;
	}
}

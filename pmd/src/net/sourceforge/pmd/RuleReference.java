package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sourceforge.pmd.util.StringUtil;

/**
 * This class represents a Rule which is a reference to Rule defined in another
 * RuleSet. All details of the Rule are delegated to the underlying referenced
 * Rule, but those operations which modify overridden aspects of the rule are
 * explicitly tracked.  Modification operations which set a value to the
 * current underlying value do not override.
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
		// Only override if different than current value, or if already overridden.
		if (!isSame(name, super.getName()) || this.name != null) {
			this.name = name;
			super.setName(name);
		}
	}

	public Properties getOverriddenProperties() {
		return properties;
	}

	@Override
	public void addProperty(String name, String property) {
		// Only override if different than current value.
		if (!super.hasProperty(name) || !isSame(property, super.getStringProperty(name))) {
			if (this.properties == null) {
				this.properties = new Properties();
			}
			this.properties.put(name, property);
			super.addProperty(name, property);
		}
	}

	@Override
	public void addProperties(Properties properties) {
		// Attempt override for each
		for (Map.Entry<Object, Object> entry : properties.entrySet()) {
			addProperty((String)entry.getKey(), (String)entry.getValue());
		}
	}

	public String getOverriddenMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		// Only override if different than current value, or if already overridden.
		if (!isSame(message, super.getMessage()) || this.message != null) {
			this.message = message;
			super.setMessage(message);
		}
	}

	public String getOverriddenDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		// Only override if different than current value, or if already overridden.
		if (!isSame(description, super.getDescription()) || this.description != null) {
			this.description = description;
			super.setDescription(description);
		}
	}

	public List<String> getOverriddenExamples() {
		return examples;
	}

	@Override
	public void addExample(String example) {
		// TODO Meaningful override of examples is hard, because they are merely
		// a list of strings.  How does one indicate override of a particular
		// value?  Via index?  Rule.setExample(int, String)?  But the XML format
		// does not provide a means of overriding by index, not unless you took
		// the position in the XML file to indicate corresponding index to
		// override.  But that means you have to override starting from index 0.
		// This would be so much easier if examples had to have names, like
		// properties.
				
		// Only override if different than current values.
		if (!contains(super.getExamples(), example)) {
			if (this.examples == null) {
				this.examples = new ArrayList<String>(1);
			}
			// TODO Fix later. To keep example overrides from being unbounded, we're only going to keep track of the last one.
			this.examples.clear();
			this.examples.add(example);
			super.addExample(example);
		}
	}

	public String getOverriddenExternalInfoUrl() {
		return externalInfoUrl;
	}

	@Override
	public void setExternalInfoUrl(String externalInfoUrl) {
		// Only override if different than current value, or if already overridden.
		if (!isSame(externalInfoUrl, super.getExternalInfoUrl()) || this.externalInfoUrl != null) {
			this.externalInfoUrl = externalInfoUrl;
			super.setExternalInfoUrl(externalInfoUrl);
		}
	}

	public Integer getOverriddenPriority() {
		return priority;
	}

	@Override
	public void setPriority(int priority) {
		// Only override if different than current value, or if already overridden.
		if (priority != super.getPriority() || this.priority != null) {
			this.priority = priority;
			super.setPriority(priority);
		}
	}

	public RuleSetReference getRuleSetReference() {
		return ruleSetReference;
	}

	public void setRuleSetReference(RuleSetReference ruleSetReference) {
		this.ruleSetReference = ruleSetReference;
	}

	private static boolean isSame(String s1, String s2) {
		return StringUtil.isSame(s1, s2, true, false, true);
	}

	private static boolean contains(Collection<String> collection, String s1) {
		for (String s2 : collection) {
			if (isSame(s1, s2)) {
				return true;
			}
		}
		return false;
	}
}

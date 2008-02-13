package net.sourceforge.pmd;

/**
 * This is a Rule implementation which can be used in scenarios where an actual
 * functional Rule is not needed.  For example, during unit testing, or as
 * an editable surrogate used by IDE plugins.
 */
public class MockRule extends AbstractRule {

	public MockRule() {
		super();
	}

	public MockRule(String name, String description, String message, String ruleSetName, int priority) {
		this(name, description, message, ruleSetName);
		setPriority(priority);
	}

	public MockRule(String name, String description, String message, String ruleSetName) {
		super();
		setName(name);
		setDescription(description);
		setMessage(message);
		setRuleSetName(ruleSetName);
	}
}

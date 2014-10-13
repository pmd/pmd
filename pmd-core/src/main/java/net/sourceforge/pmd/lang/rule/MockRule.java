/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * This is a Rule implementation which can be used in scenarios where an actual
 * functional Rule is not needed.  For example, during unit testing, or as
 * an editable surrogate used by IDE plugins.  The Language of this Rule
 * defaults to Java.
 */
public class MockRule extends AbstractRule {

    public MockRule() {
	super();
	setLanguage(LanguageRegistry.getLanguage("Dummy"));
	definePropertyDescriptor(new IntegerProperty("testIntProperty", "testIntProperty", 0, 100, 1, 0));
    }

    public MockRule(String name, String description, String message, String ruleSetName, RulePriority priority) {
	this(name, description, message, ruleSetName);
	setPriority(priority);
    }

    public MockRule(String name, String description, String message, String ruleSetName) {
	this();
	setName(name);
	setDescription(description);
	setMessage(message);
	setRuleSetName(ruleSetName);
    }

    public void apply(List<? extends Node> nodes, RuleContext ctx) {
    }
}

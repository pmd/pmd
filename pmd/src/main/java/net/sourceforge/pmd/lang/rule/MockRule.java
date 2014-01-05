/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This is a Rule implementation which can be used in scenarios where an actual
 * functional Rule is not needed.  For example, during unit testing, or as
 * an editable surrogate used by IDE plugins.  The Language of this Rule
 * defaults to Java.
 */
public class MockRule extends AbstractRule {

    public MockRule() {
	super();
	setLanguage(Language.JAVA);
    }

    public MockRule(String name, String description, String message, String ruleSetName, RulePriority priority) {
	this(name, description, message, ruleSetName);
	setPriority(priority);
    }

    public MockRule(String name, String description, String message, String ruleSetName) {
	super();
	setLanguage(Language.JAVA);
	setName(name);
	setDescription(description);
	setMessage(message);
	setRuleSetName(ruleSetName);
    }

    public void apply(List<? extends Node> nodes, RuleContext ctx) {
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * This is a Rule implementation which can be used in scenarios where an actual
 * functional Rule is not needed. For example, during unit testing, or as an
 * editable surrogate used by IDE plugins. The Language of this Rule defaults to
 * Java.
 */
public class MockRuleWithNoProperties extends AbstractRule {

    public MockRuleWithNoProperties() {
        super();
    }

    public MockRuleWithNoProperties(String name, String description, String message, String ruleSetName, RulePriority priority) {
        this(name, description, message, ruleSetName);
        setPriority(priority);
    }

    public MockRuleWithNoProperties(String name, String description, String message, String ruleSetName) {
        this();
        setName(name);
        setDescription(description);
        setMessage(message);
        setRuleSetName(ruleSetName);
    }

    @Override
    public void apply(Node node, RuleContext ctx) {
        // the mock rule does nothing. Usually you would start here to analyze the AST.
    }
}

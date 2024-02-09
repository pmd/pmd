/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.properties.NumericConstraints.inRange;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * This is a Rule implementation which can be used in scenarios where an actual
 * functional Rule is not needed. For example, during unit testing, or as an
 * editable surrogate used by IDE plugins. The Language of this Rule defaults to
 * Dummy.
 */
public class MockRule extends MockRuleWithNoProperties {

    public static final PropertyDescriptor<Integer> PROP =
        PropertyFactory.intProperty("testIntProperty")
                       .desc("testIntProperty")
                       .require(inRange(1, 100)).defaultValue(1).build();

    public MockRule() {
        super();
        setLanguage(DummyLanguageModule.getInstance());
        definePropertyDescriptor(PROP);
    }

    public MockRule(String name, String description, String message, String ruleSetName, RulePriority priority) {
        super(name, description, message, ruleSetName, priority);
        setLanguage(DummyLanguageModule.getInstance());
        definePropertyDescriptor(PROP);
    }

    public MockRule(String name, String description, String message, String ruleSetName) {
        this(name, description, message, ruleSetName, RulePriority.MEDIUM);
    }

    @Override
    public void apply(Node node, RuleContext ctx) {
        // the mock rule does nothing. Usually you would start here to analyze the AST.
    }
}

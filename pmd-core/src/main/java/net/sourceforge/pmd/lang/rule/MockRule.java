/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * This is a Rule implementation which can be used in scenarios where an actual
 * functional Rule is not needed. For example, during unit testing, or as an
 * editable surrogate used by IDE plugins. The Language of this Rule defaults to
 * Java.
 *
 * @deprecated This is not a supported API. You need the pmd-test module
 *     on your classpath, or pmd-core's test sources. This will be removed
 *     in 7.0.0
 */
@Deprecated
public class MockRule extends AbstractRule {

    public MockRule() {
        super();
        setLanguage(LanguageRegistry.getLanguage("Dummy"));
        definePropertyDescriptor(PropertyFactory.intProperty("testIntProperty").desc("testIntProperty").require(inRange(1, 100)).defaultValue(1).build());
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

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        // the mock rule does nothing. Usually you would start here to analyze the AST.
    }
}

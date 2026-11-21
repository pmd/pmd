/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.dummy;

import static net.sourceforge.pmd.properties.NumericConstraints.inRange;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * A rule for the {@link DummyLanguageModule dummy language}, that does nothing.
 * It has a single integer property {@code testIntProperty}.
 */
public class MockRule extends AbstractRule {

    public static final PropertyDescriptor<Integer> PROP =
        PropertyFactory.intProperty("testIntProperty")
                       .desc("testIntProperty")
                       .require(inRange(1, 100)).defaultValue(1).build();

    public MockRule() {
        setLanguage(DummyLanguageModule.getInstance());
        definePropertyDescriptor(PROP);
    }

    @Override
    public void apply(Node node, RuleContext ctx) {
        // the mock rule does nothing. Usually you would start here to analyze the AST.
    }
}

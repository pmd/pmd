/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Sample rule that detect any node with an image of "Foo". Used for testing.
 */
public class FooRule extends AbstractRule {

    public FooRule() {
        setName("Foo");
        setDescription("Description with Unicode Character U+2013: \u2013 .");
        setLanguage(DummyLanguageModule.getInstance());
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forXPathNames(setOf("dummyNode", "dummyRootNode"));
    }

    @Override
    public String getMessage() {
        return "blah";
    }

    @Override
    public String getRuleSetName() {
        return "RuleSet";
    }

    @Override
    public void apply(Node node, RuleContext ctx) {
        for (int i = 0; i < node.getNumChildren(); i++) {
            apply(node.getChild(i), ctx);
        }
        if ("Foo".equals(node.getImage())) {
            ctx.addViolation(node);
        }
    }
}

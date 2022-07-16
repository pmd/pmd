/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

class XSLTRendererTest {

    @Test
    void testDefaultStylesheet() throws Exception {
        XSLTRenderer renderer = new XSLTRenderer();
        DummyNode node = new DummyRootNode().withFileName("file");
        node.setCoords(1, 1, 1, 2);
        RuleViolation rv = new ParametricRuleViolation<Node>(new FooRule(), node, "violation message");
        String result = ReportTest.render(renderer, it -> it.onRuleViolation(rv));
        assertTrue(result.contains("violation message"));
    }
}

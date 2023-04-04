/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.scala.ast.ASTSource;
import net.sourceforge.pmd.lang.scala.ast.ASTTermApply;
import net.sourceforge.pmd.lang.scala.ast.ASTTermName;
import net.sourceforge.pmd.lang.scala.ast.BaseScalaTest;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;

class ScalaRuleTest extends BaseScalaTest {

    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    @Test
    void testRuleVisits() {
        final AtomicInteger visited = new AtomicInteger();
        ScalaRule rule = new ScalaRule() {

            @Override
            public RuleContext visit(ScalaNode<?> node, RuleContext data) {
                visited.incrementAndGet();
                return super.visit(node, data);
            }
        };
        ASTSource root = scala.parseResource(SCALA_TEST);
        rule.apply(root, null);
        assertEquals(12, visited.get());
    }

    @Test
    void testDummyRule() {
        ScalaRule rule = new ScalaRule() {
            @Override
            public String getMessage() {
                return "a message";
            }

            @Override
            public RuleContext visit(ASTTermApply node, RuleContext data) {
                ASTTermName child = node.getFirstChildOfType(ASTTermName.class);
                if (child != null && child.hasImageEqualTo("println")) {
                    addViolation(data, node);
                }
                return data;
            }
        };
        Report report = scala.executeRuleOnResource(rule, SCALA_TEST);

        assertEquals(1, report.getViolations().size());
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.scala.ast.ASTSource;
import net.sourceforge.pmd.lang.scala.ast.ASTTermApply;
import net.sourceforge.pmd.lang.scala.ast.ASTTermName;
import net.sourceforge.pmd.lang.scala.ast.BaseScalaTest;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;

public class ScalaRuleTest extends BaseScalaTest {

    private static final String SCALA_TEST = "/parserFiles/helloworld.scala";

    @Test
    public void testRuleVisits() {
        final AtomicInteger visited = new AtomicInteger();
        ScalaRule rule = new ScalaRule() {

            @Override
            public RuleContext visit(ScalaNode<?> node, RuleContext data) {
                visited.incrementAndGet();
                return super.visit(node, data);
            }
        };
        ASTSource root = scala.parseResource(SCALA_TEST);
        rule.apply(Collections.singletonList(root), null);
        Assert.assertEquals(12, visited.get());
    }

    @Test
    public void testDummyRule() {
        ScalaRule rule = new ScalaRule() {
            @Override
            public RuleContext visit(ASTTermApply node, RuleContext data) {
                ASTTermName child = node.getFirstChildOfType(ASTTermName.class);
                if (child != null && child.hasImageEqualTo("println")) {
                    addViolation(data, node);
                }
                return data;
            }
        };
        Report report = scala.getReportForResource(rule, SCALA_TEST);

        Assert.assertEquals(1, IteratorUtil.count(report.iterator()));
    }
}

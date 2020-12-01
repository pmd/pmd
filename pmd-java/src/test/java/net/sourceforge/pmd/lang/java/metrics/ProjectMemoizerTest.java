/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.metrics.testdata.MetricsVisitorTestData;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

/**
 * @author Clément Fournier
 */
public class ProjectMemoizerTest extends BaseNonParserTest {

    private final Metric<Node, Integer> randomMetric = randomMetric();

    private static Metric<Node, Integer> randomMetric() {
        Random capturedRandom = new Random();
        return Metric.of((t, opts) -> capturedRandom.nextInt(), t -> t, "randomMetric");
    }

    @Test
    public void memoizationTest() {
        ASTCompilationUnit acu = java.parseClass(MetricsVisitorTestData.class);

        List<Integer> expected = visitWith(acu, true);
        List<Integer> real = visitWith(acu, false);

        assertEquals(expected, real);
    }


    @Test
    public void forceMemoizationTest() {

        ASTCompilationUnit acu = java.parseClass(MetricsVisitorTestData.class);

        List<Integer> reference = visitWith(acu, true);
        List<Integer> real = visitWith(acu, true);

        assertEquals(reference.size(), real.size());

        // we force recomputation so each result should be different
        for (int i = 0; i < reference.size(); i++) {
            assertNotEquals(reference.get(i), real.get(i));
        }
    }


    private List<Integer> visitWith(ASTCompilationUnit acu, final boolean force) {
        final List<Integer> result = new ArrayList<>();

        acu.acceptVisitor(new JavaVisitorBase<Object, Object>() {
            @Override
            public Object visitMethodOrCtor(ASTMethodOrConstructorDeclaration node, Object data) {
                Integer value = MetricsUtil.computeMetric(randomMetric, node, MetricOptions.emptyOptions(), force);
                if (value != null) {
                    result.add(value);
                }
                return super.visitMethodOrCtor(node, data);
            }


            @Override
            public Object visitTypeDecl(ASTAnyTypeDeclaration node, Object data) {
                Integer value = MetricsUtil.computeMetric(randomMetric, node, MetricOptions.emptyOptions(), force);
                if (value != null) {
                    result.add(value);
                }
                return super.visitTypeDecl(node, data);
            }
        }, null);

        return result;
    }


}

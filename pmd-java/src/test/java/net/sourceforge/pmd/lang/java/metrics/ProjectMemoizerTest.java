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

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.AbstractJavaOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.testdata.MetricsVisitorTestData;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKeyUtil;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

/**
 * @author Clément Fournier
 */
public class ProjectMemoizerTest extends BaseNonParserTest {

    private MetricKey<ASTAnyTypeDeclaration> classMetricKey = MetricKeyUtil.of(null, new RandomClassMetric());
    private MetricKey<MethodLikeNode> opMetricKey = MetricKeyUtil.of(null, new RandomOperationMetric());


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

        acu.jjtAccept(new JavaParserVisitorReducedAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                if (opMetricKey.supports(node)) {
                    result.add((int) MetricsUtil.computeMetric(opMetricKey, node, MetricOptions.emptyOptions(), force));
                }
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                if (classMetricKey.supports(node)) {
                    result.add((int) MetricsUtil.computeMetric(classMetricKey, node, MetricOptions.emptyOptions(), force));
                }
                return super.visit(node, data);
            }
        }, null);

        return result;
    }


    private class RandomOperationMetric extends AbstractJavaOperationMetric {

        private Random random = new Random();


        @Override
        public double computeFor(MethodLikeNode node, MetricOptions options) {
            return random.nextInt();
        }
    }

    private class RandomClassMetric extends AbstractJavaClassMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricOptions options) {
            return random.nextInt();
        }
    }

}

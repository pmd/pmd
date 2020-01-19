/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexClassMetric;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexOperationMetric;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKeyUtil;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.metrics.MetricsUtil;

import apex.jorje.semantic.ast.compilation.Compilation;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirrorTest extends ApexParserTestBase {

    private static ApexNode<Compilation> acu;
    private MetricKey<ASTUserClassOrInterface<?>> classMetricKey = MetricKeyUtil.of(null, new RandomClassMetric());
    private MetricKey<ASTMethod> opMetricKey = MetricKeyUtil.of(null, new RandomOperationMetric());

    @Before
    public void setup() {
        acu = parseResource("../multifile/MetadataDeployController.cls");
    }


    @Test
    public void memoizationTest() {


        List<Integer> expected = visitWith(acu, true);
        List<Integer> real = visitWith(acu, false);

        assertEquals(expected, real);
    }


    @Test
    public void forceMemoizationTest() {

        List<Integer> reference = visitWith(acu, true);
        List<Integer> real = visitWith(acu, true);

        assertEquals(reference.size(), real.size());

        // we force recomputation so each result should be different
        for (int i = 0; i < reference.size(); i++) {
            assertNotEquals(reference.get(i), real.get(i));
        }
    }


    private List<Integer> visitWith(ApexNode<Compilation> acu, final boolean force) {
        final List<Integer> result = new ArrayList<>();

        acu.jjtAccept(new ApexParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethod node, Object data) {
                if (opMetricKey.supports(node)) {
                    result.add((int) MetricsUtil.computeMetric(opMetricKey, node, MetricOptions.emptyOptions(), force));
                }
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTUserClass node, Object data) {
                if (classMetricKey.supports(node)) {
                    result.add((int) MetricsUtil.computeMetric(classMetricKey, node, MetricOptions.emptyOptions(), force));
                }
                return super.visit(node, data);
            }
        }, null);

        return result;
    }


    private static class RandomOperationMetric extends AbstractApexOperationMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTMethod node, MetricOptions options) {
            return random.nextInt();
        }
    }

    private static class RandomClassMetric extends AbstractApexClassMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTUserClassOrInterface<?> node, MetricOptions options) {
            return random.nextInt();
        }
    }


}

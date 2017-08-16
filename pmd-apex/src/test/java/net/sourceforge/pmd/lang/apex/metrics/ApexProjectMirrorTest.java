/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import static net.sourceforge.pmd.lang.apex.metrics.ApexMetricsVisitorTest.parseAndVisitForString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassOrInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexClassMetric;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexOperationMetric;
import net.sourceforge.pmd.lang.metrics.Metric.Version;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKeyUtil;
import net.sourceforge.pmd.lang.metrics.MetricMemoizer;
import net.sourceforge.pmd.lang.metrics.MetricVersion;

import apex.jorje.semantic.ast.compilation.Compilation;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirrorTest {

    private static ApexNode<Compilation> acu;
    private MetricKey<ASTUserClassOrInterface<?>> classMetricKey = MetricKeyUtil.of(null, new RandomClassMetric());
    private MetricKey<ASTMethod> opMetricKey = MetricKeyUtil.of(null, new RandomOperationMetric());


    static {
        try {
            acu = parseAndVisitForString(
                IOUtils.toString(ApexMetricsVisitorTest.class.getResourceAsStream("MetadataDeployController.cls")));
        } catch (IOException ioe) {
            // Should definitely not happen
        }
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
        final ApexProjectMemoizer toplevel = ApexMetrics.getFacade().getLanguageSpecificProjectMemoizer();

        final List<Integer> result = new ArrayList<>();

        acu.jjtAccept(new ApexParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethod node, Object data) {
                MetricMemoizer<ASTMethod> op = toplevel.getOperationMemoizer(node.getQualifiedName());
                result.add((int) ApexMetricsComputer.INSTANCE.computeForOperation(opMetricKey, node, force, Version.STANDARD, op));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTUserClass node, Object data) {
                MetricMemoizer<ASTUserClassOrInterface<?>> clazz = toplevel.getClassMemoizer(node.getQualifiedName());
                result.add((int) ApexMetricsComputer.INSTANCE.computeForType(classMetricKey, node, force, Version.STANDARD, clazz));
                return super.visit(node, data);
            }
        }, null);

        return result;
    }


    private class RandomOperationMetric extends AbstractApexOperationMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTMethod node, MetricVersion version) {
            return random.nextInt();
        }
    }

    private class RandomClassMetric extends AbstractApexClassMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTUserClassOrInterface<?> node, MetricVersion version) {
            return random.nextInt();
        }
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static net.sourceforge.pmd.lang.java.metrics.JavaMetricsVisitorTest.parseAndVisitForClass15;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.MetricKeyUtil;
import net.sourceforge.pmd.lang.java.ParserTst;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.metrics.impl.AbstractJavaClassMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.AbstractJavaOperationMetric;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaFieldSignature;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaOperationSignature;
import net.sourceforge.pmd.lang.java.metrics.testdata.MetricsVisitorTestData;
import net.sourceforge.pmd.lang.metrics.Metric.Version;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricMemoizer;
import net.sourceforge.pmd.lang.metrics.MetricVersion;

/**
 * Tests functionality of PackageStats, so both its ProjectMemoizer and JavaSignatureMatcher interfaces.
 *
 * @author Clément Fournier
 */
public class PackageStatsTest extends ParserTst {

    private MetricKey<ASTAnyTypeDeclaration> classMetricKey = MetricKeyUtil.of(new RandomClassMetric(), null);
    private MetricKey<ASTMethodOrConstructorDeclaration> opMetricKey = MetricKeyUtil.of(new RandomOperationMetric(), null);
    private PackageStats pack;


    @Before
    public void setUp() {
        pack = new PackageStats();
    }


    @Test
    public void testAddClass() {
        JavaQualifiedName qname = JavaQualifiedName.parseName("org.foo.Boo");

        assertNull(pack.getClassStats(qname, false));
        assertNotNull(pack.getClassStats(qname, true));

        // now it's added, this shouldn't return null
        assertNotNull(pack.getClassStats(qname, false));
    }


    @Test
    public void testAddOperation() {
        final String TEST = "package org.foo; class Boo{ "
            + "public void foo(){}}";

        ASTMethodOrConstructorDeclaration node = getOrderedNodes(ASTMethodDeclaration.class, TEST).get(0);

        JavaQualifiedName qname = node.getQualifiedName();
        JavaOperationSignature signature = JavaOperationSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, new JavaOperationSigMask()));

        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addOperation("foo()", signature);
        assertTrue(pack.hasMatchingSig(qname, new JavaOperationSigMask()));
    }


    @Test
    public void testAddField() {
        final String TEST = "package org.foo; class Boo{ "
            + "public String bar;}";

        ASTFieldDeclaration node = getOrderedNodes(ASTFieldDeclaration.class, TEST).get(0);

        JavaQualifiedName qname = JavaQualifiedName.parseName("org.foo.Boo");
        String fieldName = "bar";
        JavaFieldSignature signature = JavaFieldSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, fieldName, new JavaFieldSigMask()));

        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addField(fieldName, signature);
        assertTrue(pack.hasMatchingSig(qname, fieldName, new JavaFieldSigMask()));
    }


    @Test
    public void memoizationTest() {
        ASTCompilationUnit acu = parseAndVisitForClass15(MetricsVisitorTestData.class);

        List<Integer> expected = visitWith(acu, true);
        List<Integer> real = visitWith(acu, false);

        assertEquals(expected, real);
    }


    @Test
    public void forceMemoizationTest() {
        ASTCompilationUnit acu = parseAndVisitForClass15(MetricsVisitorTestData.class);

        List<Integer> reference = visitWith(acu, true);
        List<Integer> real = visitWith(acu, true);

        assertEquals(reference.size(), real.size());

        // we force recomputation so each result should be different
        for (int i = 0; i < reference.size(); i++) {
            assertNotEquals(reference.get(i), real.get(i));
        }
    }


    private List<Integer> visitWith(ASTCompilationUnit acu, final boolean force) {
        final PackageStats toplevel = JavaMetrics.getTopLevelPackageStats();

        final List<Integer> result = new ArrayList<>();

        acu.jjtAccept(new JavaParserVisitorReducedAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                MetricMemoizer<ASTMethodOrConstructorDeclaration> op = toplevel.getOperationStats(node.getQualifiedName());
                result.add((int) JavaMetricsComputer.INSTANCE.computeForOperation(opMetricKey, node, force, Version.STANDARD, op));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                MetricMemoizer<ASTAnyTypeDeclaration> clazz = toplevel.getClassStats(node.getQualifiedName());
                result.add((int) JavaMetricsComputer.INSTANCE.computeForType(classMetricKey, node, force, Version.STANDARD, clazz));
                return super.visit(node, data);
            }
        }, null);

        return result;
    }


    private class RandomOperationMetric extends AbstractJavaOperationMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {
            return random.nextInt();
        }
    }

    private class RandomClassMetric extends AbstractJavaClassMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricVersion version) {
            return random.nextInt();
        }
    }
}

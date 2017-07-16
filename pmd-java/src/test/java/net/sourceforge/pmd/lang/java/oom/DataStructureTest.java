/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static net.sourceforge.pmd.lang.java.oom.MetricsVisitorTest.parseAndVisitForClass15;
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

import net.sourceforge.pmd.lang.java.ParserTst;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorReducedAdapter;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.Metric.Version;
import net.sourceforge.pmd.lang.java.oom.api.MetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;
import net.sourceforge.pmd.lang.java.oom.metrics.AbstractClassMetric;
import net.sourceforge.pmd.lang.java.oom.metrics.AbstractOperationMetric;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature;
import net.sourceforge.pmd.lang.java.oom.testdata.MetricsVisitorTestData;

/**
 * Tests functionality of the whole data structure (PackageStats, ClassStats, OperationStats). The behaviour of the
 * structure is very encapsulated, so the API to test is restricted per class.
 *
 * @author Clément Fournier
 */
public class DataStructureTest extends ParserTst {

    MetricKey<ASTAnyTypeDeclaration> classMetricKey = ClassMetricKey.of(new RandomClassMetric(), null);
    MetricKey<ASTMethodOrConstructorDeclaration> opMetricKey = OperationMetricKey.of(new RandomOperationMetric(), null);
    private PackageStats pack;


    @Before
    public void setUp() {
        pack = new PackageStats();
    }


    @Test
    public void testAddClass() {
        QualifiedName qname = QualifiedName.parseName("org.foo.Boo");

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

        QualifiedName qname = node.getQualifiedName();
        OperationSignature signature = OperationSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, new OperationSigMask()));

        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addOperation("foo()", signature);
        assertTrue(pack.hasMatchingSig(qname, new OperationSigMask()));
    }


    @Test
    public void testAddField() {
        final String TEST = "package org.foo; class Boo{ "
            + "public String bar;}";

        ASTFieldDeclaration node = getOrderedNodes(ASTFieldDeclaration.class, TEST).get(0);

        QualifiedName qname = QualifiedName.parseName("org.foo.Boo");
        String fieldName = "bar";
        FieldSignature signature = FieldSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, fieldName, new FieldSigMask()));

        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addField(fieldName, signature);
        assertTrue(pack.hasMatchingSig(qname, fieldName, new FieldSigMask()));
    }


    @Test
    public void memoizationTest() {
        ASTCompilationUnit acu = parseAndVisitForClass15(MetricsVisitorTestData.class);

        List<Integer> expected = visitWith(acu, true);
        List<Integer> real = visitWith(acu, false);

        assertEquals(expected, real);
    }


    private List<Integer> visitWith(ASTCompilationUnit acu, final boolean force) {
        final PackageStats toplevel = Metrics.getTopLevelPackageStats();

        final List<Integer> result = new ArrayList<>();

        acu.jjtAccept(new JavaParserVisitorReducedAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                result.add((int) toplevel.compute(opMetricKey, node, force, Version.STANDARD));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                result.add((int) toplevel.compute(classMetricKey, node, force, Version.STANDARD));
                return super.visit(node, data);
            }
        }, null);

        return result;
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


    private class RandomOperationMetric extends AbstractOperationMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {
            return random.nextInt();
        }
    }

    private class RandomClassMetric extends AbstractClassMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricVersion version) {
            return random.nextInt();
        }


    }


}

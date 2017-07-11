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
import net.sourceforge.pmd.lang.java.oom.api.ClassMetric;
import net.sourceforge.pmd.lang.java.oom.api.Metric.Version;
import net.sourceforge.pmd.lang.java.oom.api.MetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetric;
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

    MetricKey<ClassMetric> classMetricKey = new MetricKey<ClassMetric>() {
        @Override
        public String name() {
            return null;
        }


        @Override
        public ClassMetric getCalculator() {
            return new RandomMetric();
        }
    };
    MetricKey<OperationMetric> opMetricKey = new MetricKey<OperationMetric>() {
        @Override
        public String name() {
            return null;
        }


        @Override
        public OperationMetric getCalculator() {
            return new RandomMetric();
        }
    };
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

        final List<Integer> res = new ArrayList<>();
        final PackageStats toplevel = Metrics.getTopLevelPackageStats();

        ASTCompilationUnit acu = parseAndVisitForClass15(MetricsVisitorTestData.class);

        acu.jjtAccept(new JavaParserVisitorReducedAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                res.add((int) toplevel.compute(opMetricKey, node, true, Version.STANDARD));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                res.add((int) toplevel.compute(classMetricKey, node, true, Version.STANDARD));
                return super.visit(node, data);
            }
        }, null);

        System.out.println();

        final List<Integer> cmp = new ArrayList<>();

        acu.jjtAccept(new JavaParserVisitorReducedAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                cmp.add((int) toplevel.compute(opMetricKey, node, false, Version.STANDARD));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                cmp.add((int) toplevel.compute(classMetricKey, node, false, Version.STANDARD));
                return super.visit(node, data);
            }
        }, null);

        assertEquals(res, cmp);

    }


    @Test
    public void forceMemoizationTest() {

        final List<Integer> res = new ArrayList<>();
        final PackageStats toplevel = Metrics.getTopLevelPackageStats();

        ASTCompilationUnit acu = parseAndVisitForClass15(MetricsVisitorTestData.class);

        acu.jjtAccept(new JavaParserVisitorReducedAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                res.add((int) toplevel.compute(opMetricKey, node, true, Version.STANDARD));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                res.add((int) toplevel.compute(classMetricKey, node, true, Version.STANDARD));
                return super.visit(node, data);
            }
        }, null);

        System.out.println();

        final List<Integer> cmp = new ArrayList<>();

        acu.jjtAccept(new JavaParserVisitorReducedAdapter() {
            @Override
            public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
                cmp.add((int) toplevel.compute(opMetricKey, node, true, Version.STANDARD));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTAnyTypeDeclaration node, Object data) {
                cmp.add((int) toplevel.compute(classMetricKey, node, true, Version.STANDARD));
                return super.visit(node, data);
            }
        }, null);

        for (int i = 0; i < res.size(); i++) {
            assertNotEquals(res.get(i), cmp.get(i));
        }

    }


    /**
     * Test metric.
     */
    private class RandomMetric extends AbstractMetric implements ClassMetric, OperationMetric {

        private Random random = new Random();


        @Override
        public double computeFor(ASTAnyTypeDeclaration node, MetricVersion version) {
            return random.nextInt();
        }


        @Override
        public double computeFor(ASTMethodOrConstructorDeclaration node, MetricVersion version) {
            return random.nextInt();
        }
    }


}

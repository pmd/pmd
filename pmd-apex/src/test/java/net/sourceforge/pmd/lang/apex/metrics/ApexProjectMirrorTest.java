/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import static net.sourceforge.pmd.lang.apex.metrics.ApexMetricsVisitorTest.parseAndVisitForString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import net.sourceforge.pmd.lang.MetricKeyUtil;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexClassMetric;
import net.sourceforge.pmd.lang.apex.metrics.impl.AbstractApexOperationMetric;
import net.sourceforge.pmd.lang.metrics.Metric.Version;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricMemoizer;
import net.sourceforge.pmd.lang.metrics.MetricVersion;

import apex.jorje.semantic.ast.compilation.Compilation;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirrorTest {

    private static ApexNode<Compilation> acu
        = parseAndVisitForString("public with sharing class MetadataDeployController \n"
                                     + "{\n"
                                     + "\tprivate class Foo {\n"
                                     + "}\n"
                                     + "\n"
                                     + "\tglobal String ZipData { get; set; }\t\n"
                                     + "\t\n"
                                     + "\tpublic MetadataService.AsyncResult AsyncResult {get; private set;}\n"
                                     + "\t\n"
                                     + "\tpublic String getPackageXml(String page)\n"
                                     + "\t{\n"
                                     + "\t\treturn '<?xml version=\"1.0\" encoding=\"UTF-8\"?>' + \n"
                                     + "\t\t\t'<Package xmlns=\"http://soap.sforce.com/2006/04/metadata\">' + \n"
                                     + "    \t\t\t'<types>' + \n"
                                     + "        \t\t\t'<members>HelloWorld</members>' +\n"
                                     + "        \t\t\t'<name>ApexClass</name>' + \n"
                                     + "    \t\t\t'</types>' + \n"
                                     + "    \t\t\t'<version>26.0</version>' + \n"
                                     + "\t\t\t'</Package>';\t\t\n"
                                     + "\t}\n"
                                     + "\t\n"
                                     + "\tpublic String getHelloWorldMetadata()\n"
                                     + "\t{\n"
                                     + "\t\treturn '<?xml version=\"1.0\" encoding=\"UTF-8\"?>' +\n"
                                     + "\t\t\t'<ApexClass xmlns=\"http://soap.sforce.com/2006/04/metadata\">' +\n"
                                     + "\t\t\t    '<apiVersion>28.0</apiVersion>' + \n"
                                     + "\t\t\t    '<status>Active</status>' +\n"
                                     + "\t\t\t'</ApexClass>';\t\t\n"
                                     + "\t}\n"
                                     + "\t\n"
                                     + "\tpublic String getHelloWorld()\t\n"
                                     + "\t{\n"
                                     + "\t\treturn 'public class HelloWorld' + \n"
                                     + "\t\t\t'{' + \n"
                                     + "\t\t\t\t'public static void helloWorld()' +\n"
                                     + "\t\t\t\t'{' + \n"
                                     + "\t\t\t\t\t'System.debug(\\' Hello World\\');' +\n"
                                     + "\t\t\t\t'}' +\n"
                                     + "\t\t\t'}';\n"
                                     + "\t}"
                                     + "}");
    private MetricKey<ASTUserClass> classMetricKey = MetricKeyUtil.of(new RandomClassMetric(), null);
    private MetricKey<ASTMethod> opMetricKey = MetricKeyUtil.of(new RandomOperationMetric(), null);


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
        final ApexProjectMirror toplevel = ApexMetrics.getApexProjectMirror();

        final List<Integer> result = new ArrayList<>();

        acu.jjtAccept(new ApexParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethod node, Object data) {
                MetricMemoizer<ASTMethod> op = toplevel.getOperationStats(node.getQualifiedName());
                result.add((int) ApexMetricsComputer.INSTANCE.computeForOperation(opMetricKey, node, force, Version.STANDARD, op));
                return super.visit(node, data);
            }


            @Override
            public Object visit(ASTUserClass node, Object data) {
                MetricMemoizer<ASTUserClass> clazz = toplevel.getClassStats(node.getQualifiedName());
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
        public double computeFor(ASTUserClass node, MetricVersion version) {
            return random.nextInt();
        }
    }

}

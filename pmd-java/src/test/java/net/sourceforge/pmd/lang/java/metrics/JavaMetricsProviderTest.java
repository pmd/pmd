/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.MetricKey;


/**
 * @author Clément Fournier
 */
public class JavaMetricsProviderTest {

    @Test
    public void testComputeAllMetrics() {

        LanguageMetricsProvider<?, ?> provider = ParserTstUtil.getLanguageVersionHandler("1.8").getLanguageMetricsProvider();

        ASTCompilationUnit acu = ParserTstUtil.parseAndTypeResolveJava("1.8",
                                                                       "class Foo { void bar() { System.out.println(1); } }");

        ASTAnyTypeDeclaration type = acu.getFirstDescendantOfType(ASTAnyTypeDeclaration.class);

        Map<MetricKey<?>, Double> results = provider.computeAllMetricsFor(type);

        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            assertTrue(results.containsKey(key));
        }

        MethodLikeNode op = acu.getFirstDescendantOfType(MethodLikeNode.class);

        Map<MetricKey<?>, Double> opResults = provider.computeAllMetricsFor(op);

        for (JavaOperationMetricKey key : JavaOperationMetricKey.values()) {
            assertTrue(opResults.containsKey(key));
        }
    }


    @Test
    public void testThereIsNoMemoisation() {

        LanguageMetricsProvider<?, ?> provider = ParserTstUtil.getLanguageVersionHandler("1.8").getLanguageMetricsProvider();

        ASTAnyTypeDeclaration tdecl1 = ParserTstUtil.parseAndTypeResolveJava("1.8",
                                                                             "class Foo { void bar() { System.out.println(1); } }").getFirstDescendantOfType(ASTAnyTypeDeclaration.class);

        Map<MetricKey<?>, Double> reference = provider.computeAllMetricsFor(tdecl1);

        ASTAnyTypeDeclaration tdecl2 = ParserTstUtil.parseAndTypeResolveJava("1.8",
                                                                             // same name, different characteristics
                                                                             "class Foo { void bar(){} \npublic void hey() { System.out.println(1); } }").getFirstDescendantOfType(ASTAnyTypeDeclaration.class);

        Map<MetricKey<?>, Double> secondTest = provider.computeAllMetricsFor(tdecl2);

        assertNotEquals(reference, secondTest);

    }


}

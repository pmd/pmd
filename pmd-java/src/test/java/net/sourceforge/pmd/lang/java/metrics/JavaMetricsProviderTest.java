/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;


/**
 * @author Clément Fournier
 */
@Ignore("metrics are like rules, they've not been ported to the new grammar yet")
public class JavaMetricsProviderTest {

    private final JavaParsingHelper java8 = JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("1.8");

    @Test
    public void testComputeAllMetrics() {

        LanguageMetricsProvider provider = java8.getHandler("1.8").getLanguageMetricsProvider();

        ASTCompilationUnit acu = java8.parse("class Foo { void bar() { System.out.println(1); } }");

        ASTAnyTypeDeclaration type = acu.getFirstDescendantOfType(ASTAnyTypeDeclaration.class);

        Map<Metric<?, ?>, Number> results = provider.computeAllMetricsFor(type);

        assertEquals(10, results.size());


        ASTMethodDeclaration op = acu.getFirstDescendantOfType(ASTMethodDeclaration.class);

        Map<Metric<?, ?>, Number> opResults = provider.computeAllMetricsFor(op);

        assertEquals(10, results.size());
    }


    @Test
    public void testThereIsNoMemoisation() {

        LanguageMetricsProvider provider = java8.getHandler("1.8").getLanguageMetricsProvider();

        ASTAnyTypeDeclaration tdecl1 = java8.parse("class Foo { void bar() { System.out.println(1); } }")
                                            .getFirstDescendantOfType(ASTAnyTypeDeclaration.class);

        Map<Metric<?,?>, Number> reference = provider.computeAllMetricsFor(tdecl1);

        // same name, different characteristics
        ASTAnyTypeDeclaration tdecl2 = java8.parse("class Foo { void bar(){} \npublic void hey() { System.out.println(1); } }")
                                            .getFirstDescendantOfType(ASTAnyTypeDeclaration.class);

        Map<Metric<?,?>, Number> secondTest = provider.computeAllMetricsFor(tdecl2);

        assertNotEquals(reference, secondTest);

    }


}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;

/**
 * @author Clément Fournier
 */
class JavaMetricsProviderTest {

    private final JavaParsingHelper java8 = JavaParsingHelper.DEFAULT.withDefaultVersion("1.8");

    @Test
    void testComputeAllMetrics() {


        ASTCompilationUnit acu = java8.parse("class Foo { void bar() { System.out.println(1); } }");

        ASTAnyTypeDeclaration type = acu.getTypeDeclarations().firstOrThrow();

        LanguageMetricsProvider provider = acu.getAstInfo().getLanguageProcessor().services().getLanguageMetricsProvider();
        Map<Metric<?, ?>, Number> results = provider.computeAllMetricsFor(type);

        assertEquals(9, results.size());
    }


    @Test
    void testThereIsNoMemoisation() {


        ASTAnyTypeDeclaration tdecl1 = java8.parse("class Foo { void bar() { System.out.println(1); } }")
                                            .getTypeDeclarations().firstOrThrow();

        LanguageMetricsProvider provider = tdecl1.getAstInfo().getLanguageProcessor().services().getLanguageMetricsProvider();
        Map<Metric<?, ?>, Number> reference = provider.computeAllMetricsFor(tdecl1);

        // same name, different characteristics
        ASTAnyTypeDeclaration tdecl2 = java8.parse("class Foo { void bar(){} \npublic void hey() { System.out.println(1); } }")
                                            .getTypeDeclarations().firstOrThrow();

        Map<Metric<?, ?>, Number> secondTest = provider.computeAllMetricsFor(tdecl2);

        assertNotEquals(reference, secondTest);

    }


}

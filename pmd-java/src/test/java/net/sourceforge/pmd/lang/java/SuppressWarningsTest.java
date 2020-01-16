/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.testframework.RuleTst;

public class SuppressWarningsTest extends RuleTst {

    private static class BarRule extends AbstractJavaRule {
        @Override
        public Object visit(ASTCompilationUnit cu, Object ctx) {
            // Convoluted rule to make sure the violation is reported for the
            // ASTCompilationUnit node
            for (ASTClassOrInterfaceDeclaration c : cu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class)) {
                if (c.getImage().equalsIgnoreCase("bar")) {
                    addViolation(ctx, cu);
                }
            }
            return super.visit(cu, ctx);
        }

        @Override
        public String getName() {
            return "NoBar";
        }
    }

    @Test
    public void testClassLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST1, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
        runTestFromString(TEST2, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
    }

    @Test
    public void testInheritedSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST3, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
    }

    @Test
    public void testMethodLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST4, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testConstructorLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST5, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
    }

    @Test
    public void testFieldLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST6, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testParameterLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST7, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testLocalVariableLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST8, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST9, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionValue1() {
        Report rpt = new Report();
        runTestFromString(TEST9_VALUE1, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionValue2() {
        Report rpt = new Report();
        runTestFromString(TEST9_VALUE2, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionValue3() {
        Report rpt = new Report();
        runTestFromString(TEST9_VALUE3, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionMulitpleValues1() {
        Report rpt = new Report();
        runTestFromString(TEST9_MULTIPLE_VALUES_1, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
    }

    @Test
    public void testSpecificSuppressionMulitpleValues2() {
        Report rpt = new Report();
        runTestFromString(TEST9_MULTIPLE_VALUES_2, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
    }

    @Test
    public void testNoSuppressionBlank() {
        Report rpt = new Report();
        runTestFromString(TEST10, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(2, rpt.size());
    }

    @Test
    public void testNoSuppressionSomethingElseS() {
        Report rpt = new Report();
        runTestFromString(TEST11, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(2, rpt.size());
    }

    @Test
    public void testSuppressAll() {
        Report rpt = new Report();
        runTestFromString(TEST12, new FooRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
    }

    @Test
    public void testSpecificSuppressionAtTopLevel() {
        Report rpt = new Report();
        runTestFromString(TEST13, new BarRule(), rpt,
                LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
        assertEquals(0, rpt.size());
    }

    private static final String TEST1 = "@SuppressWarnings(\"PMD\")\npublic class Foo {}";

    private static final String TEST2 = "@SuppressWarnings(\"PMD\")\npublic class Foo {\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST3 = "public class Baz {\n @SuppressWarnings(\"PMD\")\n public class Bar {\n  void bar() {\n   int foo;\n  }\n }\n}";

    private static final String TEST4 = "public class Foo {\n @SuppressWarnings(\"PMD\")\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST5 = "public class Bar {\n @SuppressWarnings(\"PMD\")\n public Bar() {\n  int foo;\n }\n}";

    private static final String TEST6 = "public class Bar {\n @SuppressWarnings(\"PMD\")\n int foo;\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST7 = "public class Bar {\n int foo;\n void bar(@SuppressWarnings(\"PMD\") int foo) {}\n}";

    private static final String TEST8 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"PMD\") int foo;\n }\n}";

    private static final String TEST9 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"PMD.NoFoo\") int foo;\n }\n}";

    private static final String TEST9_VALUE1 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(value = \"PMD.NoFoo\") int foo;\n }\n}";

    private static final String TEST9_VALUE2 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings({\"PMD.NoFoo\"}) int foo;\n }\n}";

    private static final String TEST9_VALUE3 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(value = {\"PMD.NoFoo\"}) int foo;\n }\n}";

    private static final String TEST9_MULTIPLE_VALUES_1 = "@SuppressWarnings({\"PMD.NoFoo\", \"PMD.NoBar\"})\npublic class Bar {\n int foo;\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST9_MULTIPLE_VALUES_2 = "@SuppressWarnings(value = {\"PMD.NoFoo\", \"PMD.NoBar\"})\npublic class Bar {\n int foo;\n void bar() {\n  int foo;\n }\n}";

    private static final String TEST10 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"\") int foo;\n }\n}";

    private static final String TEST11 = "public class Bar {\n int foo;\n void bar() {\n  @SuppressWarnings(\"SomethingElse\") int foo;\n }\n}";

    private static final String TEST12 = "public class Bar {\n @SuppressWarnings(\"all\") int foo;\n}";

    private static final String TEST13 = "@SuppressWarnings(\"PMD.NoBar\")\npublic class Bar {\n}";
}

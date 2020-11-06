/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.testframework.RuleTst;

public class SuppressWarningsTest extends RuleTst {

    private static class BarRule extends AbstractApexRule {
        @Override
        public Object visit(ASTUserClass clazz, Object ctx) {
            if (clazz.getImage().equalsIgnoreCase("bar")) {
                addViolation(ctx, clazz);
            }
            return super.visit(clazz, ctx);
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
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());
        runTestFromString(TEST2, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());
    }

    @Test
    public void testInheritedSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST3, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());
    }

    @Test
    public void testMethodLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST4, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(1, rpt.size());
    }

    @Test
    public void testConstructorLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST5, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());
    }

    @Test
    public void testFieldLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST6, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(1, rpt.size());
    }

    @Test
    public void testParameterLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST7, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(1, rpt.size());
    }

    @Test
    public void testLocalVariableLevelSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST8, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST9, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(1, rpt.size());
    }

    @Test
    public void testSpecificSuppressionMulitpleValues() {
        Report rpt = new Report();
        runTestFromString(TEST9_MULTIPLE_VALUES, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());
    }

    @Test
    public void testNoSuppressionBlank() {
        Report rpt = new Report();
        runTestFromString(TEST10, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(2, rpt.size());
    }

    @Test
    public void testNoSuppressionSomethingElseS() {
        Report rpt = new Report();
        runTestFromString(TEST11, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(2, rpt.size());
    }

    @Test
    public void testSuppressAll() {
        Report rpt = new Report();
        runTestFromString(TEST12, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());
    }

    @Test
    public void testSpecificSuppressionAtTopLevel() {
        Report rpt = new Report();
        runTestFromString(TEST13, new BarRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());
    }

    @Test
    public void testCommentSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST14, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());

        List<Report.SuppressedViolation> suppressions = rpt.getSuppressedRuleViolations();
        assertEquals(1, suppressions.size());
    }

    @Test
    public void testMessageWithCommentSuppression() {
        Report rpt = new Report();
        runTestFromString(TEST15, new FooRule(), rpt,
                LanguageRegistry.getLanguage(ApexLanguageModule.NAME).getDefaultVersion());
        assertEquals(0, rpt.size());

        List<Report.SuppressedViolation> suppressions = rpt.getSuppressedRuleViolations();
        assertEquals(1, suppressions.size());
        Report.SuppressedViolation suppression = suppressions.get(0);

        assertTrue(suppression.suppressedByNOPMD());
        assertEquals("We allow foo here", suppression.getUserMessage());
    }

    private static final String TEST1 = "@SuppressWarnings('PMD')" + PMD.EOL + "public class Foo {}";

    private static final String TEST2 = "@SuppressWarnings('PMD')" + PMD.EOL + "public class Foo {" + PMD.EOL
            + " void bar() {" + PMD.EOL + "  Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST3 = "public class Baz {" + PMD.EOL + " @SuppressWarnings('PMD')" + PMD.EOL
            + " public class Bar {" + PMD.EOL + "  void bar() {" + PMD.EOL + "   Integer foo;" + PMD.EOL + "  }" + PMD.EOL
            + " }" + PMD.EOL + "}";

    private static final String TEST4 = "public class Foo {" + PMD.EOL + " @SuppressWarnings('PMD')" + PMD.EOL
            + " void bar() {" + PMD.EOL + "  Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST5 = "public class Bar {" + PMD.EOL + " @SuppressWarnings('PMD')" + PMD.EOL
            + " public Bar() {" + PMD.EOL + "  Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST6 = "public class Bar {" + PMD.EOL + " @SuppressWarnings('PMD')" + PMD.EOL
            + " Integer foo;" + PMD.EOL + " void bar() {" + PMD.EOL + "  Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST7 = "public class Bar {" + PMD.EOL + " Integer foo;" + PMD.EOL
            + " void bar(@SuppressWarnings('PMD') Integer foo) {}" + PMD.EOL + "}";

    private static final String TEST8 = "public class Bar {" + PMD.EOL + " Integer foo;" + PMD.EOL + " void bar() {"
            + PMD.EOL + "  @SuppressWarnings('PMD') Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST9 = "public class Bar {" + PMD.EOL + " Integer foo;" + PMD.EOL + " void bar() {"
            + PMD.EOL + "  @SuppressWarnings('PMD.NoFoo') Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST9_MULTIPLE_VALUES = "@SuppressWarnings('PMD.NoFoo, PMD.NoBar')"
            + PMD.EOL + "public class Bar {" + PMD.EOL + " Integer foo;" + PMD.EOL + " void bar() {" + PMD.EOL
            + "  Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST10 = "public class Bar {" + PMD.EOL + " Integer foo;" + PMD.EOL + " void bar() {"
            + PMD.EOL + "  @SuppressWarnings('') Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST11 = "public class Bar {" + PMD.EOL + " Integer foo;" + PMD.EOL + " void bar() {"
            + PMD.EOL + "  @SuppressWarnings('SomethingElse') Integer foo;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST12 = "public class Bar {" + PMD.EOL + " @SuppressWarnings('all') Integer foo;"
            + PMD.EOL + "}";

    private static final String TEST13 = "@SuppressWarnings('PMD.NoBar')" + PMD.EOL + "public class Bar {" + PMD.EOL
            + "}";

    private static final String TEST14 = "public class Bar {" + PMD.EOL + "Integer foo; // NOPMD" + PMD.EOL + "}";

    private static final String TEST15 = "public class Bar {" + PMD.EOL + "Integer foo; //NOPMD We allow foo here" + PMD.EOL + "}";
}

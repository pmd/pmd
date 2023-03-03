/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSuppressed;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

class SuppressWarningsTest extends ApexParserTestBase {

    // This could be a regular xml test

    private static class BarRule extends AbstractApexRule {

        @Override
        public String getMessage() {
            return "a message";
        }

        @Override
        public Object visit(ASTUserClass clazz, Object ctx) {
            if (clazz.getSimpleName().equalsIgnoreCase("bar")) {
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
    void testClassLevelSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST1);
        assertSize(rpt, 0);
        rpt = apex.executeRule(new FooRule(), TEST2);
        assertSize(rpt, 0);
    }

    @Test
    void testInheritedSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST3);
        assertSize(rpt, 0);
    }

    @Test
    void testMethodLevelSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST4);
        assertSize(rpt, 1);
    }

    @Test
    void testConstructorLevelSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST5);
        assertSize(rpt, 0);
    }

    @Test
    void testFieldLevelSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST6);
        assertSize(rpt, 1);
    }

    @Test
    void testParameterLevelSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST7);
        assertSize(rpt, 1);
    }

    @Test
    void testLocalVariableLevelSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST8);
        assertSize(rpt, 1);
    }

    @Test
    void testSpecificSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST9);
        assertSize(rpt, 1);
    }

    @Test
    void testSpecificSuppressionMulitpleValues() {
        Report rpt = apex.executeRule(new FooRule(), TEST9_MULTIPLE_VALUES);
        assertSize(rpt, 0);
    }

    @Test
    void testNoSuppressionBlank() {
        Report rpt = apex.executeRule(new FooRule(), TEST10);
        assertSize(rpt, 2);
    }

    @Test
    void testNoSuppressionSomethingElseS() {
        Report rpt = apex.executeRule(new FooRule(), TEST11);
        assertSize(rpt, 2);
    }

    @Test
    void testSuppressAll() {
        Report rpt = apex.executeRule(new FooRule(), TEST12);
        assertSize(rpt, 0);
    }

    @Test
    void testSpecificSuppressionAtTopLevel() {
        Report rpt = apex.executeRule(new BarRule(), TEST13);
        assertSize(rpt, 0);
    }

    @Test
    void testCommentSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST14);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    void testMessageWithCommentSuppression() {
        Report rpt = apex.executeRule(new FooRule(), TEST15);
        assertSize(rpt, 0);

        List<Report.SuppressedViolation> suppressions = assertSuppressed(rpt, 1);
        Report.SuppressedViolation suppression = suppressions.get(0);

        assertEquals(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR, suppression.getSuppressor());
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

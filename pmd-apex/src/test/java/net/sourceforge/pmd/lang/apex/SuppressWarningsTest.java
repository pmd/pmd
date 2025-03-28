/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.test.ast.TestUtilsKt.assertSuppressed;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexParserTestBase;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.ViolationSuppressor;

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
                asCtx(ctx).addViolation(clazz);
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
        assertNoWarningsWithFoo("""
                                    @SuppressWarnings('PMD')
                                    public class Foo {}\
                                    """);
    }

    private void assertNoWarningsWithFoo(String code) {
        assertWarningsWithFoo(0, code);
    }

    @Test
    void testClassLevelSuppression2() {
        assertNoWarningsWithFoo("""
            @SuppressWarnings('PMD')
            public class Foo {
             void bar() {
              Integer foo;
             }
            }\
            """);
    }

    @Test
    void testInheritedSuppression() {
        assertNoWarningsWithFoo("""
            public class Baz {
             @SuppressWarnings('PMD')
             public class Bar {
              void bar() {
               Integer foo;
              }
             }
            }\
            """);
    }

    @Test
    void testMethodLevelSuppression() {
        assertWarningsWithFoo(1, """
            public class Foo {
             @SuppressWarnings('PMD')
             void bar() {
              Integer foo;
             }
            }\
            """);
    }

    @Test
    void testConstructorLevelSuppression() {
        assertNoWarningsWithFoo("""
            public class Bar {
             @SuppressWarnings('PMD')
             public Bar() {
              Integer foo;
             }
            }\
            """);
    }

    @Test
    void testFieldLevelSuppression() {
        assertWarningsWithFoo(1, """
            public class Bar {
             @SuppressWarnings('PMD')
             Integer foo;
             void bar() {
              Integer foo;
             }
            }\
            """);
    }

    @Test
    void testParameterLevelSuppression() {
        assertWarningsWithFoo(1, """
            public class Bar {
             Integer foo;
             void bar(@SuppressWarnings('PMD') Integer foo) {}
            }\
            """);
    }

    @Test
    void testLocalVariableLevelSuppression() {
        assertWarningsWithFoo(1, """
            public class Bar {
             Integer foo;
             void bar() {
              @SuppressWarnings('PMD') Integer foo;
             }
            }\
            """);
    }

    @Test
    void testSpecificSuppression() {
        assertWarningsWithFoo(1, """
            public class Bar {
             Integer foo;
             void bar() {
              @SuppressWarnings('PMD.NoFoo') Integer foo;
             }
            }\
            """);
    }

    @Test
    void testSpecificSuppressionMulitpleValues() {
        assertNoWarningsWithFoo("""
            @SuppressWarnings('PMD.NoFoo, PMD.NoBar')
            public class Bar {
             Integer foo;
             void bar() {
              Integer foo;
             }
            }\
            """);
    }

    @Test
    void testNoSuppressionBlank() {
        assertWarningsWithFoo(2, """
            public class Bar {
             Integer foo;
             void bar() {
              @SuppressWarnings('') Integer foo;
             }
            }\
            """);
    }

    private void assertWarningsWithFoo(int size, String code) {
        Report rpt = apex.executeRule(new FooRule(), code);
        assertSize(rpt, size);
    }

    @Test
    void testNoSuppressionSomethingElseS() {
        assertWarningsWithFoo(2, """
            public class Bar {
             Integer foo;
             void bar() {
              @SuppressWarnings('SomethingElse') Integer foo;
             }
            }\
            """);
    }

    @Test
    void testSuppressAll() {
        assertNoWarningsWithFoo("""
            public class Bar {
             @SuppressWarnings('all') Integer foo;
            }\
            """);
    }

    @Test
    void testSpecificSuppressionAtTopLevel() {
        Report rpt = apex.executeRule(new BarRule(), """
            @SuppressWarnings('PMD.NoBar')
            public class Bar {
            }\
            """);
        assertSize(rpt, 0);
    }

    @Test
    void testCommentSuppression() {
        Report rpt = apex.executeRule(new FooRule(), """
            public class Bar {
            Integer foo; // NOPMD
            }\
            """);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    @Test
    void testMessageWithCommentSuppression() {
        Report rpt = apex.executeRule(new FooRule(), """
            public class Bar {
            Integer foo; //NOPMD We allow foo here
            }\
            """);
        assertSize(rpt, 0);

        List<Report.SuppressedViolation> suppressions = assertSuppressed(rpt, 1);
        Report.SuppressedViolation suppression = suppressions.get(0);

        assertEquals(ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR, suppression.getSuppressor());
        assertEquals("We allow foo here", suppression.getUserMessage());
    }

}

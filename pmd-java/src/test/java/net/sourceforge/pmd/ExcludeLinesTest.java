/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSuppressed;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

class ExcludeLinesTest extends BaseParserTest {

    @Test
    void testAcceptance() {
        assertSize(java.executeRule(getRule(), TEST1), 0);
        assertSize(java.executeRule(getRule(), TEST2), 1);
    }

    Rule getRule() {
        return new AbstractJavaRule() {
            {
                setMessage("!");
            }

            @Override
            public Object visit(ASTVariableDeclaratorId node, Object data) {
                addViolation(data, node);
                return data;
            }
        };
    }

    @Test
    void testAlternateMarker() {
        Report rpt = java.withSuppressMarker("FOOBAR").executeRule(getRule(), TEST3);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    private static final String TEST1 = "public class Foo {\n"
                                        + " void foo() {\n"
                                        + "  int x; //NOPMD \n"
                                        + " } \n"
                                        + "}";

    private static final String TEST2 = "public class Foo {\n"
                                        + " void foo() {\n"
                                        + "  int x;\n"
                                        + " } \n"
                                        + "}";

    private static final String TEST3 = "public class Foo {\n"
                                        + " void foo() {\n"
                                        + "  int x; // FOOBAR\n"
                                        + " } \n"
                                        + "}";
}

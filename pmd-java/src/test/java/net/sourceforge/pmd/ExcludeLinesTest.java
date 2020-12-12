/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;
import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSuppressed;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class ExcludeLinesTest extends BaseNonParserTest {

    @Test
    public void testAcceptance() {
        assertSize(java.executeRule(getRule(), TEST1), 0);
        assertSize(java.executeRule(getRule(), TEST2), 1);
    }

    public Rule getRule() {
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
    public void testAlternateMarker() {
        Report rpt = java.withParserConfig(p -> p.setProperty(ParserTask.COMMENT_MARKER, "FOOBAR")).executeRule(getRule(), TEST3);
        assertSize(rpt, 0);
        assertSuppressed(rpt, 1);
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int x; //NOPMD "
            + PMD.EOL + " } " + PMD.EOL + "}";

    private static final String TEST2 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int x;"
            + PMD.EOL + " } " + PMD.EOL + "}";

    private static final String TEST3 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  int x; // FOOBAR" + PMD.EOL + " } " + PMD.EOL + "}";
}

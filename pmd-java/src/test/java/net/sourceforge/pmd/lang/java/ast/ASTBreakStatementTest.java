/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTBreakStatementTest extends BaseParserTest {
    @Test
    @Deprecated // Tests deprecated @Image attribute
    void imageIsLabel() {
        ASTCompilationUnit unit = java.parse("class Foo {\n"
                + "    void bar() {\n"
                + "        mylabel: while(true) {\n"
                + "            break mylabel;\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
        );
        ASTBreakStatement breakStatement = unit.descendants(ASTBreakStatement.class).firstOrThrow();
        assertEquals(breakStatement.getLabel(), breakStatement.getImage());
    }
}

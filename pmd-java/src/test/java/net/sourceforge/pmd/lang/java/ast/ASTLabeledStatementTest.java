/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTLabeledStatementTest extends BaseParserTest {
    @Test
    void labelAttribute() {
        ASTCompilationUnit unit = java.parse("class Foo { { int x; mylabel: x = 1; } }");
        ASTLabeledStatement labeledStatement = unit.descendants(ASTLabeledStatement.class).firstOrThrow();
        assertEquals("mylabel", labeledStatement.getLabel());
    }

    @Test
    @Deprecated // Tests deprecated @Image attribute
    void imageShouldBeLabel() {
        ASTCompilationUnit unit = java.parse("class Foo { { int x; mylabel: x = 1; } }");
        ASTLabeledStatement labeledStatement = unit.descendants(ASTLabeledStatement.class).firstOrThrow();
        assertEquals(labeledStatement.getLabel(), labeledStatement.getImage());
        assertEquals("mylabel", labeledStatement.getImage());
    }
}

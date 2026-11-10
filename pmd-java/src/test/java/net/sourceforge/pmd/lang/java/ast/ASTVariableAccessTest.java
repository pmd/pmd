/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTVariableAccessTest extends BaseParserTest {
    @Test
    void nameAttribute() {
        ASTCompilationUnit unit = java.parse("class Foo { { int i = 1; int y = i + 1; } }");
        ASTVariableAccess variableAccess = unit.descendants(ASTVariableAccess.class).firstOrThrow();
        assertEquals("i", variableAccess.getName());
    }

    @Test
    @Deprecated // Tests deprecated @Image attribute
    void imageShouldBeName() {
        ASTCompilationUnit unit = java.parse("class Foo { { int i = 1; int y = i + 1; } }");
        ASTVariableAccess variableAccess = unit.descendants(ASTVariableAccess.class).firstOrThrow();
        assertEquals(variableAccess.getName(), variableAccess.getImage());
    }

}

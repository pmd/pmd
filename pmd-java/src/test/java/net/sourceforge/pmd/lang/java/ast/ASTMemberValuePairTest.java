/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTMemberValuePairTest extends BaseParserTest {
    @Test
    void nameAttribute() {
        ASTCompilationUnit unit = java.parse("class Foo { @Deprecated( att = 1 ) String bar; }");
        ASTMemberValuePair valuePair = unit.descendants(ASTMemberValuePair.class).firstOrThrow();
        assertEquals("att", valuePair.getName());
    }

    @Test
    @Deprecated // Tests deprecated @Image attribute
    void imageShouldBeName() {
        ASTCompilationUnit unit = java.parse("class Foo { @Deprecated( att = 1 ) String bar; }");
        ASTMemberValuePair valuePair = unit.descendants(ASTMemberValuePair.class).firstOrThrow();
        assertEquals(valuePair.getName(), valuePair.getImage());
    }
}

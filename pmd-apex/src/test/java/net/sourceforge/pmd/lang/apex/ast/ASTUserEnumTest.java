/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ASTUserEnumTest extends ApexParserTestBase {

    @Test
    void testEnumName() {
        ASTUserClass node = (ASTUserClass) parse("class Foo { enum Bar { } }");
        ASTUserEnum enumNode = node.descendants(ASTUserEnum.class).firstOrThrow();
        assertEquals("Bar", enumNode.getSimpleName());
    }
}

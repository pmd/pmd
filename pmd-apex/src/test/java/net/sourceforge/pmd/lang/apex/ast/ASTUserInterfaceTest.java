/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ASTUserInterfaceTest extends ApexParserTestBase {

    @Test
    void testInterfaceName() {
        ASTUserInterface node = (ASTUserInterface) parse("interface Foo { }");
        assertEquals("Foo", node.getSimpleName());
    }

    @Test
    void testInnerInterfaceName() {
        ASTUserClass node = (ASTUserClass) parse("class Foo { interface Bar { } }");
        ASTUserInterface innerNode = node.descendants(ASTUserInterface.class).firstOrThrow();
        assertEquals("Bar", innerNode.getSimpleName());
    }

    @Test
    void testSuperInterface() {
        ASTUserInterface toplevel = (ASTUserInterface) parse("public interface CustomInterface extends A {}");
        assertEquals("A", toplevel.getSuperInterfaceName());
    }

    @Test
    void testSuperInterface2() {
        ASTUserInterface toplevel = (ASTUserInterface) parse("public interface CustomInterface extends Other.A {}");
        assertEquals("Other.A", toplevel.getSuperInterfaceName());
    }
}

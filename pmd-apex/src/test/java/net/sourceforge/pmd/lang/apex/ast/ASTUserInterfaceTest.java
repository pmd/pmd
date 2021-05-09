/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTUserInterfaceTest extends ApexParserTestBase {

    @Test
    public void testInterfaceName() {
        ASTUserInterface node = (ASTUserInterface) parse("interface Foo { }");
        Assert.assertEquals("Foo", node.getSimpleName());
    }

    @Test
    public void testInnerInterfaceName() {
        ASTUserClass node = (ASTUserClass) parse("class Foo { interface Bar { } }");
        ASTUserInterface innerNode = node.descendants(ASTUserInterface.class).firstOrThrow();
        Assert.assertEquals("Bar", innerNode.getSimpleName());
    }

    @Test
    public void testSuperInterface() {
        ASTUserInterface toplevel = (ASTUserInterface) parse("public interface CustomInterface extends A {}");
        Assert.assertEquals("A", toplevel.getSuperInterfaceName());
    }

    @Test
    public void testSuperInterface2() {
        ASTUserInterface toplevel = (ASTUserInterface) parse("public interface CustomInterface extends Other.A {}");
        Assert.assertEquals("Other.A", toplevel.getSuperInterfaceName());
    }
}

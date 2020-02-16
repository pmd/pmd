/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTUserEnumTest extends ApexParserTestBase {

    @Test
    public void testEnumName() {
        ApexNode<Compilation> node = parse("class Foo { enum Bar { } }");
        Assert.assertSame(ASTUserClass.class, node.getClass());
        ASTUserEnum enumNode = node.getFirstDescendantOfType(ASTUserEnum.class);
        Assert.assertNotNull(enumNode);
        Assert.assertEquals("Bar", enumNode.getImage());
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.apex.ast.ApexParserTestHelpers.parse;

import org.junit.Assert;
import org.junit.Test;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ASTFieldTest {

    @Test
    public void testGetType() {
        ApexNode<Compilation> node = parse("public class Foo { private String myField = \"a\"; }");
        ASTField field = node.getFirstDescendantOfType(ASTField.class);

        Assert.assertEquals("myField", field.getImage());
        Assert.assertEquals("String", field.getTypeRef());
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTUserEnumTest extends ApexParserTestBase {

    @Test
    public void testEnumName() {
        ASTUserClass node = (ASTUserClass) parse("class Foo { enum Bar { } }");
        ASTUserEnum enumNode = node.descendants(ASTUserEnum.class).firstOrThrow();
        Assert.assertEquals("Bar", enumNode.getSimpleName());
    }
}

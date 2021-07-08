/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTFieldTest extends ApexParserTestBase {

    @Test
    public void testGetType() {
        ASTField field = parse("public class Foo { private String myField = 'a'; }")
            .descendants(ASTField.class).firstOrThrow();

        Assert.assertEquals("myField", field.getImage());
        Assert.assertEquals("String", field.getType());
        Assert.assertEquals("a", field.getValue());
    }

    @Test
    public void testGetValue() {
        ASTField field = parse("public class Foo { private String myField = 'a'; }")
            .descendants(ASTField.class).firstOrThrow();

        Assert.assertEquals("a", field.getValue());
    }

    @Test
    public void testGetNoValue() {
        ASTField field = parse("public class Foo { private String myField; }")
            .descendants(ASTField.class).firstOrThrow();

        Assert.assertNull(field.getValue());
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ASTFieldTest extends ApexParserTestBase {

    @Test
    void testGetType() {
        ASTField field = parse("public class Foo { private String myField = 'a'; }")
            .descendants(ASTField.class).firstOrThrow();

        assertEquals("myField", field.getImage());
        assertEquals("String", field.getType());
        assertEquals("a", field.getValue());
    }

    @Test
    void testGetValue() {
        ASTField field = parse("public class Foo { private String myField = 'a'; }")
            .descendants(ASTField.class).firstOrThrow();

        assertEquals("a", field.getValue());
    }

    @Test
    void testGetNoValue() {
        ASTField field = parse("public class Foo { private String myField; }")
            .descendants(ASTField.class).firstOrThrow();

        assertNull(field.getValue());
    }
}

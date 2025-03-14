/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ASTReferenceExpressionTest extends ApexParserTestBase {
    @Test
    void referenceTypeMethodWithSafeNav() {
        ASTReferenceExpression reference = parse("class Foo { static void bar() { Foo?.staticMethod(); } }")
                .descendants(ASTReferenceExpression.class)
                .first();
        assertEquals(ReferenceType.METHOD, reference.getReferenceType());
        assertTrue(reference.isSafeNav());
    }

    @Test
    void referenceTypeMethodWithoutSafeNav() {
        ASTReferenceExpression reference = parse("class Foo { static void bar() { Foo.staticMethod(); } }")
                .descendants(ASTReferenceExpression.class)
                .first();
        assertEquals(ReferenceType.METHOD, reference.getReferenceType());
        assertFalse(reference.isSafeNav());
    }

    @Test
    void referenceTypeLoad() {
        ASTReferenceExpression reference = parse("class Foo { static void bar() { Foo x = Foo?.INSTANCE; } }")
                .descendants(ASTReferenceExpression.class)
                .first();
        assertEquals(ReferenceType.LOAD, reference.getReferenceType());
        assertTrue(reference.isSafeNav());
    }

    @Test
    void referenceTypeStore() {
        ASTReferenceExpression reference = parse("class Foo { static void bar() { Foo.INSTANCE = x; } }")
                .descendants(ASTReferenceExpression.class)
                .first();
        assertEquals(ReferenceType.STORE, reference.getReferenceType());
        assertFalse(reference.isSafeNav());
    }
}

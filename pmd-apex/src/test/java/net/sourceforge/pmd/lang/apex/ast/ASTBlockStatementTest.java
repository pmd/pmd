/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ASTBlockStatementTest extends ApexParserTestBase {
    @Test
    void noCurlyBraces() {
        ASTBlockStatement blockStatement = parse("class Foo { { if (true) methodCall(); } }")
                .descendants(ASTIfBlockStatement.class)
                .firstChild(ASTBlockStatement.class)
                .first();
        assertFalse(blockStatement.hasCurlyBrace());
    }

    @Test
    void withCurlyBraces() {
        ASTBlockStatement blockStatement = parse("class Foo { { if (true) { methodCall(); } } }")
                .descendants(ASTIfBlockStatement.class)
                .firstChild(ASTBlockStatement.class)
                .first();
        assertTrue(blockStatement.hasCurlyBrace());
    }
}

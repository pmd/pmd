/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtAssignmentAndOperator;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;

/**
 * Minimal test that parses a Kotlin snippet and asserts generated token accessors
 * which call getToken/getTokens return expected terminal nodes.
 *
 * This is to show a bug where antlr4.BaseAntlrTerminalNode#getTokenKind() returns index instead of type (aka kind).
 */
class KotlinParserTokenAccessTest {

    @Test
    void testGetTokensInKtAssignmentAndOperator() {
        String code = "class Foo { fun foo() { var a = 42; a += 1 } }";

        // Parse using KotlinParsingHelper
        KtKotlinFile root = KotlinParsingHelper.DEFAULT.parse(code);

        assertNotNull(root);

        // Traverse to the first function declaration in the file
        KtFunctionDeclaration fn = root.descendants(KtFunctionDeclaration.class).first();
        assertNotNull(fn, "Expected a function declaration in the parsed Kotlin file");

        // Find the first KtAssignmentAndOperator node within the function body
        KtAssignmentAndOperator idNode = fn.descendants(KtAssignmentAndOperator.class).first();
        assertNotNull(idNode, "Expected a assignmentAndOperator within the function node");
        // Call the generated accessor that returns the ADD_ASSIGNMENT terminal node.
        // This calls getToken/getTokens under the hood and will exercise
        // BaseAntlrInnerNode#getToken(s).
        TerminalNode tn = idNode.ADD_ASSIGNMENT();
        assertNotNull(tn, "Expected an ADD_ASSIGNMENT terminal node");
        assertEquals("+=", tn.getText());
        assertEquals(KotlinParser.ADD_ASSIGNMENT, tn.getSymbol().getType());
    }
}

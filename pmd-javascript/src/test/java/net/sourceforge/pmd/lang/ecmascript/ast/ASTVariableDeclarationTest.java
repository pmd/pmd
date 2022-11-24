/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ASTVariableDeclarationTest extends EcmascriptParserTestBase {

    @Test
    void testLet() {
        ASTAstRoot node = js.parse("let x = 1;");
        ASTVariableDeclaration varDecl = (ASTVariableDeclaration) node.getChild(0);
        assertTrue(varDecl.isLet());

        ASTVariableInitializer varInit = (ASTVariableInitializer) varDecl.getChild(0);
        ASTName name = (ASTName) varInit.getChild(0);
        assertEquals("x", name.getImage());
    }

}

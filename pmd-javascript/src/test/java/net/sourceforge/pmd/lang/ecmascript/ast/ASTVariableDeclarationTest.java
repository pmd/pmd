/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.ecmascript.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTVariableDeclarationTest extends EcmascriptParserTestBase {

    @Test
    public void testLet() {
        ASTAstRoot node = js.parse("let x = 1;");
        ASTVariableDeclaration varDecl = (ASTVariableDeclaration) node.getChild(0);
        Assert.assertTrue(varDecl.isLet());

        ASTVariableInitializer varInit = (ASTVariableInitializer) varDecl.getChild(0);
        ASTName name = (ASTName) varInit.getChild(0);
        Assert.assertEquals("x", name.getImage());
    }

}

/*
 * Created on Jan 19, 2005 
 *
 * $Id$
 */
package test.net.sourceforge.pmd.ast;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import test.net.sourceforge.pmd.testframework.ParserTst;

/**
 * 
 * @author mgriffa
 */
public class ASTThrowStatementTest extends ParserTst {

    public final void testGetFirstASTNameImageNull() throws Throwable {
        ASTThrowStatement t = (ASTThrowStatement)getNodes(ASTThrowStatement.class, NULL_NAME).iterator().next();
        assertNull(t.getFirstClassOrInterfaceTypeImage());
    }

    public final void testGetFirstASTNameImageNew() throws Throwable {
        ASTThrowStatement t = (ASTThrowStatement)getNodes(ASTThrowStatement.class, OK_NAME).iterator().next();
        assertEquals("FooException", t.getFirstClassOrInterfaceTypeImage());
    }

    private static final String NULL_NAME =
    "public class Test {" + PMD.EOL +
    "  void bar() {" + PMD.EOL +
    "   throw e;" + PMD.EOL +
    "  }" + PMD.EOL +
    "}";

    private static final String OK_NAME =
    "public class Test {" + PMD.EOL +
    "  void bar() {" + PMD.EOL +
    "   throw new FooException();" + PMD.EOL +
    "  }" + PMD.EOL +
    "}";
}

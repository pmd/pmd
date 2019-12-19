/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import net.sourceforge.pmd.PMD;

/**
 * Created on Jan 19, 2005
 * @author mgriffa
 */
public class ASTThrowStatementTest extends BaseParserTest {

    @Test
    public final void testGetFirstASTNameImageNull() {
        ASTThrowStatement t = java.getNodes(ASTThrowStatement.class, NULL_NAME).get(0);
        assertNull(t.getFirstClassOrInterfaceTypeImage());
    }

    @Test
    public final void testGetFirstASTNameImageNew() {
        ASTThrowStatement t = java.getNodes(ASTThrowStatement.class, OK_NAME).get(0);
        assertEquals("FooException", t.getFirstClassOrInterfaceTypeImage());
    }

    private static final String NULL_NAME = "public class Test {" + PMD.EOL + "  void bar() {" + PMD.EOL + "   throw e;"
            + PMD.EOL + "  }" + PMD.EOL + "}";

    private static final String OK_NAME = "public class Test {" + PMD.EOL + "  void bar() {" + PMD.EOL
            + "   throw new FooException();" + PMD.EOL + "  }" + PMD.EOL + "}";
}

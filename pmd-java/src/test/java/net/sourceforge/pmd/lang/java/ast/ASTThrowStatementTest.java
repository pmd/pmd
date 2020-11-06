/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

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

    private static final String NULL_NAME = "public class Test {\n  void bar() {\n   throw e;\n  }\n}";

    private static final String OK_NAME = "public class Test {\n  void bar() {\n   throw new FooException();\n  }\n}";
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

public class EncodingTest extends BaseParserTest {

    @Test
    public void testDecodingOfUTF8() {
        ASTCompilationUnit acu = java.parse("class Foo { void é() {} }");
        String methodName = acu.descendants(ASTMethodDeclaration.class).firstOrThrow().getName();
        assertEquals("é", methodName);
    }

}

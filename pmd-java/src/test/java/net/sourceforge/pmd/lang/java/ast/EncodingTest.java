/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EncodingTest extends BaseParserTest {

    @Test
    public void testDecodingOfUTF8() {
        ASTCompilationUnit acu = java.parse(TEST_UTF8);
        String methodName = acu.findDescendantsOfType(ASTMethodDeclarator.class).get(0).getImage();
        assertEquals("é", methodName);
    }

    private static final String TEST_UTF8 = "class Foo {\n  void é() {}\n  void fiddle() {}\n}";
}

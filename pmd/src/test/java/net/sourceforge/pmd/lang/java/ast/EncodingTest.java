/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

import org.junit.Test;

public class EncodingTest extends ParserTst {

    @Test
    public void testDecodingOfUTF8() throws Exception {
        ASTCompilationUnit acu = parseJava14(TEST_UTF8);
        String methodName = acu.findDescendantsOfType(ASTMethodDeclarator.class).get(0).getImage();
        assertEquals("é", methodName);
    }

    private static final String TEST_UTF8 = 
            "class Foo {" + PMD.EOL +
            "  void é() {}" + PMD.EOL +
            "  void fiddle() {}" + PMD.EOL +
            "}";
}

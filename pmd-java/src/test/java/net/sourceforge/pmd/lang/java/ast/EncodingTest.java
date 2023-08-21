/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class EncodingTest extends BaseParserTest {

    @Test
    void testDecodingOfUTF8() {
        ASTCompilationUnit acu = java.parse("class Foo { void é() {} }");
        String methodName = acu.descendants(ASTMethodDeclaration.class).firstOrThrow().getName();
        assertEquals("é", methodName);
    }

}

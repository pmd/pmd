/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTClassTypeTest extends BaseParserTest {

    @Test
    void testGithub4990() {
        ASTCompilationUnit acu = java.parse(
            "import java.util.*;\n"
                + "\n"
                + "public class Test {\n"
                + "  public void Test() {\n"
                + "    boolean good4 = java.util.Collections.emptyList();  // Line 6\n"
                + "  }\n"
                + "}");
        List<ASTClassType> types = acu.descendants(ASTClassType.class).toList();

        ASTClassType ct = types.get(0);
        assertEquals("Collections", ct.getSimpleName());
        assertEquals("java.util", ct.getPackageQualifier());

    }

}

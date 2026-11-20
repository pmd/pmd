/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTAmbiguousNameTest extends BaseParserTest {

    @Test
    @Deprecated // Tests deprecated @Image
    void imageIsName() {
        ASTCompilationUnit unit = java.parse("class Foo {\n"
                + "    private static final Logger LOG = LoggerFactory.getLogger(Foo.class);\n"
                + "}\n");
        ASTAmbiguousName astAmbiguousName = unit.descendants(ASTAmbiguousName.class).firstOrThrow();
        assertEquals(astAmbiguousName.getName(), astAmbiguousName.getImage());
    }
}

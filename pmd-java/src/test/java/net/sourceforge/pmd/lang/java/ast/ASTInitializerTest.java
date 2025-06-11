/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import org.junit.jupiter.api.Test;

class ASTInitializerTest extends BaseParserTest {

    @Test
    void testDontCrashOnBlockStatement() {
        java.parse("public class Foo { { x = 5; } }");
    }
}

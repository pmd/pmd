/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTInitializerTest extends BaseParserTest {

    @Test
    void testDontCrashOnBlockStatement() {
        java.parse("public class Foo { { x = 5; } }");
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

public class ASTInitializerTest extends BaseParserTest {

    @Test
    public void testDontCrashOnBlockStatement() {
        java.parse("public class Foo { { x = 5; } }");
    }

}

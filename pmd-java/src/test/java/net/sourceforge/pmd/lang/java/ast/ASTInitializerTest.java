/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ParserTst;

public class ASTInitializerTest extends ParserTst {

    @Test
    public void testDontCrashOnBlockStatement() {
        getNodes(ASTInitializer.class, TEST1);
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL + " {" + PMD.EOL + "   x = 5;" + PMD.EOL + " }"
            + PMD.EOL + "}";
}

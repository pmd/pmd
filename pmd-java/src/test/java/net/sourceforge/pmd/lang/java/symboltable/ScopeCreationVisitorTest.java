/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;

public class ScopeCreationVisitorTest extends STBBaseTst {

    @Test
    public void testScopesAreCreated() {
        parseCode(TEST1);
        ASTBlock n = acu.findDescendantsOfType(ASTIfStatement.class).get(0)
                .getFirstDescendantOfType(ASTBlock.class);
        assertTrue(n.getScope() instanceof LocalScope);
    }

    private static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  if (x>2) {}"
            + PMD.EOL + " }" + PMD.EOL + "}" + PMD.EOL;

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ScopeCreationVisitorTest.class);
    }
}

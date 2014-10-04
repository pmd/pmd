/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.symboltable.LocalScope;

import org.junit.Test;

public class ScopeCreationVisitorTest extends STBBaseTst {

    @Test
    public void testScopesAreCreated() {
        parseCode(TEST1);
        ASTIfStatement n = acu.findDescendantsOfType(ASTIfStatement.class).get(0);
        assertTrue(n.getScope() instanceof LocalScope);
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  if (x>2) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}" + PMD.EOL;

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ScopeCreationVisitorTest.class);
    }
}

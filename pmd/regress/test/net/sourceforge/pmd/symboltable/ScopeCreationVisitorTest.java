/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.symboltable.LocalScope;

public class ScopeCreationVisitorTest extends STBBaseTst {

    public void testScopesAreCreated() {
        parseCode(TEST1);
        ASTIfStatement n = (ASTIfStatement) acu.findChildrenOfType(ASTIfStatement.class).get(0);
        assertTrue(n.getScope() instanceof LocalScope);
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  if (x>2) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}" + PMD.EOL;
}

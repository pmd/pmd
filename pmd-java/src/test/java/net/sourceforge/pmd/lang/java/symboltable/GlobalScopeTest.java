/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class GlobalScopeTest extends BaseNonParserTest {

    @Test
    public void testClassDeclAppears() {
        ASTCompilationUnit acu = parseCode(TEST1);
        Scope scope = acu.getScope();
        Map<NameDeclaration, List<NameOccurrence>> m = scope.getDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    @Test
    public void testEnums() {
        java5.parse(TEST2);
    }

    private static final String TEST1 = "public class Foo {}" + PMD.EOL;

    private static final String TEST2 = "public enum Bar {" + PMD.EOL + "  FOO1 {          " + PMD.EOL
            + "    private static final String FIELD_NAME = \"\";" + PMD.EOL + "  }," + PMD.EOL + "  FOO2 {          "
            + PMD.EOL + "    private static final String FIELD_NAME = \"\";" + PMD.EOL + "  }" + PMD.EOL + "}"
            + PMD.EOL;
}

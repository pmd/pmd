/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

import org.junit.Test;

public class GlobalScopeTest extends STBBaseTst {

    @Test
    public void testClassDeclAppears() {
        parseCode(TEST1);
        ASTCompilationUnit decl = acu;
        Scope scope = decl.getScope();
        Map<NameDeclaration, List<NameOccurrence>> m = scope.getDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    @Test
    public void testEnums() {
        parseCode15(TEST2);
    }

    private static final String TEST1 =
            "public class Foo {}" + PMD.EOL;

    private static final String TEST2 =
            "public enum Bar {" + PMD.EOL +
            "  FOO1 {          " + PMD.EOL +
            "    private static final String FIELD_NAME = \"\";" + PMD.EOL +
            "  }," + PMD.EOL +
            "  FOO2 {          " + PMD.EOL +
            "    private static final String FIELD_NAME = \"\";" + PMD.EOL +
            "  }" + PMD.EOL +
            "}" + PMD.EOL;
}

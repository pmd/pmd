package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTClassDeclaration;
import net.sourceforge.pmd.symboltable.ClassNameDeclaration;

import java.util.Map;

public class GlobalScopeTest extends STBBaseTst {

    public void testClassDeclAppears() {
        parseCode(TEST1);
        Map m = ((ASTClassDeclaration)(acu.findChildrenOfType(ASTClassDeclaration.class)).get(0)).getScope().getClassDeclarations();
        assertEquals(((ClassNameDeclaration)m.keySet().iterator().next()).getImage(), "Foo");
    }

    private static final String TEST1 =
    "public class Foo {}" + PMD.EOL;

}

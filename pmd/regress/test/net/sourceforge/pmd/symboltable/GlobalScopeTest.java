package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.symboltable.Scope;

import java.util.Map;

public class GlobalScopeTest extends STBBaseTst {

    public void testClassDeclAppears() {
        parseCode(TEST1);
        ASTCompilationUnit decl = (ASTCompilationUnit)(acu.findChildrenOfType(ASTCompilationUnit.class)).get(0);
        Scope scope = decl.getScope();
        Map m = scope.getClassDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration)m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    private static final String TEST1 =
    "public class Foo {}" + PMD.EOL;

}

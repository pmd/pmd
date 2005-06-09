package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.symboltable.Scope;

import java.util.Map;

public class SourceFileScopeTest extends STBBaseTst {

    public void testClassDeclAppears() {
        parseCode(TEST1);
        ASTCompilationUnit decl = (ASTCompilationUnit)(acu.findChildrenOfType(ASTCompilationUnit.class)).get(0);
        Scope scope = decl.getScope();
        Map m = scope.getClassDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration)m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    public void testPackageIsEmptyString() {
        parseCode(TEST1);
        ASTCompilationUnit decl = (ASTCompilationUnit)(acu.findChildrenOfType(ASTCompilationUnit.class)).get(0);
        assertEquals(decl.getScope().getEnclosingSourceFileScope().getPackageName(), "");
    }

    public void testPackageNameFound() {
        parseCode(TEST2);
        ASTCompilationUnit decl = (ASTCompilationUnit)(acu.findChildrenOfType(ASTCompilationUnit.class)).get(0);
        assertEquals(decl.getScope().getEnclosingSourceFileScope().getPackageName(), "foo.bar");
    }

    private static final String TEST1 =
    "public class Foo {}" + PMD.EOL;

    private static final String TEST2 =
    "package foo.bar;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    "}" + PMD.EOL;

}

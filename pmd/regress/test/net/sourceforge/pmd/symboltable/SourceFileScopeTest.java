package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.symboltable.ClassNameDeclaration;

import java.util.Iterator;
import java.util.Map;

public class SourceFileScopeTest extends STBBaseTst {

    public void testClassDeclAppears() {
        parseCode(TEST1);
        Map m = acu.getScope().getClassDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    public void testPackageIsEmptyString() {
        parseCode(TEST1);
        ASTCompilationUnit decl = (ASTCompilationUnit) (acu.findChildrenOfType(ASTCompilationUnit.class)).get(0);
        assertEquals(decl.getScope().getEnclosingSourceFileScope().getPackageName(), "");
    }

    public void testPackageNameFound() {
        parseCode(TEST2);
        ASTCompilationUnit decl = (ASTCompilationUnit) (acu.findChildrenOfType(ASTCompilationUnit.class)).get(0);
        assertEquals(decl.getScope().getEnclosingSourceFileScope().getPackageName(), "foo.bar");
    }

    public void testNestedClasses() {
        parseCode(TEST3);
        Map m = acu.getScope().getClassDeclarations();
        Iterator iterator = m.keySet().iterator();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) iterator.next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
        assertFalse(iterator.hasNext());
    }

    private static final String TEST1 =
            "public class Foo {}" + PMD.EOL;

    private static final String TEST2 =
            "package foo.bar;" + PMD.EOL +
            "public class Foo {" + PMD.EOL +
            "}" + PMD.EOL;

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " public class Bar {" + PMD.EOL +
            " }" + PMD.EOL +
            "}" + PMD.EOL;

}

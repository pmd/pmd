package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;

import java.util.Iterator;
import java.util.Map;

public class MethodNameDeclarationTest extends STBBaseTst {

    public void testEquality() {
        parseCode(SIMILAR);
        ASTClassOrInterfaceDeclaration n = (ASTClassOrInterfaceDeclaration) acu.findChildrenOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        Iterator i = m.keySet().iterator();
        MethodNameDeclaration mnd1 = (MethodNameDeclaration) i.next();
        MethodNameDeclaration mnd2 = (MethodNameDeclaration) i.next();
    }

    private static final String SIMILAR =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  bar(x, y);" + PMD.EOL +
            " }" + PMD.EOL +
            " private void bar(int x, int y) {}" + PMD.EOL +
            "}";


}

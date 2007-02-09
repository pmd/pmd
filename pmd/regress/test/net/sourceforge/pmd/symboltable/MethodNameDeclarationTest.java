package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MethodNameDeclarationTest extends STBBaseTst {

    @Test
    public void testEquality() {
        //FIXME does this test DO anything?
        parseCode(SIMILAR);
        ASTClassOrInterfaceDeclaration n = acu.findChildrenOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<MethodNameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getMethodDeclarations();
        Iterator<MethodNameDeclaration> i = m.keySet().iterator();
        MethodNameDeclaration mnd1 = i.next();
        MethodNameDeclaration mnd2 = i.next();
    }

    private static final String SIMILAR =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  bar(x, y);" + PMD.EOL +
            " }" + PMD.EOL +
            " private void bar(int x, int y) {}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MethodNameDeclarationTest.class);
    }
}

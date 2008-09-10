package test.net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.symboltable.MethodScope;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import org.junit.Test;

import java.util.List;
import java.util.Map;

public class MethodScopeTest extends STBBaseTst {

    @Test
    public void testMethodParameterOccurrenceRecorded() {
        parseCode(TEST1);
        Map m = acu.findChildrenOfType(ASTMethodDeclaration.class).get(0).getScope().getVariableDeclarations();
        VariableNameDeclaration vnd = (VariableNameDeclaration) m.keySet().iterator().next();
        assertEquals("bar", vnd.getImage());
        List occs = (List) m.get(vnd);
        NameOccurrence occ = (NameOccurrence) occs.get(0);
        assertEquals(3, occ.getLocation().getBeginLine());
    }

    @Test
    public void testMethodName() {
        parseCode(TEST1);
        ASTMethodDeclaration meth = acu.findChildrenOfType(ASTMethodDeclaration.class).get(0);
        MethodScope ms = (MethodScope) meth.getScope();
        assertEquals(ms.getName(), "foo");
    }
    @Test
    public void testGenerics() {
        parseCode15(TEST_GENERICS);
    }

    public static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void foo(int bar) {" + PMD.EOL +
            "  bar = 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST_GENERICS =
        "public class Tree {" + PMD.EOL +
        "  private List<Object> subForest;" + PMD.EOL +
        "  public <B> Tree<B> fmap(final F<B> f) { return Tree.<B>foo(); }" + PMD.EOL +
        "  public List<Object> subForest() { return null; }" + PMD.EOL +
        "}" + PMD.EOL;

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MethodScopeTest.class);
    }
}

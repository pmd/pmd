/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class VariableNameDeclarationTest extends BaseNonParserTest {

    @Test
    public void testConstructor() {
        ASTCompilationUnit acu = parseCode(TEST1);
        List<ASTVariableDeclaratorId> nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        Scope s = nodes.get(0).getScope();
        NameDeclaration decl = s.getDeclarations().keySet().iterator().next();
        assertEquals("bar", decl.getImage());
        assertEquals(3, decl.getNode().getBeginLine());
    }

    @Test
    public void testExceptionBlkParam() {
        ASTCompilationUnit acu = java.parse(EXCEPTION_PARAMETER);
        ASTVariableDeclaratorId id = acu.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        assertTrue(new VariableNameDeclaration(id).isExceptionBlockParameter());
    }

    @Test
    public void testIsArray() {
        ASTCompilationUnit acu = parseCode(TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        assertTrue(decl.isArray());
    }

    @Test
    public void testPrimitiveType() {
        ASTCompilationUnit acu = parseCode(TEST1);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        assertTrue(decl.isPrimitiveType());
    }

    @Test
    public void testArrayIsReferenceType() {
        ASTCompilationUnit acu = parseCode(TEST3);
        VariableNameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations(VariableNameDeclaration.class).keySet().iterator().next();
        assertTrue(decl.isReferenceType());
    }

    @Test
    public void testPrimitiveTypeImage() {
        ASTCompilationUnit acu = parseCode(TEST3);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("int", ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testRefTypeImage() {
        ASTCompilationUnit acu = parseCode(TEST4);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("String", ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testParamTypeImage() {
        ASTCompilationUnit acu = parseCode(TEST5);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("String", ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testVarKeywordTypeImage() {
        ASTCompilationUnit acu = parseCode(TEST6);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("java.util.ArrayList", ((TypedNameDeclaration) decl).getType().getName());
        // since the type is inferred, there is no type image
        assertEquals(null, ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testVarKeywordWithPrimitiveTypeImage() {
        ASTCompilationUnit acu = parseCode(TEST7);
        NameDeclaration decl = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator().next();
        assertEquals("long", ((TypedNameDeclaration) decl).getType().getName());
        // since the type is inferred, there is no type image
        assertEquals(null, ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testVarKeywordWithIndirectReference() {
        ASTCompilationUnit acu = parseCode(TEST8);
        Iterator<NameDeclaration> nameDeclarationIterator = acu.findDescendantsOfType(ASTVariableDeclaratorId.class).get(0).getScope()
                .getDeclarations().keySet().iterator();
        nameDeclarationIterator.next(); // first variable 'bar'
        NameDeclaration decl = nameDeclarationIterator.next(); // second variable 'foo'
        assertEquals("java.lang.String", ((TypedNameDeclaration) decl).getType().getName());
        // since the type is inferred, there is no type image
        assertEquals(null, ((TypedNameDeclaration) decl).getTypeImage());
    }

    @Test
    public void testLamdaParameterTypeImage() {
        ASTCompilationUnit acu = parseCode(TEST9);
        List<ASTVariableDeclaratorId> variableDeclaratorIds = acu.findDescendantsOfType(
                ASTVariableDeclaratorId.class,
                true
        );

        List<VariableNameDeclaration> nameDeclarations = new ArrayList<>();
        for (ASTVariableDeclaratorId variableDeclaratorId : variableDeclaratorIds) {
            nameDeclarations.add(variableDeclaratorId.getNameDeclaration());
        }

        assertEquals("Map", nameDeclarations.get(0).getTypeImage()); // variable 'bar'
        assertEquals(null, nameDeclarations.get(1).getTypeImage()); // variable 'key'
        assertEquals(null, nameDeclarations.get(2).getTypeImage()); // variable 'value'

        // variable 'foo'
        assertEquals("foo", nameDeclarations.get(3).getName());
        assertEquals("long", nameDeclarations.get(3).getType().getName());
        // since the type is inferred, there is no type image
        assertEquals(null, nameDeclarations.get(3).getTypeImage());
    }

    private static final String EXCEPTION_PARAMETER = "public class Test { { try {} catch(Exception ie) {} } }";

    public static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int bar = 42;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST2 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  try {} catch(Exception e) {}" + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST3 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int[] x;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST4 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  String x;"
            + PMD.EOL + " }" + PMD.EOL + "}";
    public static final String TEST5 = "public class Foo {" + PMD.EOL + " void foo(String x) {}" + PMD.EOL + "}";

    public static final String TEST6 = "import java.util.ArrayList; public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  var bar = new ArrayList<String>(\"param\");" + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST7 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  var bar = 42L;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST8 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  var bar = \"test\";" + PMD.EOL + "  var foo = bar;" + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST9 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  Map<String, Object> bar = new HashMap<>();" + PMD.EOL + "  bar.forEach((key, value) -> {" + PMD.EOL
            + "   if (value instanceof String) {" + PMD.EOL + "    var foo = 42L;" + PMD.EOL
            + "    System.out.println(value);" + PMD.EOL + "   }" + PMD.EOL + "  });" + PMD.EOL + " }" + PMD.EOL + "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(VariableNameDeclarationTest.class);
    }
}

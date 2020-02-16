/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symboltable.testdata.InnerClass;
import net.sourceforge.pmd.lang.java.symboltable.testdata.InnerClass.TheInnerClass;
import net.sourceforge.pmd.lang.java.symboltable.testdata.InnerClass.TheInnerClass.EnumTest;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class ClassScopeTest extends BaseNonParserTest {

    @Test
    public void testEnumsClassScope() {
        java5.parse(ENUM_SCOPE);
    }

    @Test
    public void testEnumTypeParameter() {
        java5.parse(ENUM_TYPE_PARAMETER);
    }

    @Test
    public void testVarArgsEmpty() {
        java5.parse("public class Foo {\n" + "  public void bar1(String s, Integer... i) {}\n"
                + "  public void bar1() {}\n" + "  public void c() {\n" + "    bar1();\n" + "  }\n" + "}\n");
    }

    // FIXME - these will break when this goes from Anonymous$1 to Foo$1
    @Test
    public void testAnonymousInnerClassName() {
        ClassNameDeclaration classDeclaration = new ClassNameDeclaration(null);
        ClassScope s = new ClassScope("Foo", classDeclaration);
        s = new ClassScope(classDeclaration);
        assertEquals("Anonymous$1", s.getClassName());
        s = new ClassScope(classDeclaration);
        assertEquals("Anonymous$2", s.getClassName());
    }

    @Test
    public void testContains() {
        ClassNameDeclaration classDeclaration = new ClassNameDeclaration(null);
        ClassScope s = new ClassScope("Foo", classDeclaration);
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("bar");
        s.addDeclaration(new VariableNameDeclaration(node));
        assertTrue(s.getDeclarations().keySet().iterator().hasNext());
    }

    @Test
    public void testCantContainsSuperToString() {
        ClassNameDeclaration classDeclaration = new ClassNameDeclaration(null);
        ClassScope s = new ClassScope("Foo", classDeclaration);
        JavaNode node = new DummyJavaNode(1);
        node.setImage("super.toString");
        assertFalse(s.contains(new JavaNameOccurrence(node, node.getImage())));
    }

    @Test
    public void testContainsStaticVariablePrefixedWithClassName() {
        ClassNameDeclaration classDeclaration = new ClassNameDeclaration(null);
        ClassScope s = new ClassScope("Foo", classDeclaration);
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("X");
        s.addDeclaration(new VariableNameDeclaration(node));

        JavaNode node2 = new DummyJavaNode(2);
        node2.setImage("Foo.X");
        assertTrue(s.contains(new JavaNameOccurrence(node2, node2.getImage())));
    }

    @Test
    public void testClassName() {
        ASTCompilationUnit acu = parseCode(CLASS_NAME);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        assertEquals("Foo", n.getScope().getEnclosingScope(ClassScope.class).getClassName());
    }

    @Test
    public void testMethodDeclarationRecorded() {
        ASTCompilationUnit acu = parseCode(METHOD_DECLARATIONS_RECORDED);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        ClassScope s = (ClassScope) n.getScope();
        Map<NameDeclaration, List<NameOccurrence>> m = s.getDeclarations();
        assertEquals(1, m.size());
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("bar", mnd.getImage());
        ASTMethodDeclaration node = (ASTMethodDeclaration) mnd.getNode().getParent();
        assertTrue(node.isPrivate());
    }

    @Test
    public void testTwoMethodsSameNameDiffArgs() {
        // TODO this won't work with String and java.lang.String
        ASTCompilationUnit acu = parseCode(METHODS_WITH_DIFF_ARG);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        assertEquals(2, m.size());
        Iterator<NameDeclaration> i = m.keySet().iterator();
        MethodNameDeclaration mnd = (MethodNameDeclaration) i.next();
        assertEquals("bar", mnd.getImage());
        assertEquals("bar", ((MethodNameDeclaration) i.next()).getImage());
    }

    @Test
    public final void testOneParam() {
        ASTCompilationUnit acu = parseCode(ONE_PARAM);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String)", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testTwoParams() {
        ASTCompilationUnit acu = parseCode(TWO_PARAMS);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String,int)", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testNoParams() {
        ASTCompilationUnit acu = parseCode(NO_PARAMS);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("()", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testOneParamVararg() {
        ASTCompilationUnit acu = java5.parse(ONE_PARAM_VARARG);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String...)", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testTwoParamsVararg() {
        ASTCompilationUnit acu = java5.parse(TWO_PARAMS_VARARG);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String,String...)", mnd.getParameterDisplaySignature());
    }

    @Test
    public void testNestedClassesOfImportResolution() {
        ASTCompilationUnit acu = parseCode(NESTED_CLASSES_OF_IMPORT);
        final ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        final ClassScope c = (ClassScope) n.getScope();
        assertEquals(EnumTest.class, c.resolveType("TheInnerClass.EnumTest"));
    }

    @Test
    public void testNestedClassesResolution() {
        final ASTClassOrInterfaceDeclaration n = java.parseClass(InnerClass.class)
                                                     .findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        final ClassScope c = (ClassScope) n.getScope();
        assertEquals(InnerClass.class, c.resolveType("InnerClass"));
        assertEquals(TheInnerClass.class, c.resolveType("InnerClass.TheInnerClass"));
        assertEquals(TheInnerClass.class, c.resolveType("TheInnerClass")); // Within this scope, we can access it directly
    }

    @Test
    public void testImportNestedClassesResolution() {
        ASTCompilationUnit acu = parseCode(IMPORT_NESTED_CLASSES);
        final ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        final ClassScope c = (ClassScope) n.getScope();
        assertEquals(EnumTest.class, c.resolveType("EnumTest"));
    }

    @Test
    public final void testNestedClassDeclFound() {
        ASTCompilationUnit acu = parseCode(NESTED_CLASS_FOUND);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        ClassScope c = (ClassScope) n.getScope();
        Map<NameDeclaration, List<NameOccurrence>> m = c.getDeclarations();
        ClassNameDeclaration cnd = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals("Buz", cnd.getImage());
    }

    @Test
    public final void testbuz() {
        parseCode(METH);
        // SymbolTableViewer st = new SymbolTableViewer();
        // acu.jjtAccept(st, null);
    }

    @Test
    public void testMethodUsageSeen() {
        ASTCompilationUnit acu = parseCode(METHOD_USAGE_SEEN);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        Iterator<Map.Entry<NameDeclaration, List<NameOccurrence>>> i = m.entrySet().iterator();
        MethodNameDeclaration mnd;
        Map.Entry<NameDeclaration, List<NameOccurrence>> entry;

        do {
            entry = i.next();
            mnd = (MethodNameDeclaration) entry.getKey();
        } while (!mnd.getImage().equals("bar"));

        List<NameOccurrence> usages = entry.getValue();
        assertEquals(1, usages.size());
        assertEquals("bar", ((JavaNameOccurrence) usages.get(0)).getImage());
    }

    @Test
    public void testMethodUsageSeenWithThis() {
        ASTCompilationUnit acu = parseCode(METHOD_USAGE_SEEN_WITH_THIS);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        Iterator<Map.Entry<NameDeclaration, List<NameOccurrence>>> i = m.entrySet().iterator();
        MethodNameDeclaration mnd;
        Map.Entry<NameDeclaration, List<NameOccurrence>> entry;

        do {
            entry = i.next();
            mnd = (MethodNameDeclaration) entry.getKey();
        } while (!mnd.getImage().equals("bar"));

        List<NameOccurrence> usages = entry.getValue();
        assertEquals(1, usages.size());
        assertEquals("bar", ((JavaNameOccurrence) usages.get(0)).getImage());
    }

    @Test
    public void testMethodUsageSeen2() {
        ASTCompilationUnit acu = parseCode(METHOD_USAGE_SEEN2);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);

        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();

        assertEquals(2, m.size());

        for (Map.Entry<NameDeclaration, List<NameOccurrence>> entry : m.entrySet()) {
            assertEquals("baz", entry.getKey().getImage());
            if (entry.getKey().getNode().getBeginLine() == 2) {
                // this is the public method declaration - it is not used
                // anywhere
                assertEquals(0, entry.getValue().size());
            } else if (entry.getKey().getNode().getBeginLine() == 5) {
                // this is the private (overloaded) method
                assertEquals(1, entry.getValue().size());
                // it's used once in line 3
                assertEquals(3, entry.getValue().get(0).getLocation().getBeginLine());
            } else {
                fail("unexpected name declaration");
            }
        }
    }

    /**
     * Test case for bug report #2410201
     */
    @Test
    public void testNestedClassFieldAndParameter() {
        ASTCompilationUnit acu = parseCode(NESTED_CLASS_FIELD_AND_PARAM);
        ASTMethodDeclaration node = acu.getFirstDescendantOfType(ASTMethodDeclaration.class);
        Map<VariableNameDeclaration, List<NameOccurrence>> vd = node.getScope().getDeclarations(VariableNameDeclaration.class);
        assertEquals(2, vd.size());

        int paramCount = 0;
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vd.entrySet()) {
            if (entry.getKey().getDeclaratorId().isFormalParameter()) {
                assertEquals("field", entry.getKey().getImage());

                List<NameOccurrence> occurrences = entry.getValue();
                assertEquals(2, occurrences.size());
                NameOccurrence no1 = occurrences.get(0);
                assertEquals(8, no1.getLocation().getBeginLine());
                NameOccurrence no2 = occurrences.get(1);
                assertEquals(9, no2.getLocation().getBeginLine());
                paramCount++;
            }
        }

        assertEquals(1, paramCount);
    }

    @Test
    public void testNullType() {
        ASTCompilationUnit acu = parseCode(TEST_NULL_TYPE);
    }

    private static final String NESTED_CLASS_FIELD_AND_PARAM = "public class Foo {" + PMD.EOL + " class Test {"
            + PMD.EOL + "   public String field;" + PMD.EOL + "   public Test t;" + PMD.EOL + " }" + PMD.EOL
            + " public void foo(String field) {" + PMD.EOL + "   Test t = new Test();" + PMD.EOL + "   t.field = field;"
            + PMD.EOL + "   t.t.field = field;" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String METHOD_USAGE_SEEN2 = "public class Foo {" + PMD.EOL + " public void baz() {" + PMD.EOL
            + "  baz(x, y);" + PMD.EOL + " }" + PMD.EOL + " private void baz(int x, int y) {}" + PMD.EOL + "}";

    private static final String METHOD_USAGE_SEEN = "public class Foo {" + PMD.EOL + " private void bar() {}" + PMD.EOL
            + " public void buz() {" + PMD.EOL + "  bar();" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String METHOD_USAGE_SEEN_WITH_THIS = "public class Foo {" + PMD.EOL + " private void bar() {}"
            + PMD.EOL + " public void buz() {" + PMD.EOL + "  this.bar();" + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String METH = "public class Test {" + PMD.EOL + "  static { " + PMD.EOL + "   int y; "
            + PMD.EOL + "  } " + PMD.EOL + "  void bar(int x) {} " + PMD.EOL + "  void baz(int x) {} " + PMD.EOL + "}";

    private static final String NESTED_CLASS_FOUND = "public class Test {" + PMD.EOL + "  private class Buz {} "
            + PMD.EOL + "}";

    private static final String ONE_PARAM = "public class Test {" + PMD.EOL + "  void bar(String x) {" + PMD.EOL + "  }"
            + PMD.EOL + "}";

    private static final String TWO_PARAMS = "public class Test {" + PMD.EOL + "  void bar(String x, int y) {" + PMD.EOL
            + "  }" + PMD.EOL + "}";

    private static final String NO_PARAMS = "public class Test {" + PMD.EOL + "  void bar() {" + PMD.EOL + "  }"
            + PMD.EOL + "}";

    private static final String ONE_PARAM_VARARG = "public class Test {" + PMD.EOL + "  void bar(String... s) {"
            + PMD.EOL + "  }" + PMD.EOL + "}";

    private static final String TWO_PARAMS_VARARG = "public class Test {" + PMD.EOL
            + "  void bar(String s1, String... s2) {" + PMD.EOL + "  }" + PMD.EOL + "}";

    private static final String CLASS_NAME = "public class Foo {}";

    private static final String METHOD_DECLARATIONS_RECORDED = "public class Foo {" + PMD.EOL + " private void bar() {}"
            + PMD.EOL + "}";

    private static final String METHODS_WITH_DIFF_ARG = "public class Foo {" + PMD.EOL
            + " private void bar(String x) {}" + PMD.EOL + " private void bar() {}" + PMD.EOL + "}";

    private static final String ENUM_SCOPE = "public enum Foo {" + PMD.EOL + " HEAP(\"foo\");" + PMD.EOL
            + " private final String fuz;" + PMD.EOL + " public String getFuz() {" + PMD.EOL + "  return fuz;" + PMD.EOL
            + " }" + PMD.EOL + "}";

    public static final String TEST_NULL_TYPE = "public abstract class NullTypeTest {" + PMD.EOL
            + "   protected Comparator<TreeNode> nodesComparator = (o1, o2) -> StringHelper.saveCompare(getFilterableString(o1), getFilterableString(o2));"
            + PMD.EOL + "   public abstract String getFilterableString(TreeNode node);" + PMD.EOL + "}";

    private static final String ENUM_TYPE_PARAMETER = "public enum Foo {" + PMD.EOL
            + "   BAR(isCustomer(BazEnum.FOO_BAR));" + PMD.EOL + "   Foo(boolean isCustomer) { }" + PMD.EOL
            + "   private static boolean isCustomer(BazEnum baz) {" + PMD.EOL + "      return false;" + PMD.EOL + "   }"
            + PMD.EOL + "}";

    private static final String IMPORT_NESTED_CLASSES =
            "import net.sourceforge.pmd.lang.java.symboltable.testdata.InnerClass.TheInnerClass.EnumTest;" + PMD.EOL
            + "public class Foo {" + PMD.EOL
            + " public EnumTest e;" + PMD.EOL
            + "}" + PMD.EOL;

    private static final String NESTED_CLASSES_OF_IMPORT =
            "import net.sourceforge.pmd.lang.java.symboltable.testdata.InnerClass.TheInnerClass;" + PMD.EOL
            + "public class Foo {" + PMD.EOL
            + " public TheInnerClass.EnumTest e;" + PMD.EOL
            + "}" + PMD.EOL;
}

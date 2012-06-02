/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

import org.junit.Test;
public class ClassScopeTest extends STBBaseTst {

    @Test
    public void testEnumsClassScope() {
        parseCode15(ENUM_SCOPE);
    }

    // FIXME - these will break when this goes from Anonymous$1 to Foo$1
    @Test
    public void testAnonymousInnerClassName() {
        ClassScope s = new ClassScope("Foo");
        s = new ClassScope();
        assertEquals("Anonymous$1", s.getClassName());
        s = new ClassScope();
        assertEquals("Anonymous$2", s.getClassName());
    }

    @Test
    public void testContains() {
        ClassScope s = new ClassScope("Foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("bar");
        s.addDeclaration(new VariableNameDeclaration(node));
        assertTrue(s.getVariableDeclarations().keySet().iterator().hasNext());
    }

    @Test
    public void testCantContainsSuperToString() {
        ClassScope s = new ClassScope("Foo");
        JavaNode node = new DummyJavaNode(1);
        node.setImage("super.toString");
        assertFalse(s.contains(new NameOccurrence(node, node.getImage())));
    }

    @Test
    public void testContainsStaticVariablePrefixedWithClassName() {
        ClassScope s = new ClassScope("Foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("X");
        s.addDeclaration(new VariableNameDeclaration(node));

        JavaNode node2 = new DummyJavaNode(2);
        node2.setImage("Foo.X");
        assertTrue(s.contains(new NameOccurrence(node2, node2.getImage())));
    }

    @Test
    public void testClassName() {
        parseCode(CLASS_NAME);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        assertEquals("Foo", n.getScope().getEnclosingClassScope().getClassName());
    }

    @Test
    public void testMethodDeclarationRecorded() {
        parseCode(METHOD_DECLARATIONS_RECORDED);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        ClassScope s = (ClassScope) n.getScope();
        Map m = s.getMethodDeclarations();
        assertEquals(1, m.size());
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("bar", mnd.getImage());
        ASTMethodDeclaration node = (ASTMethodDeclaration) mnd.getNode().jjtGetParent();
        assertTrue(node.isPrivate());
    }

    @Test
    public void testTwoMethodsSameNameDiffArgs() {
        // TODO this won't work with String and java.lang.String
        parseCode(METHODS_WITH_DIFF_ARG);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        assertEquals(2, m.size());
        Iterator i = m.keySet().iterator();
        MethodNameDeclaration mnd = (MethodNameDeclaration) i.next();
        assertEquals("bar", mnd.getImage());
        assertEquals("bar", ((MethodNameDeclaration) i.next()).getImage());
    }


    @Test
    public final void testOneParam() throws Throwable {
        parseCode(ONE_PARAM);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String)", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testTwoParams() throws Throwable {
        parseCode(TWO_PARAMS);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String,int)", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testNoParams() throws Throwable {
        parseCode(NO_PARAMS);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("()", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testOneParamVararg() throws Throwable {
    	parseCode15(ONE_PARAM_VARARG);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String...)", mnd.getParameterDisplaySignature());
    }

    @Test
    public final void testTwoParamsVararg() throws Throwable {
    	parseCode15(TWO_PARAMS_VARARG);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        MethodNameDeclaration mnd = (MethodNameDeclaration) m.keySet().iterator().next();
        assertEquals("(String,String...)", mnd.getParameterDisplaySignature());
    }


    @Test
    public final void testNestedClassDeclFound() throws Throwable {
        parseCode(NESTED_CLASS_FOUND);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        ClassScope c = (ClassScope) n.getScope();
        Map m = c.getClassDeclarations();
        ClassNameDeclaration cnd = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals("Buz", cnd.getImage());
    }

    @Test
    public final void testbuz() throws Throwable {
        parseCode(METH);
        //SymbolTableViewer st = new SymbolTableViewer();
        //acu.jjtAccept(st, null);
    }

    @Test
    public void testMethodUsageSeen() {
        parseCode(METHOD_USAGE_SEEN);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        Iterator i = m.entrySet().iterator();
        MethodNameDeclaration mnd;
        Map.Entry entry;
        
        do {
            entry = (Map.Entry) i.next();
            mnd = (MethodNameDeclaration) entry.getKey();
        } while (!mnd.getImage().equals("bar"));

        List usages = (List) entry.getValue();
        assertEquals(1, usages.size());
        assertEquals("bar", ((NameOccurrence) usages.get(0)).getImage());
    }

    @Test
    public void testMethodUsageSeenWithThis() {
        parseCode(METHOD_USAGE_SEEN_WITH_THIS);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        Iterator i = m.entrySet().iterator();
        MethodNameDeclaration mnd;
        Map.Entry entry;
        
        do {
            entry = (Map.Entry) i.next();
            mnd = (MethodNameDeclaration) entry.getKey();
        } while (!mnd.getImage().equals("bar"));

        List usages = (List) entry.getValue();
        assertEquals(1, usages.size());
        assertEquals("bar", ((NameOccurrence) usages.get(0)).getImage());
    }

    @Test
    public void testMethodUsageSeen2() {
        parseCode(METHOD_USAGE_SEEN2);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope) n.getScope()).getMethodDeclarations();
        Iterator i = m.entrySet().iterator();
        Map.Entry entry = (Map.Entry) i.next();
        MethodNameDeclaration mnd = (MethodNameDeclaration) entry.getKey();
        if (mnd.getNode().getBeginLine() == 2) {
            List usages = (List) entry.getValue();
            System.out.println(usages.size());
            System.out.println(mnd);
            mnd = (MethodNameDeclaration) i.next();
        }
    }

    /**
     * Test case for bug report #2410201
     */
    @Test
    public void testNestedClassFieldAndParameter() {
    	parseCode(NESTED_CLASS_FIELD_AND_PARAM);
    	ASTMethodDeclaration node = acu.getFirstDescendantOfType(ASTMethodDeclaration.class);
    	Map<VariableNameDeclaration, List<NameOccurrence>> vd = node.getScope().getVariableDeclarations();
    	assertEquals(1, vd.size());
    	
    	for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vd.entrySet()) {
    		assertEquals("field", entry.getKey().getImage());
    		
    		List<NameOccurrence> occurrences = entry.getValue();
			assertEquals(2, occurrences.size());
			NameOccurrence no1 = occurrences.get(0);
			assertEquals(8, no1.getLocation().getBeginLine());
			NameOccurrence no2 = occurrences.get(1);
			assertEquals(9, no2.getLocation().getBeginLine());
    	}
    	
    }

    private static final String NESTED_CLASS_FIELD_AND_PARAM =
            "public class Foo {" + PMD.EOL +
            " class Test {" + PMD.EOL +
            "   public String field;" + PMD.EOL +
            "   public Test t;" + PMD.EOL +
            " }" + PMD.EOL +
            " public void foo(String field) {" + PMD.EOL +
            "   Test t = new Test();" + PMD.EOL +
            "   t.field = field;" + PMD.EOL +
            "   t.t.field = field;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String METHOD_USAGE_SEEN2 =
            "public class Foo {" + PMD.EOL +
            " public void baz() {" + PMD.EOL +
            "  baz(x, y);" + PMD.EOL +
            " }" + PMD.EOL +
            " private void baz(int x, int y) {}" + PMD.EOL +
            "}";


    private static final String METHOD_USAGE_SEEN =
            "public class Foo {" + PMD.EOL +
            " private void bar() {}" + PMD.EOL +
            " public void buz() {" + PMD.EOL +
            "  bar();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String METHOD_USAGE_SEEN_WITH_THIS =
            "public class Foo {" + PMD.EOL +
            " private void bar() {}" + PMD.EOL +
            " public void buz() {" + PMD.EOL +
            "  this.bar();" + PMD.EOL +
            " }" + PMD.EOL +
            "}";


    private static final String METH =
            "public class Test {" + PMD.EOL +
            "  static { " + PMD.EOL +
            "   int y; " + PMD.EOL +
            "  } " + PMD.EOL +
            "  void bar(int x) {} " + PMD.EOL +
            "  void baz(int x) {} " + PMD.EOL +
            "}";

    private static final String NESTED_CLASS_FOUND =
            "public class Test {" + PMD.EOL +
            "  private class Buz {} " + PMD.EOL +
            "}";

    private static final String ONE_PARAM =
            "public class Test {" + PMD.EOL +
            "  void bar(String x) {" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String TWO_PARAMS =
            "public class Test {" + PMD.EOL +
            "  void bar(String x, int y) {" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String NO_PARAMS =
            "public class Test {" + PMD.EOL +
            "  void bar() {" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String ONE_PARAM_VARARG =
            "public class Test {" + PMD.EOL +
            "  void bar(String... s) {" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";

    private static final String TWO_PARAMS_VARARG =
            "public class Test {" + PMD.EOL +
            "  void bar(String s1, String... s2) {" + PMD.EOL +
            "  }" + PMD.EOL +
            "}";


    private static final String CLASS_NAME =
            "public class Foo {}";

    private static final String METHOD_DECLARATIONS_RECORDED =
            "public class Foo {" + PMD.EOL +
            " private void bar() {}" + PMD.EOL +
            "}";

    private static final String METHODS_WITH_DIFF_ARG =
            "public class Foo {" + PMD.EOL +
            " private void bar(String x) {}" + PMD.EOL +
            " private void bar() {}" + PMD.EOL +
            "}";

    private static final String ENUM_SCOPE =
            "public enum Foo {" + PMD.EOL +
            " HEAP(\"foo\");" + PMD.EOL +
            " private final String fuz;" + PMD.EOL +
            " public String getFuz() {" + PMD.EOL +
            "  return fuz;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ClassScopeTest.class);
    }
}

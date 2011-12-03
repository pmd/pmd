package net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.symboltable.ClassNameDeclaration;

import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
public class SourceFileScopeTest extends STBBaseTst {

    @Test
    public void testClassDeclAppears() {
        parseCode(TEST1);
        Map m = acu.getScope().getClassDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    @Test
    public void testPackageIsEmptyString() {
        parseCode(TEST1);
        ASTCompilationUnit decl = acu;
        assertEquals(decl.getScope().getEnclosingSourceFileScope().getPackageName(), "");
    }

    @Test
    public void testPackageNameFound() {
        parseCode(TEST2);
        ASTCompilationUnit decl = acu;
        assertEquals(decl.getScope().getEnclosingSourceFileScope().getPackageName(), "foo.bar");
    }

    @Test
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(SourceFileScopeTest.class);
    }
}

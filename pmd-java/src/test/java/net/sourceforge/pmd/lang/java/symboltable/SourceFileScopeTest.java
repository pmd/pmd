/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class SourceFileScopeTest extends BaseNonParserTest {

    @Test
    public void testClassDeclAppears() {
        ASTCompilationUnit acu = parseCode(TEST1);
        Map<NameDeclaration, List<NameOccurrence>> m = acu.getScope().getDeclarations();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) m.keySet().iterator().next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
    }

    @Test
    public void testPackageIsEmptyString() {
        ASTCompilationUnit acu = parseCode(TEST1);
        assertEquals(acu.getScope().getEnclosingScope(SourceFileScope.class).getPackageName(), "");
    }

    @Test
    public void testPackageNameFound() {
        ASTCompilationUnit acu = parseCode(TEST2);
        assertEquals(acu.getScope().getEnclosingScope(SourceFileScope.class).getPackageName(), "foo.bar");
    }

    @Test
    public void testNestedClasses() {
        ASTCompilationUnit acu = parseCode(TEST3);
        Map<NameDeclaration, List<NameOccurrence>> m = acu.getScope().getDeclarations();
        Iterator<NameDeclaration> iterator = m.keySet().iterator();
        ClassNameDeclaration classNameDeclaration = (ClassNameDeclaration) iterator.next();
        assertEquals(classNameDeclaration.getImage(), "Foo");
        assertFalse(iterator.hasNext());
    }

    private static final String TEST1 = "public class Foo {}" + PMD.EOL;

    private static final String TEST2 = "package foo.bar;" + PMD.EOL + "public class Foo {" + PMD.EOL + "}" + PMD.EOL;

    private static final String TEST3 = "public class Foo {" + PMD.EOL + " public class Bar {" + PMD.EOL + " }"
            + PMD.EOL + "}" + PMD.EOL;
}

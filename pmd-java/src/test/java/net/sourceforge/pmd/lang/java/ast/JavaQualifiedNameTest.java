/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

/**
 * @author Clément Fournier
 */
class JavaQualifiedNameTest {


    private <T extends Node> List<T> getNodes(Class<T> target, String code) {
        return JavaParsingHelper.DEFAULT.withDefaultVersion("15").getNodes(target, code);
    }

    @Test
    void testEmptyPackage() {
        final String TEST = "class Foo {}";
        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            assertEquals("Foo", coid.getBinaryName());
            assertEquals("", coid.getPackageName());
        }
    }


    @Test
    void testPackage() {
        final String TEST = "package foo.bar; class Bzaz{}";

        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);
        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            assertEquals("foo.bar.Bzaz", coid.getBinaryName());
        }
    }


    @Test
    void testNestedClass() {
        final String TEST = "package foo.bar; class Bzaz{ class Bor{ class Foo{}}}";

        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            if ("Foo".equals(coid.getImage())) {
                assertEquals("foo.bar.Bzaz$Bor$Foo", coid.getBinaryName());
            }
        }
    }


    @Test
    void testNestedEnum() {
        final String TEST = "package foo.bar; class Foo { enum Bzaz{HOO;}}";

        List<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            assertEquals("foo.bar.Foo$Bzaz", coid.getBinaryName());
            assertEquals("Bzaz", coid.getSimpleName());
            assertEquals("foo.bar", coid.getPackageName());
        }
    }


    @Test
    void testEnum() {
        final String TEST = "package foo.bar; enum Bzaz{HOO;}";

        List<ASTEnumDeclaration> nodes = getNodes(ASTEnumDeclaration.class, TEST);

        for (ASTEnumDeclaration coid : nodes) {
            assertEquals("foo.bar.Bzaz", coid.getBinaryName());
            assertEquals("Bzaz", coid.getSimpleName());
            assertEquals("foo.bar", coid.getPackageName());
        }
    }


    @Test
    void testNestedEmptyPackage() {
        final String TEST = "class Bzaz{ class Bor{ class Foo{}}}";

        List<ASTClassOrInterfaceDeclaration> nodes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        for (ASTClassOrInterfaceDeclaration coid : nodes) {
            if ("Foo".equals(coid.getSimpleName())) {
                assertEquals("Bzaz$Bor$Foo", coid.getBinaryName());
                assertEquals("", coid.getPackageName());
            }
        }
    }

    @Test
    void testSimpleLocalClass() {
        final String TEST = "package bar; class Boron { public void foo(String j) { class Local {} } }";

        List<ASTClassOrInterfaceDeclaration> classes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertEquals("bar.Boron$1Local", classes.get(1).getBinaryName());
    }


    @Test
    void testLocalClassNameClash() {
        final String TEST = "package bar; class Bzaz{ void foo() { class Local {} } {// initializer\n class Local {}}}";

        List<ASTClassOrInterfaceDeclaration> classes
            = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertEquals("bar.Bzaz$1Local", classes.get(1).getBinaryName());
        assertEquals("bar.Bzaz$2Local", classes.get(2).getBinaryName());
    }


    @Test
    void testLocalClassDeepNesting() {
        final String TEST
            = "class Bzaz{ void foo() { "
            + "  class Local { "
            + "    class Nested {"
            + "      {"
            + "        class InnerLocal{}"
            + "      }"
            + "    }"
            + "  }"
            + "}}";

        List<ASTClassOrInterfaceDeclaration> classes
            = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertEquals("Bzaz$1Local", classes.get(1).getBinaryName());
        assertEquals("Local", classes.get(1).getSimpleName());
        assertTrue(classes.get(1).isLocal());
        assertFalse(classes.get(1).isNested());

        assertEquals("Bzaz$1Local$Nested", classes.get(2).getBinaryName());
        assertFalse(classes.get(2).isLocal());
        assertTrue(classes.get(2).isNested());

        assertEquals("Bzaz$1Local$Nested$1InnerLocal", classes.get(3).getBinaryName());
        assertTrue(classes.get(3).isLocal());
        assertFalse(classes.get(3).isNested());
    }


    @Test
    void testAnonymousClass() {
        final String TEST
            = "class Bzaz{ void foo() { "
            + "  new Runnable() {"
            + "      public void run() {}"
            + "  };"
            + "}}";

        List<ASTAnonymousClassDeclaration> classes = getNodes(ASTAnonymousClassDeclaration.class, TEST);

        assertEquals(("Bzaz$1"), classes.get(0).getBinaryName());
        assertFalse(classes.get(0).isLocal());
        assertTrue(classes.get(0).isAnonymous());
        assertEquals("", classes.get(0).getSimpleName());
    }


    @Test
    void testMultipleAnonymousClasses() {
        final String TEST
            = "class Bzaz{ void foo() { "
            + "  new Runnable() {"
            + "      public void run() {}"
            + "  };"
            + "  new Runnable() {"
            + "      public void run() {}"
            + "  };"
            + "}}";

        List<ASTAnonymousClassDeclaration> classes = getNodes(ASTAnonymousClassDeclaration.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals("Bzaz$1", classes.get(0).getBinaryName());
        assertEquals("Bzaz$2", classes.get(1).getBinaryName());
    }


    @Test
    void testNestedAnonymousClass() {
        final String TEST
            = "class Bzaz{ void foo() {"
            + "  new Runnable() {"
            + "    public void run() {"
            + "      new Runnable() {"
            + "        public void run() {}"
            + "      };"
            + "    }"
            + "  };"
            + "}}";

        List<ASTAnonymousClassDeclaration> classes = getNodes(ASTAnonymousClassDeclaration.class, TEST);

        assertNotEquals(classes.get(0), classes.get(1));
        assertEquals("Bzaz$1", classes.get(0).getBinaryName());
        assertEquals("Bzaz$1$1", classes.get(1).getBinaryName());
    }


    @Test
    void testLocalInAnonymousClass() {
        final String TEST
                = "class Bzaz{ void foo() {"
                + "  new Runnable() {"
                + "    public void run() {"
                + "      class FooRunnable {}"
                + "    }"
                + "  };"
                + "}}";

        List<ASTClassOrInterfaceDeclaration> classes = getNodes(ASTClassOrInterfaceDeclaration.class, TEST);

        assertTrue(classes.get(1).isLocal());
        assertEquals("Bzaz$1$1FooRunnable", classes.get(1).getBinaryName());
    }

}


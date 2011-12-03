/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.symboltable.TypeSet;

import org.junit.Test;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TypeSetTest {

    @Test
    public void testASTCompilationUnitPackage() {
        TypeSet t = new TypeSet();
        t.setASTCompilationUnitPackage("java.lang.");
        assertEquals("java.lang.", t.getASTCompilationUnitPackage());
    }

    @Test
    public void testAddImport() {
        TypeSet t = new TypeSet();
        t.addImport("java.io.File");
        assertEquals(1, t.getImportsCount());
    }

    @Test
    public void testFindClassImplicitImport() throws Throwable {
        TypeSet t = new TypeSet();
        Class clazz = t.findClass("String");
        assertEquals(String.class, clazz);
    }

    @Test
    public void testFindClassSamePackage() throws Throwable {
        TypeSet t = new TypeSet();
        t.setASTCompilationUnitPackage("net.sourceforge.pmd.");
        Class clazz = t.findClass("PMD");
        assertEquals(PMD.class, clazz);
    }

    @Test
    public void testFindClassExplicitImport() throws Throwable {
        TypeSet t = new TypeSet();
        t.addImport("java.io.File");
        Class clazz = t.findClass("File");
        assertEquals(File.class, clazz);
    }

    @Test
    public void testFindClassImportOnDemand() throws Throwable {
        TypeSet t = new TypeSet();
        t.addImport("java.io.*");
        Class clazz = t.findClass("File");
        assertEquals(File.class, clazz);
    }

    @Test
    public void testFindClassPrimitive() throws Throwable {
        TypeSet t = new TypeSet();
        assertEquals(int.class, t.findClass("int"));
    }

    @Test
    public void testFindClassVoid() throws Throwable {
        TypeSet t = new TypeSet();
        assertEquals(void.class, t.findClass("void"));
    }

    @Test
    public void testFindFullyQualified() throws Throwable {
        TypeSet t = new TypeSet();
        assertEquals(String.class, t.findClass("java.lang.String"));
        assertEquals(Set.class, t.findClass("java.util.Set"));
    }

    // inner class tests
    @Test
    public void testPrimitiveTypeResolver() throws Throwable {
        TypeSet.Resolver r = new TypeSet.PrimitiveTypeResolver();
        assertEquals(int.class, r.resolve("int"));
        assertEquals(byte.class, r.resolve("byte"));
        assertEquals(long.class, r.resolve("long"));
    }

    @Test
    public void testVoidTypeResolver() throws Throwable {
        TypeSet.Resolver r = new TypeSet.VoidResolver();
        assertEquals(void.class, r.resolve("void"));
    }

    @Test
    public void testExplicitImportResolver() throws Throwable {
        Set<String> imports = new HashSet<String>();
        imports.add("java.io.File");
        TypeSet.Resolver r = new TypeSet.ExplicitImportResolver(imports);
        assertEquals(File.class, r.resolve("File"));
    }

    @Test
    public void testImplicitImportResolverPass() throws Throwable {
        TypeSet.Resolver r = new TypeSet.ImplicitImportResolver();
        assertEquals(String.class, r.resolve("String"));
    }

    @Test(expected=ClassNotFoundException.class)
    public void testImplicitImportResolverPassFail() throws Throwable {
        TypeSet.Resolver r = new TypeSet.ImplicitImportResolver();
        r.resolve("PMD");
    }

    @Test
    public void testCurrentPackageResolverPass() throws Throwable {
        TypeSet.Resolver r = new TypeSet.CurrentPackageResolver("net.sourceforge.pmd.");
        assertEquals(PMD.class, r.resolve("PMD"));
    }

    @Test
    public void testImportOnDemandResolverPass() throws Throwable {
        TypeSet.Resolver r = getResolver();
        assertEquals(Set.class, r.resolve("Set"));
        assertEquals(File.class, r.resolve("File"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void importOnDemandResolverFail1() throws Throwable {
        TypeSet.Resolver r = getResolver();
        r.resolve("foo");
    }

    @Test(expected = ClassNotFoundException.class)
    public void importOnDemandResolverFail2() throws Throwable {
        TypeSet.Resolver r = getResolver();
        r.resolve("String");
    }

    private TypeSet.Resolver getResolver() {
        Set<String> imports = new HashSet<String>();
        imports.add("java.io.*");
        imports.add("java.util.*");
        TypeSet.Resolver r = new TypeSet.ImportOnDemandResolver(imports);
        return r;
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TypeSetTest.class);
    }
}

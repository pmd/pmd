/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;

public class TypeSetTest {
    private PMDASMClassLoader pmdClassLoader = PMDASMClassLoader.getInstance(TypeSetTest.class.getClassLoader());

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
    public void testFindClassImplicitImport() throws ClassNotFoundException {
        TypeSet t = new TypeSet();
        Class<?> clazz = t.findClass("String");
        assertEquals(String.class, clazz);
    }

    @Test
    public void testFindClassSamePackage() throws ClassNotFoundException {
        TypeSet t = new TypeSet();
        t.setASTCompilationUnitPackage("net.sourceforge.pmd");
        Class<?> clazz = t.findClass("PMD");
        assertEquals(PMD.class, clazz);
    }

    @Test
    public void testFindClassExplicitImport() throws ClassNotFoundException {
        TypeSet t = new TypeSet();
        t.addImport("java.io.File");
        Class<?> clazz = t.findClass("File");
        assertEquals(File.class, clazz);
    }

    @Test
    public void testFindClassImportOnDemand() throws ClassNotFoundException {
        TypeSet t = new TypeSet();
        t.addImport("java.io.*");
        Class<?> clazz = t.findClass("File");
        assertEquals(File.class, clazz);
    }

    @Test
    public void testFindClassPrimitive() throws ClassNotFoundException {
        TypeSet t = new TypeSet();
        assertEquals(int.class, t.findClass("int"));
    }

    @Test
    public void testFindClassVoid() throws ClassNotFoundException {
        TypeSet t = new TypeSet();
        assertEquals(void.class, t.findClass("void"));
    }

    @Test
    public void testFindFullyQualified() throws ClassNotFoundException {
        TypeSet t = new TypeSet();
        assertEquals(String.class, t.findClass("java.lang.String"));
        assertEquals(Set.class, t.findClass("java.util.Set"));
    }

    // inner class tests
    @Test
    public void testPrimitiveTypeResolver() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.PrimitiveTypeResolver();
        assertEquals(int.class, r.resolve("int"));
        assertEquals(byte.class, r.resolve("byte"));
        assertEquals(long.class, r.resolve("long"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testPrimitiveTypeResolverWithNull() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.PrimitiveTypeResolver();
        r.resolve(null);
    }

    @Test
    public void testVoidTypeResolver() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.VoidResolver();
        assertEquals(void.class, r.resolve("void"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testVoidTypeResolverWithNull() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.VoidResolver();
        r.resolve(null);
    }

    @Test
    public void testExplicitImportResolver() throws ClassNotFoundException {
        Set<String> imports = new HashSet<>();
        imports.add("java.io.File");
        TypeSet.Resolver r = new TypeSet.ExplicitImportResolver(pmdClassLoader, imports);
        assertEquals(File.class, r.resolve("File"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testExplicitImportResolverWithNull() throws ClassNotFoundException {
        Set<String> imports = new HashSet<>();
        imports.add("java.io.File");
        TypeSet.Resolver r = new TypeSet.ExplicitImportResolver(pmdClassLoader, imports);
        r.resolve(null);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testExplicitImportResolverWithNullAndEmptyImports() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.ExplicitImportResolver(pmdClassLoader, new HashSet<String>());
        r.resolve(null);
    }

    @Test
    public void testImplicitImportResolverPass() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.ImplicitImportResolver(pmdClassLoader);
        assertEquals(String.class, r.resolve("String"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testImplicitImportResolverPassFail() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.ImplicitImportResolver(pmdClassLoader);
        r.resolve("PMD");
    }

    @Test(expected = ClassNotFoundException.class)
    public void testImplicitImportResolverWithNull() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.ImplicitImportResolver(pmdClassLoader);
        r.resolve(null);
    }

    @Test
    public void testCurrentPackageResolverPass() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.CurrentPackageResolver(pmdClassLoader, "net.sourceforge.pmd");
        assertEquals(PMD.class, r.resolve("PMD"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testCurrentPackageResolverWithNull() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.CurrentPackageResolver(pmdClassLoader, "net.sourceforge.pmd");
        r.resolve(null);
    }

    @Test
    public void testImportOnDemandResolverPass() throws ClassNotFoundException {
        TypeSet.Resolver r = getResolver();
        assertEquals(Set.class, r.resolve("Set"));
        assertEquals(File.class, r.resolve("File"));
    }

    @Test(expected = ClassNotFoundException.class)
    public void testImportOnDemandResolverWithNull() throws ClassNotFoundException {
        TypeSet.Resolver r = getResolver();
        r.resolve(null);
    }

    @Test(expected = ClassNotFoundException.class)
    public void importOnDemandResolverFail1() throws ClassNotFoundException {
        TypeSet.Resolver r = getResolver();
        r.resolve("foo");
    }

    @Test(expected = ClassNotFoundException.class)
    public void importOnDemandResolverFail2() throws ClassNotFoundException {
        TypeSet.Resolver r = getResolver();
        r.resolve("String");
    }

    private TypeSet.Resolver getResolver() {
        Set<String> imports = new HashSet<>();
        imports.add("java.io.*");
        imports.add("java.util.*");
        TypeSet.Resolver r = new TypeSet.ImportOnDemandResolver(pmdClassLoader, imports);
        return r;
    }

    @Test(expected = ClassNotFoundException.class)
    public void testFullyQualifiedNameResolverWithNull() throws ClassNotFoundException {
        TypeSet.Resolver r = new TypeSet.FullyQualifiedNameResolver(pmdClassLoader);
        r.resolve(null);
    }
}

/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.symboltable.TypeSet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class TypeSetTest extends TestCase {

    public void testASTCompilationUnitPackage() {
        TypeSet t = new TypeSet();
        t.setASTCompilationUnitPackage("java.lang.");
        assertEquals("java.lang.", t.getASTCompilationUnitPackage());
    }

    public void testAddImport() {
        TypeSet t = new TypeSet();
        t.addImport("java.io.File");
        assertEquals(1, t.getImportsCount());
    }

    public void testFindClassImplicitImport() throws Throwable {
        TypeSet t = new TypeSet();
        Class clazz = t.findClass("String");
        assertEquals(String.class, clazz);
    }

    public void testFindClassSamePackage() throws Throwable {
        TypeSet t = new TypeSet();
        t.setASTCompilationUnitPackage("net.sourceforge.pmd.");
        Class clazz = t.findClass("PMD");
        assertEquals(PMD.class, clazz);
    }

    public void testFindClassExplicitImport() throws Throwable {
        TypeSet t = new TypeSet();
        t.addImport("java.io.File");
        Class clazz = t.findClass("File");
        assertEquals(File.class, clazz);
    }

    public void testFindClassImportOnDemand() throws Throwable {
        TypeSet t = new TypeSet();
        t.addImport("java.io.*");
        Class clazz = t.findClass("File");
        assertEquals(File.class, clazz);
    }

    public void testFindClassPrimitive() throws Throwable {
        TypeSet t = new TypeSet();
        assertEquals(int.class, t.findClass("int"));
    }

    public void testFindClassVoid() throws Throwable {
        TypeSet t = new TypeSet();
        assertEquals(void.class, t.findClass("void"));
    }

    public void testFindFullyQualified() throws Throwable {
        TypeSet t = new TypeSet();
        assertEquals(String.class, t.findClass("java.lang.String"));
        assertEquals(Set.class, t.findClass("java.util.Set"));
    }

    // inner class tests
    public void testPrimitiveTypeResolver() throws Throwable {
        TypeSet.Resolver r = new TypeSet.PrimitiveTypeResolver();
        assertEquals(int.class, r.resolve("int"));
        assertEquals(byte.class, r.resolve("byte"));
        assertEquals(long.class, r.resolve("long"));
    }

    public void testVoidTypeResolver() throws Throwable {
        TypeSet.Resolver r = new TypeSet.VoidResolver();
        assertEquals(void.class, r.resolve("void"));
    }

    public void testExplicitImportResolver() throws Throwable {
        Set imports = new HashSet();
        imports.add("java.io.File");
        TypeSet.Resolver r = new TypeSet.ExplicitImportResolver(imports);
        assertEquals(File.class, r.resolve("File"));
    }

    public void testImplicitImportResolverPass() throws Throwable {
        TypeSet.Resolver r = new TypeSet.ImplicitImportResolver();
        assertEquals(String.class, r.resolve("String"));
    }

    public void testImplicitImportResolverPassFail() throws Throwable {
        TypeSet.Resolver r = new TypeSet.ImplicitImportResolver();
        try {
            r.resolve("PMD");
            throw new RuntimeException("Should have thrown an exception");
        } catch (ClassNotFoundException cnfe) {
        }
    }

    public void testCurrentPackageResolverPass() throws Throwable {
        TypeSet.Resolver r = new TypeSet.CurrentPackageResolver("net.sourceforge.pmd.");
        assertEquals(PMD.class, r.resolve("PMD"));
    }

    public void testImportOnDemandResolverPass() throws Throwable {
        Set imports = new HashSet();
        imports.add("java.io.*");
        imports.add("java.util.*");
        TypeSet.Resolver r = new TypeSet.ImportOnDemandResolver(imports);
        assertEquals(Set.class, r.resolve("Set"));
        assertEquals(File.class, r.resolve("File"));
    }

    public void testImportOnDemandResolverFail() throws Throwable {
        Set imports = new HashSet();
        imports.add("java.io.*");
        imports.add("java.util.*");
        TypeSet.Resolver r = new TypeSet.ImportOnDemandResolver(imports);
        try {
            r.resolve("foo");
            throw new RuntimeException("Should have thrown an exception");
        } catch (ClassNotFoundException cnfe) {
        }
        try {
            r.resolve("String");
            throw new RuntimeException("Should have thrown an exception");
        } catch (ClassNotFoundException cnfe) {
        }
    }

}




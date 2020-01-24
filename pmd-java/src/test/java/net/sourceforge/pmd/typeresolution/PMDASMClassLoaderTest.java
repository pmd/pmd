/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;

public class PMDASMClassLoaderTest {

    private PMDASMClassLoader cl;

    @Before
    public void setUp() throws Exception {
        cl = PMDASMClassLoader.getInstance(getClass().getClassLoader());
    }

    @Test
    public void testLoadClassWithImportOnDemand() throws Exception {
        String className = "net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand";
        Class<?> clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        assertNotNull(imports);
        assertEquals("java.util.List", imports.get("List"));
        assertEquals("java.util.ArrayList", imports.get("ArrayList"));
        assertEquals("java.lang.Object", imports.get("Object"));
        assertEquals("net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand",
                imports.get("ClassWithImportOnDemand"));
    }

    @Test
    public void testClassWithImportInnerOnDemand() throws Exception {
        String className = "net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand";
        Class<?> clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        assertNotNull(imports);
        assertEquals("java.util.Iterator", imports.get("Iterator"));
        assertEquals("java.util.Map", imports.get("Map"));
        assertEquals("java.util.Set", imports.get("Set"));
        assertEquals("java.util.Map$Entry", imports.get("Entry"));
        assertEquals("java.util.Map$Entry", imports.get("Map$Entry"));
        assertEquals("java.lang.Object", imports.get("Object"));
        assertEquals("java.util.StringTokenizer", imports.get("StringTokenizer"));
        assertEquals("net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand",
                imports.get("ClassWithImportInnerOnDemand"));
    }

    /**
     * Unit test for bug 3546093.
     *
     * @throws Exception
     *             any error
     */
    @Test
    public void testCachingOfNotFoundClasses() throws Exception {
        MockedClassLoader mockedClassloader = new MockedClassLoader();
        PMDASMClassLoader cl = PMDASMClassLoader.getInstance(mockedClassloader);
        String notExistingClassname = "that.clazz.doesnot.Exist";
        try {
            cl.loadClass(notExistingClassname);
            fail();
        } catch (ClassNotFoundException e) {
            // expected
        }

        try {
            cl.loadClass(notExistingClassname);
            fail();
        } catch (ClassNotFoundException e) {
            // expected
        }

        assertEquals(1, mockedClassloader.findClassCalls);
    }

    private static class MockedClassLoader extends ClassLoader {
        int findClassCalls = 0;

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            findClassCalls++;
            return super.findClass(name);
        }
    }

    /**
     * With this test you can verify, how much memory could be consumed by the
     * dontBother cache.
     *
     * @throws Exception
     *             any error
     */
    @Ignore
    @Test
    public void testCachingMemoryConsumption() throws Exception {
        MockedClassLoader mockedClassLoader = new MockedClassLoader();
        PMDASMClassLoader cl = PMDASMClassLoader.getInstance(mockedClassLoader);

        Runtime runtime = Runtime.getRuntime();
        System.gc();

        long usedBytesBefore = runtime.totalMemory() - runtime.freeMemory();

        for (long i = 0; i < 3000; i++) {
            try {
                cl.loadClass("com.very.long.package.name.and.structure.MyClass" + i);
            } catch (ClassNotFoundException e) {
                // expected
            }
        }

        long usedBytesAfter = runtime.totalMemory() - runtime.freeMemory();

        System.out.println((usedBytesAfter - usedBytesBefore) / (1024.0 * 1024.0) + " mb needed");
    }
}

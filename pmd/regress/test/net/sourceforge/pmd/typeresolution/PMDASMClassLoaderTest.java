package test.net.sourceforge.pmd.typeresolution;

import net.sourceforge.pmd.typeresolution.PMDASMClassLoader;

import java.util.Map;

import junit.framework.TestCase;

public class PMDASMClassLoaderTest extends TestCase {

    private PMDASMClassLoader cl;
    protected void setUp() throws Exception {
        cl = new PMDASMClassLoader();
        super.setUp();
    }

    public void testLoadClassWithImportOnDemand() throws Exception {
        String className = "test.net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand";
        Class clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map imports = cl.getImportedClasses(className);
        assertNotNull(imports);
        assertEquals(4, imports.size());
        assertEquals("java.util.List", imports.get("List"));
        assertEquals("java.util.ArrayList", imports.get("ArrayList"));
        assertEquals("java.lang.Object", imports.get("Object"));
        assertEquals("test.net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand", imports.get("ClassWithImportOnDemand"));
    }
    
    public void testClassWithImportInnerOnDemand() throws Exception {
        String className = "test.net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand";
        Class clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map imports = cl.getImportedClasses(className);
        assertNotNull(imports);
        System.err.println(imports);
        assertEquals(8, imports.size());
        assertEquals("java.util.Iterator", imports.get("Iterator"));
        assertEquals("java.util.Map", imports.get("Map"));
        assertEquals("java.util.Set", imports.get("Set"));
        assertEquals("java.util.Map$Entry", imports.get("Entry"));
        assertEquals("java.util.Map$Entry", imports.get("Map$Entry"));
        assertEquals("java.util.Map$Entry", imports.get("Map$Entry"));
        assertEquals("java.lang.Object", imports.get("Object"));
        assertEquals("test.net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand", imports.get("ClassWithImportInnerOnDemand"));
    }
}

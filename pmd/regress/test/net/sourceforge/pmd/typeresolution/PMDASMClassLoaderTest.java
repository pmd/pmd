package test.net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.pmd.typeresolution.PMDASMClassLoader;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
public class PMDASMClassLoaderTest {

    private PMDASMClassLoader cl;
    
    @Before
    public void setUp() throws Exception {
        cl = new PMDASMClassLoader(getClass().getClassLoader());
    }

    @Test
    public void testLoadClassWithImportOnDemand() throws Exception {
        String className = "test.net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand";
        Class clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        assertNotNull(imports);
        assertEquals(4, imports.size());
        assertEquals("java.util.List", imports.get("List"));
        assertEquals("java.util.ArrayList", imports.get("ArrayList"));
        assertEquals("java.lang.Object", imports.get("Object"));
        assertEquals("test.net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand", imports.get("ClassWithImportOnDemand"));
    }
    
    @Test
    public void testClassWithImportInnerOnDemand() throws Exception {
        String className = "test.net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand";
        Class clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        assertNotNull(imports);
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

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PMDASMClassLoaderTest.class);
    }
}

package net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
public class PMDASMClassLoaderTest {

    private PMDASMClassLoader cl;
    
    @Before
    public void setUp() throws Exception {
        cl = new PMDASMClassLoader(getClass().getClassLoader());
    }

    /**
     * Determines whether clover was used. Clover will instrument the classes and therefore
     * increase the number of imports/other classes referenced by the analyzed class...
     * @param imports the map of imported classes
     * @return <code>true</code> if clover is found, <code>false</code> otherwise.
     */
    private boolean isClover(Map<String, String> imports) {
    	return imports.values().contains("com_cenqua_clover.Clover");
    }
    
    @Test
    public void testLoadClassWithImportOnDemand() throws Exception {
        String className = "net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand";
        Class<?> clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        assertNotNull(imports);
        if (isClover(imports)) {
        	assertEquals(22, imports.size());
        } else {
        	assertEquals(4, imports.size());
        }
        assertEquals("java.util.List", imports.get("List"));
        assertEquals("java.util.ArrayList", imports.get("ArrayList"));
        assertEquals("java.lang.Object", imports.get("Object"));
        assertEquals("net.sourceforge.pmd.typeresolution.ClassWithImportOnDemand", imports.get("ClassWithImportOnDemand"));
    }
    
    @Test
    public void testClassWithImportInnerOnDemand() throws Exception {
        String className = "net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand";
        Class<?> clazz = cl.loadClass(className);
        assertNotNull(clazz);
        Map<String, String> imports = cl.getImportedClasses(className);
        assertNotNull(imports);
        if (isClover(imports)) {
        	assertEquals(26, imports.size());
        } else {
        	assertEquals(8, imports.size());
        }
        assertEquals("java.util.Iterator", imports.get("Iterator"));
        assertEquals("java.util.Map", imports.get("Map"));
        assertEquals("java.util.Set", imports.get("Set"));
        assertEquals("java.util.Map$Entry", imports.get("Entry"));
        assertEquals("java.util.Map$Entry", imports.get("Map$Entry"));
        assertEquals("java.util.Map$Entry", imports.get("Map$Entry"));
        assertEquals("java.lang.Object", imports.get("Object"));
        assertEquals("net.sourceforge.pmd.typeresolution.ClassWithImportInnerOnDemand", imports.get("ClassWithImportInnerOnDemand"));
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PMDASMClassLoaderTest.class);
    }
}

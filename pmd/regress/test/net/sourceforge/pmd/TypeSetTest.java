/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 8:10:10 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.TypeSet;

public class TypeSetTest extends TestCase {
    public TypeSetTest(String name) {
        super(name);
    }

    public void testASTCompilationUnitPackage() {
        TypeSet t = new TypeSet();
        t.setASTCompilationUnitPackage("java.lang.");
        assertEquals("java.lang.", t.getASTCompilationUnitPackage());
    }

    public void testAddImport() {
        TypeSet t = new TypeSet();
        t.addImport("java.io.File");
    }

    public void testFindClass() throws Throwable {
        TypeSet t = new TypeSet();
        t.setASTCompilationUnitPackage("net.sourceforge.pmd.");
        Class clazz = t.findClass("String");
        assertEquals(String.class, clazz);
    }
}

package test.net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.typeresolution.ClassTypeResolver;

public class ClassTypeResolverTest {

	@Test
	public void testClassNameExists() {
		assertEquals(true, ClassTypeResolver.classNameExists("java.lang.System"));
		assertEquals(false, ClassTypeResolver.classNameExists("im.sure.that.this.does.not.Exist"));
		assertEquals(true, ClassTypeResolver.classNameExists("java.awt.List"));
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ClassTypeResolverTest.class);
    }
	
}

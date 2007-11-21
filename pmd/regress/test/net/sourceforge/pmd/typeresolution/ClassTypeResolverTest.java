package test.net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.typeresolution.ClassTypeResolver;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;

import java.io.StringReader;

public class ClassTypeResolverTest {

    private static final String ARRAYLIST_FOUND =
            "import java.util.ArrayList;" + PMD.EOL +
            "public class Test {" + PMD.EOL +
            "   ArrayList x;" + PMD.EOL +
            "}";

	public void testClassNameExists() {
		assertEquals(true, ClassTypeResolver.classNameExists("java.lang.System"));
		assertEquals(false, ClassTypeResolver.classNameExists("im.sure.that.this.does.not.Exist"));
		assertEquals(true, ClassTypeResolver.classNameExists("java.awt.List"));
    }

    @Test
    public void acceptanceTest() {
        ASTCompilationUnit acu = (new TargetJDK1_5().createParser(new StringReader(ARRAYLIST_FOUND)).CompilationUnit());
        ClassTypeResolver ctr = new ClassTypeResolver();
        ctr.visit(acu, new RuleContext());
        assertEquals(null, acu.getType()); // null since there's no "Test" class file out there
        ASTImportDeclaration id = acu.getFirstChildOfType(ASTImportDeclaration.class);
        assertEquals("java.util", id.getPackage().getName());
        assertEquals(java.util.ArrayList.class, id.getType());

    }


    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ClassTypeResolverTest.class);
    }
	
}

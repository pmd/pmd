/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.java.multifile.testdata.MultifileVisitorTestData2;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

/**
 * Tests of the multifile visitor.
 *
 * @author Clément Fournier
 */
public class JavaMultifileVisitorTest extends BaseNonParserTest {


    @Test
    public void testPackageStatsNotNull() {
        assertNotNull(PackageStats.INSTANCE);
    }


    @Before
    public void resetMultifile() {
        PackageStats.INSTANCE.reset();
    }


    @Test
    public void testOperationsAreThere() {
        ASTCompilationUnit acu = java.parseClass(MultifileVisitorTestData2.class);

        final ProjectMirror toplevel = PackageStats.INSTANCE;

        final JavaOperationSigMask opMask = new JavaOperationSigMask();

        // We could parse qnames from string but probably simpler to do that
        acu.jjtAccept(new JavaParserVisitorAdapter() {
            @Override
            public Object visit(ASTMethodDeclaration node, Object data) {
                assertTrue(toplevel.hasMatchingSig(node.getQualifiedName(), opMask));
                return data;
            }
        }, null);
    }


    @Test
    public void testFieldsAreThere() {
        java.parseClass(MultifileVisitorTestData2.class);

        final ProjectMirror toplevel = PackageStats.INSTANCE;

        final JavaFieldSigMask fieldSigMask = new JavaFieldSigMask();

        JavaTypeQualifiedName clazz = QualifiedNameFactory.ofClass(MultifileVisitorTestData2.class);

        String[] fieldNames = {"x", "y", "z", "t"};
        Visibility[] visibilities = {Visibility.PUBLIC, Visibility.PRIVATE, Visibility.PROTECTED, Visibility.PACKAGE};

        for (int i = 0; i < fieldNames.length; i++) {
            fieldSigMask.restrictVisibilitiesTo(visibilities[i]);
            assertTrue(toplevel.hasMatchingSig(clazz, fieldNames[i], fieldSigMask));
        }
    }


    @Test
    public void testBothClassesOperationsAreThere() {
        java.parseClass(MultifileVisitorTestData2.class);
        java.parseClass(MultifileVisitorTestData2.class);

        final ProjectMirror toplevel = PackageStats.INSTANCE;

        final JavaOperationSigMask operationSigMask = new JavaOperationSigMask();

        JavaQualifiedName clazz = QualifiedNameFactory.ofClass(MultifileVisitorTestData2.class);
        JavaQualifiedName clazz2 = QualifiedNameFactory.ofClass(MultifileVisitorTestData2.class);

        String[] opNames = {"getX()", "getY()", "setX(String)", "setY(String)",
                            "mymethod1()", "mymethod2()", "mystatic1()",
                            "mystatic2(String)", "mystatic2(String, String)", };
        Role[] roles = {Role.GETTER_OR_SETTER, Role.GETTER_OR_SETTER, Role.GETTER_OR_SETTER, Role.GETTER_OR_SETTER,
                        Role.METHOD, Role.METHOD, Role.STATIC, Role.STATIC, Role.STATIC, };


        for (int i = 0; i < opNames.length; i++) {
            operationSigMask.restrictRolesTo(roles[i]);
            JavaOperationQualifiedName name1 = (JavaOperationQualifiedName) QualifiedNameFactory.ofString(clazz.toString() + "#" + opNames[i]);
            JavaOperationQualifiedName name2 = (JavaOperationQualifiedName) QualifiedNameFactory.ofString(clazz2.toString() + "#" + opNames[i]);

            assertTrue(toplevel.hasMatchingSig(name1, operationSigMask));
            assertTrue(toplevel.hasMatchingSig(name2, operationSigMask));
        }
    }


    @Test
    public void testBothClassesFieldsAreThere() {
        java.parseClass(MultifileVisitorTestData2.class);
        java.parseClass(MultifileVisitorTestData2.class);

        final ProjectMirror toplevel = PackageStats.INSTANCE;

        final JavaFieldSigMask fieldSigMask = new JavaFieldSigMask();

        JavaTypeQualifiedName clazz = QualifiedNameFactory.ofClass(MultifileVisitorTestData2.class);
        JavaTypeQualifiedName clazz2 = QualifiedNameFactory.ofClass(MultifileVisitorTestData2.class);

        String[] fieldNames = {"x", "y", "z", "t"};
        Visibility[] visibilities = {Visibility.PUBLIC, Visibility.PRIVATE, Visibility.PROTECTED, Visibility.PACKAGE};


        for (int i = 0; i < fieldNames.length; i++) {
            fieldSigMask.restrictVisibilitiesTo(visibilities[i]);
            assertTrue(toplevel.hasMatchingSig(clazz, fieldNames[i], fieldSigMask));
            assertTrue(toplevel.hasMatchingSig(clazz2, fieldNames[i], fieldSigMask));
        }
    }


}

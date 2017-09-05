/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.ParserTstUtil;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaSignature.Visibility;
import net.sourceforge.pmd.lang.java.metrics.testdata.MetricsVisitorTestData;

/**
 * Tests of the metrics visitor.
 *
 * @author Clément Fournier
 */
public class JavaMetricsVisitorTest {


    @Test
    public void testPackageStatsNotNull() {
        assertNotNull(JavaMetrics.getFacade().getTopLevelPackageStats());
    }


    @Test
    public void testOperationsAreThere() {
        ASTCompilationUnit acu = parseAndVisitForClass(MetricsVisitorTestData.class);

        final JavaSignatureMatcher toplevel = JavaMetrics.getFacade().getTopLevelPackageStats();

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
        parseAndVisitForClass(MetricsVisitorTestData.class);


        final JavaSignatureMatcher toplevel = JavaMetrics.getFacade().getTopLevelPackageStats();

        final JavaFieldSigMask fieldSigMask = new JavaFieldSigMask();

        JavaQualifiedName clazz = JavaQualifiedName.ofString("net.sourceforge.pmd.lang.java"
                                                                  + ".metrics.testdata"
                                                                  + ".MetricsVisitorTestData");
        String[] fieldNames = {"x", "y", "z", "t"};
        Visibility[] visibilities = {Visibility.PUBLIC, Visibility.PRIVATE, Visibility.PROTECTED, Visibility.PACKAGE};

        for (int i = 0; i < fieldNames.length; i++) {
            fieldSigMask.restrictVisibilitiesTo(visibilities[i]);
            assertTrue(toplevel.hasMatchingSig(clazz, fieldNames[i], fieldSigMask));
        }
    }


    // this test is probably useless, SignatureTest and SigMaskTest already ensure signatures and sigmask have no
    // problem
    @Test
    public void testStaticOperationsSig() {
        parseAndVisitForClass(MetricsVisitorTestData.class);

        final JavaSignatureMatcher toplevel = JavaMetrics.getFacade().getTopLevelPackageStats();

        final JavaOperationSigMask operationSigMask = new JavaOperationSigMask();
        operationSigMask.restrictRolesTo(Role.STATIC);

        JavaQualifiedName q1 = JavaQualifiedName.ofString("net.sourceforge.pmd.lang.java"
                                                               + ".metrics.testdata"
                                                               + ".MetricsVisitorTestData#mystatic1()");

        assertTrue(toplevel.hasMatchingSig(q1, operationSigMask));

        operationSigMask.coverAllRoles();
        operationSigMask.forbid(Role.STATIC);

        assertFalse(toplevel.hasMatchingSig(q1, operationSigMask));
    }


    static ASTCompilationUnit parseAndVisitForClass(Class<?> clazz) {
        ASTCompilationUnit acu = ParserTstUtil.parseJavaDefaultVersion(clazz);
        LanguageVersionHandler handler = ParserTstUtil.getDefaultLanguageVersionHandler();
        handler.getTypeResolutionFacade(JavaMetricsVisitorTest.class.getClassLoader()).start(acu);
        handler.getMetricsVisitorFacade().start(acu);
        return acu;
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.metrics.signature.FieldSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.OperationSignature.Role;
import net.sourceforge.pmd.lang.java.metrics.signature.Signature.Visibility;
import net.sourceforge.pmd.lang.java.metrics.testdata.MetricsVisitorTestData;
import net.sourceforge.pmd.typeresolution.ClassTypeResolverTest;

/**
 * Tests of the metrics visitor.
 *
 * @author Clément Fournier
 */
public class JavaMetricsVisitorTest {


    @Test
    public void testPackageStatsNotNull() {
        assertNotNull(JavaMetrics.getTopLevelPackageStats());
    }


    @Test
    public void testOperationsAreThere() {
        ASTCompilationUnit acu = parseAndVisitForClass15(MetricsVisitorTestData.class);

        final JavaPackageStats toplevel = JavaMetrics.getTopLevelPackageStats();

        final OperationSigMask opMask = new OperationSigMask();

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
        parseAndVisitForClass15(MetricsVisitorTestData.class);


        final JavaPackageStats toplevel = JavaMetrics.getTopLevelPackageStats();

        final FieldSigMask fieldSigMask = new FieldSigMask();

        QualifiedName clazz = QualifiedName.parseName("net.sourceforge.pmd.lang.java"
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
        parseAndVisitForClass15(MetricsVisitorTestData.class);

        final JavaPackageStats toplevel = JavaMetrics.getTopLevelPackageStats();

        final OperationSigMask operationSigMask = new OperationSigMask();
        operationSigMask.restrictRolesTo(Role.STATIC);

        QualifiedName q1 = QualifiedName.parseName("net.sourceforge.pmd.lang.java"
                                                       + ".metrics.testdata"
                                                       + ".MetricsVisitorTestData#mystatic1()");

        assertTrue(toplevel.hasMatchingSig(q1, operationSigMask));

        operationSigMask.coverAllRoles();
        operationSigMask.forbid(Role.STATIC);

        assertFalse(toplevel.hasMatchingSig(q1, operationSigMask));
    }


    /* default */ static ASTCompilationUnit parseAndVisitForClass15(Class<?> clazz) {
        return parseAndVisitForClass(clazz, "1.5");
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you
    // _must_ have the src/test/java folder in
    // the classpath. Normally the IDE doesn't put source directories themselves
    // directly in the classpath, only
    // the output directories are in the classpath.
    private static ASTCompilationUnit parseAndVisitForClass(Class<?> clazz, String version) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ClassTypeResolverTest.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException(
                "Unable to find source file " + sourceFile + " for " + clazz);
        }
        String source;
        try {
            source = IOUtils.toString(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return parseAndVisitForString(source, version);
    }

    private static ASTCompilationUnit parseAndVisitForString(String source, String version) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                                                                        .getVersion(version).getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler
            .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(source));
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getTypeResolutionFacade(JavaMetricsVisitorTest.class.getClassLoader()).start(acu);
        languageVersionHandler.getMetricsVisitorFacade().start(acu);
        return acu;
    }
}

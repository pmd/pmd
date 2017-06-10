/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

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
import net.sourceforge.pmd.lang.java.oom.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.oom.testdata.MetricsVisitorTestClass;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature.Role;
import net.sourceforge.pmd.typeresolution.ClassTypeResolverTest;

/**
 * @author Clément Fournier
 */
public class MetricsVisitorTest {


    @Test
    public void testPackageStatsNotNull() {
        assertNotNull(Metrics.getTopLevelPackageStats());
    }

    @Test
    public void testAllOperations() {
        ASTCompilationUnit acu = parseAndVisitForClass15(MetricsVisitorTestClass.class);

        final PackageStats toplevel = Metrics.getTopLevelPackageStats();

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
    public void testStaticOperationsSig() {
        parseAndVisitForClass15(MetricsVisitorTestClass.class);

        final PackageStats toplevel = Metrics.getTopLevelPackageStats();

        final OperationSigMask opMask = new OperationSigMask();
        opMask.restrictRolesTo(Role.STATIC);

        QualifiedName q1 = QualifiedName.parseName("net.sourceforge.pmd.lang.java"
                                    + ".oom.testdata"
                                    + ".MetricsVisitorTestClass#mystatic1()");

        assertTrue(toplevel.hasMatchingSig(q1, opMask));

        opMask.coverAllRoles();
        opMask.forbid(Role.STATIC);

        assertFalse(toplevel.hasMatchingSig(q1, opMask));
    }


    private ASTCompilationUnit parseAndVisitForClass15(Class<?> clazz) {
        return parseAndVisitForClass(clazz, "1.5");
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you
    // _must_ have the src/test/java folder in
    // the classpath. Normally the IDE doesn't put source directories themselves
    // directly in the classpath, only
    // the output directories are in the classpath.
    private ASTCompilationUnit parseAndVisitForClass(Class<?> clazz, String version) {
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

    private ASTCompilationUnit parseAndVisitForString(String source, String version) {
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                                                                        .getVersion(version).getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler
            .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new StringReader(source));
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getTypeResolutionFacade(MetricsVisitorTest.class.getClassLoader()).start(acu);
        languageVersionHandler.getMetricsVisitorFacade().start(acu);
        return acu;
    }
}

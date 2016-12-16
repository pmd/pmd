/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.bugs;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.typeresolution.testdata.UsesJavaStreams;

@Ignore
public class InterfaceMethodTest {

    @Test
    public void shouldNotFail() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass(UsesJavaStreams.class);
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you
    // _must_ have the regress folder in
    // the classpath. Normally the IDE doesn't put source directories themselves
    // directly in the classpath, only
    // the output directories are in the classpath.
    private ASTCompilationUnit parseAndTypeResolveForClass(Class<?> clazz) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = InterfaceMethodTest.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException("Unable to find source file " + sourceFile + " for " + clazz);
        }
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getVersion("1.8").getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new InputStreamReader(is));
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getTypeResolutionFacade(InterfaceMethodTest.class.getClassLoader()).start(acu);
        return acu;
    }
}

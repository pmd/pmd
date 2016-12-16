/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.typeresolution.testdata.UsesJavaStreams;
import net.sourceforge.pmd.typeresolution.testdata.UsesRepeatableAnnotations;

public class ClassTypeResolverJava8Test {

    @Test
    public void interfaceMethodShouldBeParseable() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(UsesJavaStreams.class);
    }

    @Test
    public void repeatableAnnotationsMethodShouldBeParseable() {
        ASTCompilationUnit acu = parseAndTypeResolveForClass18(UsesRepeatableAnnotations.class);
    }

    private ASTCompilationUnit parseAndTypeResolveForClass18(Class<?> clazz) {
        return parseAndTypeResolveForClass(clazz, "1.8");
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you
    // _must_ have the regress folder in
    // the classpath. Normally the IDE doesn't put source directories themselves
    // directly in the classpath, only
    // the output directories are in the classpath.
    private ASTCompilationUnit parseAndTypeResolveForClass(Class<?> clazz, String version) {
        String sourceFile = clazz.getName().replace('.', '/') + ".java";
        InputStream is = ClassTypeResolverJava8Test.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException("Unable to find source file " + sourceFile + " for " + clazz);
        }
        LanguageVersionHandler languageVersionHandler = LanguageRegistry.getLanguage(JavaLanguageModule.NAME)
                .getVersion(version).getLanguageVersionHandler();
        ASTCompilationUnit acu = (ASTCompilationUnit) languageVersionHandler
                .getParser(languageVersionHandler.getDefaultParserOptions()).parse(null, new InputStreamReader(is));
        languageVersionHandler.getSymbolFacade().start(acu);
        languageVersionHandler.getTypeResolutionFacade(ClassTypeResolverJava8Test.class.getClassLoader()).start(acu);
        return acu;
    }
}

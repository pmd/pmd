/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

public abstract class STBBaseTst {

    protected ASTCompilationUnit acu;

    protected void parseCode(final String code) {
        parseCode(code, LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion());
    }

    protected void parseCode15(String code) {
        parseCode(code, LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getVersion("1.5"));
    }

    protected void parseCode(final String code, final LanguageVersion languageVersion) {
        final LanguageVersionHandler languageVersionHandler = languageVersion.getLanguageVersionHandler();
        acu = (ASTCompilationUnit) languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions())
                .parse(null, new StringReader(code));
        languageVersionHandler.getQualifiedNameResolutionFacade(STBBaseTst.class.getClassLoader()).start(acu);
        languageVersionHandler.getSymbolFacade(STBBaseTst.class.getClassLoader()).start(acu);
        languageVersionHandler.getTypeResolutionFacade(STBBaseTst.class.getClassLoader()).start(acu);
    }

    // Note: If you're using Eclipse or some other IDE to run this test, you
    // _must_ have the src/test/java folder in the classpath. Normally the IDE
    // doesn't put source directories themselves directly in the classpath, only
    // the output directories are in the classpath.
    protected void parseForClass(final Class<?> clazz) {
        final String sourceFile = clazz.getName().replace('.', '/') + ".java";
        final InputStream is = STBBaseTst.class.getClassLoader().getResourceAsStream(sourceFile);
        if (is == null) {
            throw new IllegalArgumentException("Unable to find source file " + sourceFile + " for " + clazz);
        }
        final String source;
        try {
            source = IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        parseCode(source, LanguageRegistry.getLanguage(JavaLanguageModule.NAME).getDefaultVersion());
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.tools.ant.filters.StringInputStream;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.AstAnalysisContext;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.modelica.internal.ModelicaProcessingStage;

public class ModelicaLoader {
    private ModelicaLoader() {
    }

    public static ASTStoredDefinition parse(final String resourceName) {
        final InputStream inputStream = ModelicaLoader.class.getResourceAsStream(resourceName);
        return parse(resourceName, new InputStreamReader(inputStream));
    }

    public static ASTStoredDefinition parse(final String fileName, final String contents) {
        final InputStream inputStream = new StringInputStream(contents);
        return parse(fileName, new InputStreamReader(inputStream));
    }

    public static ASTStoredDefinition parse(final String fileName, Reader source) {
        LanguageVersion lversion = LanguageRegistry.getLanguage(ModelicaLanguageModule.NAME).getDefaultVersion();
        final LanguageVersionHandler modelicaLang = lversion.getLanguageVersionHandler();
        final Parser parser = modelicaLang.getParser(modelicaLang.getDefaultParserOptions());
        final ASTStoredDefinition node = (ASTStoredDefinition) parser.parse(fileName, source);
        AstAnalysisContext ctx = new AstAnalysisContext() {
            @Override
            public ClassLoader getTypeResolutionClassLoader() {
                return ModelicaLoader.class.getClassLoader();
            }

            @Override
            public LanguageVersion getLanguageVersion() {
                return lversion;
            }
        };
        ModelicaProcessingStage.SYMBOL_RESOLUTION.processAST(node, ctx);
        return node;
    }
}

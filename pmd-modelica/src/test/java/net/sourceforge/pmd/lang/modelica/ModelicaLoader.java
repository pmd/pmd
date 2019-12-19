/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.tools.ant.filters.StringInputStream;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;

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
        final LanguageVersionHandler modelicaLang = LanguageRegistry.getLanguage(ModelicaLanguageModule.NAME)
                .getDefaultVersion()
                .getLanguageVersionHandler();
        final Parser parser = modelicaLang.getParser(modelicaLang.getDefaultParserOptions());
        final Node node = parser.parse(fileName, source);
        modelicaLang.getSymbolFacade().start(node);
        return (ASTStoredDefinition) node;
    }
}

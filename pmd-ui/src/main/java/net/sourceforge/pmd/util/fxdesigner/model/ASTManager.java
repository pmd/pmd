/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.io.StringReader;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;


/**
 * Main class of the model. Manages a compilation unit.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ASTManager {

    private final DesignerRoot designerRoot;

    /**
     * Last valid source that was compiled, corresponds to {@link #compilationUnit}.
     */
    private String lastValidSource;
    /**
     * Last language version used.
     */
    private LanguageVersion lastLanguageVersion;
    /**
     * Most up-to-date compilation unit. Is null if the current source cannot be parsed.
     */
    private final Var<Node> compilationUnit = Var.newSimpleVar(null);
    /**
     * Selected language version.
     */
    private final Var<LanguageVersion> languageVersion = Var.newSimpleVar(LanguageRegistry.getDefaultLanguage().getDefaultVersion());


    public ASTManager(DesignerRoot owner) {
        this.designerRoot = owner;
    }


    public LanguageVersion getLanguageVersion() {
        return languageVersion.getValue();
    }


    public void setLanguageVersion(LanguageVersion version) {
        languageVersion.setValue(version);
    }


    public Var<LanguageVersion> languageVersionProperty() {
        return languageVersion;
    }


    public Optional<Node> getCompilationUnit() {
        return compilationUnit.getOpt();
    }


    /**
     * Refreshes the compilation unit given the current parameters of the model.
     *
     * @param source Source code
     *
     * @throws ParseAbortedException if parsing fails and cannot recover
     */
    public Optional<Node> updateIfChanged(String source, ClassLoader classLoader) throws ParseAbortedException {
        if (compilationUnit.isPresent()
                && getLanguageVersion().equals(lastLanguageVersion)
                && StringUtils.equals(source, lastValidSource)) {
            return getCompilationUnit();
        }
        LanguageVersionHandler languageVersionHandler = getLanguageVersion().getLanguageVersionHandler();
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());

        Node node;
        try {
            node = parser.parse(null, new StringReader(source));
        } catch (Exception e) {
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.PARSE_EXCEPTION));
            compilationUnit.setValue(null);
            throw new ParseAbortedException(e);
        }
        try {
            languageVersionHandler.getSymbolFacade().start(node);
        } catch (Exception e) {
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.SYMBOL_FACADE_EXCEPTION));
        }
        try {
            languageVersionHandler.getQualifiedNameResolutionFacade(classLoader).start(node);
        } catch (Exception e) {
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.QNAME_RESOLUTION_EXCEPTION));
        }

        try {
            languageVersionHandler.getTypeResolutionFacade(classLoader).start(node);
        } catch (Exception e) {
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.TYPERESOLUTION_EXCEPTION));
        }

        compilationUnit.setValue(node);
        lastValidSource = source;
        lastLanguageVersion = getLanguageVersion();

        // Notify that the parse went OK so we can avoid logging very recent exceptions
        designerRoot.getLogger().logEvent(new LogEntry(null, Category.PARSE_OK));

        return getCompilationUnit();
    }


}

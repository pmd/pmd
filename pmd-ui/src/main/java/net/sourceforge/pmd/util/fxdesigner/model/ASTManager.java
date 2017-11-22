/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.model.LogEntry.Category;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


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
     * Latest computed compilation unit (only null before the first call to {@link #updateCompilationUnit(String)})
     */
    private ObjectProperty<Node> compilationUnit = new SimpleObjectProperty<>();
    /**
     * Selected language version.
     */
    private ObjectProperty<LanguageVersion> languageVersion = new SimpleObjectProperty<>();

    {
        languageVersion.setValue(LanguageRegistry.findLanguageVersionByTerseName("java 8"));
    }


    public ASTManager(DesignerRoot owner) {
        this.designerRoot = owner;
    }


    public LanguageVersion getLanguageVersion() {
        return languageVersion.get();
    }


    public ObjectProperty<LanguageVersion> languageVersionProperty() {
        return languageVersion;
    }


    public Node updateCompilationUnit() {
        return compilationUnit.get();
    }


    public ObjectProperty<Node> compilationUnitProperty() {
        return compilationUnit;
    }


    /**
     * Refreshes the compilation unit given the current parameters of the model.
     *
     * @param source Source code
     * @throws ParseAbortedException if parsing fails and cannot recover
     */
    public Node updateCompilationUnit(String source) throws ParseAbortedException {
        if (compilationUnit.get() != null
            && languageVersion.get().equals(lastLanguageVersion) && StringUtils.equals(source, lastValidSource)) {
            return compilationUnit.get();
        }
        LanguageVersionHandler languageVersionHandler = languageVersion.get().getLanguageVersionHandler();
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());

        Node node;
        try {
            node = parser.parse(null, new StringReader(source));
        } catch (Exception e) {
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.PARSE_EXCEPTION));
            throw new ParseAbortedException(e);
        }
        try {
            languageVersionHandler.getSymbolFacade().start(node);
        } catch (Exception e) {
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.SYMBOL_FACADE_EXCEPTION));
        }
        try {
            languageVersionHandler.getTypeResolutionFacade(ASTManager.class.getClassLoader()).start(node);
        } catch (Exception e) {
            designerRoot.getLogger().logEvent(new LogEntry(e, Category.TYPERESOLUTION_EXCEPTION));
        }

        compilationUnit.setValue(node);
        lastValidSource = source;
        lastLanguageVersion = languageVersion.get();
        return compilationUnit.get();


    }


}

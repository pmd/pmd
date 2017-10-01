/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.io.StringReader;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.Designer;
import net.sourceforge.pmd.util.fxdesigner.util.LogEntry;
import net.sourceforge.pmd.util.fxdesigner.util.LogEntry.Category;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Main class of the model. Manages the compilation unit and evaluation logic.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ASTManager {

    /** Evaluates XPath queries. */
    private final XPathEvaluator xpathEvaluator = new XPathEvaluator();
    /** Evaluates metrics on a node. */
    private final MetricEvaluator metricEvaluator = new MetricEvaluator();
    /** Last valid source that was compiled, corresponds to {@link #compilationUnit}. */
    private String lastValidSource;
    /** Last language version used. */
    private LanguageVersion lastLanguageVersion;
    /** Latest computed compilation unit (only null before the first call to {@link #getCompilationUnit(String)}) */
    private ObjectProperty<Node> compilationUnit = new SimpleObjectProperty<>();
    /** Selected language version. */
    private ObjectProperty<LanguageVersion> languageVersion = new SimpleObjectProperty<>();


    public LanguageVersion getLanguageVersion() {
        return languageVersion.get();
    }


    public ObjectProperty<LanguageVersion> languageVersionProperty() {
        return languageVersion;
    }


    public Node getCompilationUnit() {
        return compilationUnit.get();
    }


    public ObjectProperty<Node> compilationUnitProperty() {
        return compilationUnit;
    }


    public StringProperty xpathVersionProperty() {
        return xpathEvaluator.xpathVersionProperty();
    }


    public String getXPathVersion() {
        return xpathEvaluator.getXpathVersion();
    }


    /**
     * Evaluates an XPath query, returns the matching nodes.
     *
     * @param xpathQuery Query to execute
     *
     * @return List of the matching nodes, never null.
     *
     * @throws XPathEvaluationException if there was an error during the evaluation. The cause is preserved.
     */
    public ObservableList<Node> evaluateXPath(String xpathQuery) throws XPathEvaluationException {
        return FXCollections.observableArrayList(xpathEvaluator.evaluateQuery(compilationUnit.get(),
                                                                              languageVersion.get(),
                                                                              xpathQuery));
    }


    /**
     * Returns true if the source must be recompiled.
     *
     * @param source Source to test
     *
     * @return true if the current AST does not correspond to the parameter source
     */
    public boolean isRecompilationNeeded(String source) {
        return !StringUtils.equals(source, lastValidSource)
            || !languageVersion.get().equals(lastLanguageVersion);
    }


    /**
     * Refreshes the compilation unit given the current parameters of the model.
     *
     * @param source Source code
     *
     * @throws ParseAbortedException if parsing fails and cannot recover
     */
    public Node getCompilationUnit(String source) throws ParseAbortedException {
        if (languageVersion.get().equals(lastLanguageVersion)
            && StringUtils.equals(source, lastValidSource)) {
            return compilationUnit.get();
        }
        LanguageVersionHandler languageVersionHandler = languageVersion.get().getLanguageVersionHandler();
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());

        Node node;
        try {
            node = parser.parse(null, new StringReader(source));
        } catch (Exception e) {
            Designer.instance().getLogger().logEvent(new LogEntry(e, Category.PARSE_EXCEPTION));
            throw new ParseAbortedException(e);
        }
        try {
            languageVersionHandler.getSymbolFacade().start(node);
        } catch (Exception e) {
            Designer.instance().getLogger().logEvent(new LogEntry(e, Category.SYMBOL_FACADE_EXCEPTION));
            throw new ParseAbortedException(e);
        }
        try {
            languageVersionHandler.getTypeResolutionFacade(ASTManager.class.getClassLoader()).start(node);
        } catch (Exception e) {
            Designer.instance().getLogger().logEvent(new LogEntry(e, Category.TYPERESOLUTION_EXCEPTION));
        }

        compilationUnit.setValue(node);
        lastValidSource = source;
        lastLanguageVersion = languageVersion.get();
        return compilationUnit.get();


    }


}

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

    /** Last valid source that was compiled, corresponds to {@link #compilationUnit}. */
    private String lastValidSource;
    /** Last language version used. */
    private LanguageVersion lastLanguageVersion;

    /** Latest computed compilation unit (only null before the first call to {@link #getCompilationUnit(String)}) */
    private Node compilationUnit;

    /** Selected language version. */
    private ObjectProperty<LanguageVersion> languageVersion = new SimpleObjectProperty<>();

    /** Evaluates XPath queries. */
    private XPathEvaluator xpathEvaluator = new XPathEvaluator();

    /** Evaluates metrics on a node. */
    private MetricEvaluator metricEvaluator = new MetricEvaluator();


    public LanguageVersion getLanguageVersion() {
        return languageVersion.get();
    }


    public ObjectProperty<LanguageVersion> languageVersionProperty() {
        return languageVersion;
    }


    public StringProperty xpathVersionProperty() {
        return xpathEvaluator.xpathVersionProperty();
    }


    public String getXPathVersion() {
        return xpathEvaluator.getXpathVersion();
    }


    /**
     * Evaluates all available metrics for that node.
     *
     * @param n Node
     *
     * @return A list of all the metric results that could be computed, possibly with some Double.NaN results
     */
    public ObservableList<MetricResult> evaluateAllMetrics(Node n) {
        try {
            return FXCollections.observableArrayList(metricEvaluator.evaluateAllMetrics(n));
        } catch (UnsupportedOperationException e) {
            return FXCollections.emptyObservableList();
        }
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
        return FXCollections.observableArrayList(xpathEvaluator.evaluateQuery(compilationUnit,
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
     * @throws ParseTimeException if parsing or one of the visitors fails. The cause is preserved.
     */
    public Node getCompilationUnit(String source) throws ParseTimeException {
        if (StringUtils.equals(source, lastValidSource)
            && languageVersion.get().equals(lastLanguageVersion)) {
            return compilationUnit;
        }
        LanguageVersionHandler languageVersionHandler = languageVersion.get().getLanguageVersionHandler();
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
        try {
            Node node = parser.parse(null, new StringReader(source));
            languageVersionHandler.getSymbolFacade().start(node);
            try {
                languageVersionHandler.getTypeResolutionFacade(ASTManager.class.getClassLoader()).start(node);
            } catch (Exception e) {
                e.printStackTrace();
            }
            languageVersionHandler.getMetricsVisitorFacade().start(node);
            compilationUnit = node;
            lastValidSource = source;
            lastLanguageVersion = languageVersion.get();
            return compilationUnit;
        } catch (Exception e) {
            throw new ParseTimeException(e);
        }

    }


}

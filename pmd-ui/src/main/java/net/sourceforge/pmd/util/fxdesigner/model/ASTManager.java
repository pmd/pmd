/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.io.StringReader;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.Designer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Main class of the model.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ASTManager {

    /** Source code text, bound to the view. */
    private StringProperty sourceCode = new SimpleStringProperty();

    /** Selected language version. */
    private ObjectProperty<LanguageVersion> languageVersion = new SimpleObjectProperty<>();

    /** Evaluates XPath queries and stores the results. */
    private XPathEvaluator xpathEvaluator = new XPathEvaluator();

    /** Latest computed compilation unit (only null before the first call to {@link #getCompilationUnit()}) */
    private Node compilationUnit;


    public LanguageVersion getLanguageVersion() {
        return languageVersion.get();
    }


    public ObjectProperty<LanguageVersion> languageVersionProperty() {
        return languageVersion;
    }


    public String getSourceCode() {
        return sourceCode.get();
    }


    public StringProperty sourceCodeProperty() {
        return sourceCode;
    }


    public StringProperty xpathVersionProperty() {
        return xpathEvaluator.xpathVersionProperty();
    }


    public String getXPathVersion() {
        return xpathEvaluator.xpathVersionProperty().get();
    }


    /**
     * Evaluates an XPath request, returns the matching nodes.
     *
     * @param xpathQuery Query to execute
     *
     * @return The matching nodes, or Optional.empty if the compilation unit is invalid.
     *
     * @throws XPathEvaluationException if there was an error during the evaluation. The cause is preserved.
     */
    public ObservableList<Node> evaluateXPath(String xpathQuery) throws XPathEvaluationException {
        return FXCollections.observableArrayList(xpathEvaluator.evaluateQuery(compilationUnit,
                                                                              languageVersion.get(),
                                                                              xpathQuery));
    }


    /**
     * Refreshes the compilation unit given the current parameters of the model.
     *
     * @throws ParseTimeException if parsing or one of the visitors fails. The cause is preserved.
     */
    public Node getCompilationUnit() throws ParseTimeException {
        LanguageVersionHandler languageVersionHandler = languageVersion.get().getLanguageVersionHandler();
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
        try {
            Node node = parser.parse(null, new StringReader(sourceCode.get()));
            languageVersionHandler.getSymbolFacade().start(node);
            languageVersionHandler.getTypeResolutionFacade(Designer.class.getClassLoader()).start(node);
            compilationUnit = node;
            return compilationUnit;
        } catch (Exception e) {
            throw new ParseTimeException(e);
        }

    }


}

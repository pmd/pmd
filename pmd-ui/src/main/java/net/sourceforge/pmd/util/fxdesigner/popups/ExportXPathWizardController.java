/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.popups;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting.XmlSyntaxHighlighter;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ExportXPathWizardController implements Initializable {

    private final ObservableValue<String> xpathExpression;
    Map<ObservableValue<?>, ChangeListener<Object>> registeredListeners = new WeakHashMap<>();
    @FXML
    private CustomCodeArea exportResultArea;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField messageField;
    @FXML
    private Slider prioritySlider;
    @FXML
    private ChoiceBox<Language> languageChoiceBox;
    @FXML
    private TextField nameField;


    public ExportXPathWizardController(ObservableValue<String> xpathExpression) {
        this.xpathExpression = xpathExpression;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        languageChoiceBox.getItems().addAll(DesignerUtil.getSupportedLanguageVersions()
                                                        .stream()
                                                        .map(LanguageVersion::getLanguage)
                                                        .distinct()
                                                        .collect(Collectors.toList()));

        languageChoiceBox.setConverter(new StringConverter<Language>() {
            @Override
            public String toString(Language object) {
                return object.getTerseName();
            }


            @Override
            public Language fromString(String string) {
                return LanguageRegistry.findLanguageByTerseName(string);
            }
        });

        languageChoiceBox.getSelectionModel().select(LanguageRegistry.getDefaultLanguage());

        exportResultArea.setSyntaxHighlighter(new XmlSyntaxHighlighter());

        registerListener(nameField.textProperty(), updateResultListener());
        registerListener(messageField.textProperty(), updateResultListener());
        registerListener(descriptionField.textProperty(), updateResultListener());
        registerListener(prioritySlider.valueProperty(), updateResultListener());
        registerListener(languageChoiceBox.getSelectionModel().selectedItemProperty(), updateResultListener());
        registerListener(xpathExpression, updateResultListener());
        updateResultListener().changed(null, null, null);
    }


    public void shutdown() {
        registeredListeners.entrySet().stream()
                           .filter(e -> e.getKey() != null)
                           .forEach(e -> e.getKey().removeListener(e.getValue()));
        exportResultArea.disableSyntaxHighlighting();
    }


    private <T> void registerListener(ObservableValue<T> value, ChangeListener<Object> listener) {
        ChangeListener<Object> previous = registeredListeners.put(value, listener);
        if (previous != null) {
            value.removeListener(previous);
        }

        value.addListener(listener);
    }


    private ChangeListener<Object> updateResultListener() {
        return (observable, oldValue, newValue) -> exportResultArea.replaceText(getUpToDateRuleElement());
    }


    private String getUpToDateRuleElement() {
        // TODO very inefficient, can we do better?

        final String template = "<rule name=\"%s\"\n"
                + "      language=\"%s\"\n"
                + "      message=\"%s\"\n"
                + "      class=\"net.sourceforge.pmd.lang.rule.XPathRule\"\n"
                + "      <!-- externalInfoUrl=\"%s\"--> >\n"
                + "    <description>\n"
                + "%s\n"
                + "    </description>\n"
                + "    <priority>%d</priority>\n"
                + "    <properties>\n"
                + "        <property name=\"xpath\">\n"
                + "            <value>\n"
                + "<![CDATA[\n"
                + "%s\n"
                + "]]>\n"
                + "            </value>\n"
                + "        </property>\n"
                + "    </properties>\n"
                + "    <!--<example><![CDATA[]]></example>-->\n"
                + "</rule>";

        return String.format(template,
                             nameField.getText(),
                             languageChoiceBox.getSelectionModel().getSelectedItem().getTerseName(),
                             messageField.getText(),
                             "TODO",
                             descriptionField.getText(), // TODO format
                             (int) prioritySlider.getValue(),
                             xpathExpression.getValue()
        );
    }

}

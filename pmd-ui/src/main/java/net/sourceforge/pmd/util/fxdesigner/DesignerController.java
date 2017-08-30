/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.designer.Designer;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

/**
 * @author Clément Fournier
 */
public class DesignerController implements Initializable {

    @FXML
    public TextArea codeEditorArea;

    @FXML
    private Menu languageMenu;

    @FXML
    private Button refreshASTButton;


    private Node compilationUnit;
    private LanguageVersion selectedLanguageVersion;
    private Map<LanguageVersion, RadioMenuItem> languageRadioMenuMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ASTTreeNode.initialize(this);
        initializeLanguageVersionMenu();
    }


    private void initializeLanguageVersionMenu() {
        LanguageVersion[] supported = DesignerUtil.getSupportedLanguageVersions();
        ObservableList<MenuItem> items = languageMenu.getItems();
        ToggleGroup group = new ToggleGroup();

        for (LanguageVersion version : supported) {
            RadioMenuItem item = new RadioMenuItem(version.getShortName());
            item.setToggleGroup(group);
            items.add(item);
            languageRadioMenuMap.put(version, item);
        }

        selectedLanguageVersion = LanguageRegistry.getLanguage("Java").getDefaultVersion();
        languageRadioMenuMap.get(selectedLanguageVersion).setSelected(true);

        languageMenu.show();

    }


    private Node getCompilationUnit() throws Exception {
        LanguageVersionHandler languageVersionHandler = getLanguageVersionHandler();
        return getCompilationUnit(languageVersionHandler, codeEditorArea.getText());
    }


    LanguageVersionHandler getLanguageVersionHandler() {
        return selectedLanguageVersion.getLanguageVersionHandler();
    }


    public void onRefreshASTClicked(ActionEvent event) {
        try {
            compilationUnit = getCompilationUnit();
        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("An exception occurred:");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }
    }


    private static Node getCompilationUnit(LanguageVersionHandler languageVersionHandler, String code)
        throws Exception {
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
        Node node = parser.parse(null, new StringReader(code));
        languageVersionHandler.getSymbolFacade().start(node);
        languageVersionHandler.getTypeResolutionFacade(Designer.class.getClassLoader()).start(node);
        return node;
    }

}

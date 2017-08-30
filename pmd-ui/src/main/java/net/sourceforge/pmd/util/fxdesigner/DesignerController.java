/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;

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
    @FXML
    private TextArea xpathExpressionArea;
    @FXML
    private ListView<Node> xpathResultListView;
    @FXML
    private Label xpathResultLabel;
    @FXML
    private TreeView<Node> astTreeView;


    private LanguageVersion selectedLanguageVersion;
    private Map<LanguageVersion, RadioMenuItem> languageRadioMenuMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ASTTreeNode.initialize(this);
        initializeLanguageVersionMenu();
        astTreeView.setCellFactory(param -> new ASTTreeCell());

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


    /**
     * Refresh the AST view with the updated code.
     *
     * @param event ActionEvent
     */
    @FXML
    public void onRefreshASTClicked(ActionEvent event) {
        Node n = null;
        try {
            n = getCompilationUnit();
        } catch (Exception e) {
            Alert errorAlert = new Alert(AlertType.ERROR);
            errorAlert.setHeaderText("An exception occurred:");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
        }

        if (n != null) {
            ASTTreeItem root = ASTTreeItem.getRoot(n);
            root.expandAll();
            astTreeView.setRoot(root);
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

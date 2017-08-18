/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.fxdesigner;

import java.io.StringReader;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.designer.Designer;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

/**
 * @author Clément Fournier
 */
class DesignerController implements Initializable {

    @FXML
    private Menu languageMenu;


    private LanguageVersion selectedLanguageVersion;
    private Map<LanguageVersion, RadioMenuItem> languageRadioMenuMap;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ASTTreeNode.initialize(this);
        initializeLanguageVersionMenu();
    }


    private void initializeLanguageVersionMenu() {
        LanguageVersion[] supported = DesignerUtil.getSupportedLanguageVersions();
        ObservableList<MenuItem> items = languageMenu.getItems();
        assert items.size() == 0;

        System.out.println(supported);
        ToggleGroup group = new ToggleGroup();

        for (LanguageVersion version : supported) {
            RadioMenuItem item = new RadioMenuItem(version.getShortName());
            items.add(item);
            languageRadioMenuMap.put(version, item);
        }

        selectedLanguageVersion = LanguageRegistry.getLanguage("Java").getDefaultVersion();
        languageRadioMenuMap.get(selectedLanguageVersion).setSelected(true);

        languageMenu.show();

    }


    private Node getCompilationUnit() {
        LanguageVersionHandler languageVersionHandler = getLanguageVersionHandler();
        return getCompilationUnit(languageVersionHandler);
    }


    private Node getCompilationUnit(LanguageVersionHandler languageVersionHandler) {
        return null; //getCompilationUnit(languageVersionHandler, codeEditorPane.getText());
    }


    private LanguageVersion getLanguageVersion() {
        return DesignerUtil.getSupportedLanguageVersions()[selectedLanguageVersionIndex()];
    }


    private void setLanguageVersion(LanguageVersion languageVersion) {
        if (languageVersion != null) {
            LanguageVersion[] versions = DesignerUtil.getSupportedLanguageVersions();
            for (int i = 0; i < versions.length; i++) {
                LanguageVersion version = versions[i];
                if (languageVersion.equals(version)) {
                    //        languageVersionMenuItems[i].setSelected(true);
                    break;
                }
            }
        }
    }


    private int selectedLanguageVersionIndex() {
        /*  for (int i = 0; i < languageVersionMenuItems.length; i++) {
            if (languageVersionMenuItems[i].isSelected()) {
                return i;
            }
        }
        */
        throw new RuntimeException("Initial default language version not specified");
    }


    LanguageVersionHandler getLanguageVersionHandler() {
        LanguageVersion languageVersion = getLanguageVersion();
        return languageVersion.getLanguageVersionHandler();
    }


    static Node getCompilationUnit(LanguageVersionHandler languageVersionHandler, String code) {
        Parser parser = languageVersionHandler.getParser(languageVersionHandler.getDefaultParserOptions());
        Node node = parser.parse(null, new StringReader(code));
        languageVersionHandler.getSymbolFacade().start(node);
        languageVersionHandler.getTypeResolutionFacade(Designer.class.getClassLoader()).start(node);
        return node;
    }


}

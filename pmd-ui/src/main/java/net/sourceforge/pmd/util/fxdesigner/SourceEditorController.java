/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.ClasspathClassLoader;
import net.sourceforge.pmd.util.fxdesigner.app.AbstractController;
import net.sourceforge.pmd.util.fxdesigner.app.CompositeSelectionSource;
import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource;
import net.sourceforge.pmd.util.fxdesigner.model.ASTManager;
import net.sourceforge.pmd.util.fxdesigner.model.ParseAbortedException;
import net.sourceforge.pmd.util.fxdesigner.popups.AuxclasspathSetupController;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.TextAwareNodeWrapper;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ASTTreeItem;
import net.sourceforge.pmd.util.fxdesigner.util.controls.AstTreeView;
import net.sourceforge.pmd.util.fxdesigner.util.controls.NodeEditionCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ToolbarTitledPane;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;


/**
 * One editor, i.e. source editor and ast tree view. The {@link NodeEditionCodeArea} handles the
 * presentation of different types of nodes in separate layers. This class aggregates the event
 * streams of its controls and handles configuration, language selection and such.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SourceEditorController extends AbstractController<MainDesignerController> implements CompositeSelectionSource {

    private static final Duration AST_REFRESH_DELAY = Duration.ofMillis(100);
    private final ASTManager astManager;
    private final Var<List<File>> auxclasspathFiles = Var.newSimpleVar(emptyList());
    private final Val<ClassLoader> auxclasspathClassLoader = auxclasspathFiles.map(fileList -> {
        try {
            return new ClasspathClassLoader(fileList, SourceEditorController.class.getClassLoader());
        } catch (IOException e) {
            e.printStackTrace();
            return SourceEditorController.class.getClassLoader();
        }
    });
    @FXML
    private ToolbarTitledPane astTitledPane;
    @FXML
    private ToolbarTitledPane editorTitledPane;
    @FXML
    private MenuButton languageSelectionMenuButton;
    @FXML
    private AstTreeView astTreeView;
    @FXML
    private NodeEditionCodeArea nodeEditionCodeArea;
    private Var<LanguageVersion> languageVersionUIProperty;


    public SourceEditorController(MainDesignerController mainController) {
        super(mainController);
        astManager = new ASTManager(mainController.getDesignerRoot());

    }


    @Override
    protected void beforeParentInit() {

        astTreeView.setDesignerRoot(getDesignerRoot());
        nodeEditionCodeArea.setDesignerRoot(getDesignerRoot());

        initializeLanguageSelector(); // languageVersionProperty() must be initialized

        languageVersionProperty().values()
                                 .filterMap(Objects::nonNull, LanguageVersion::getLanguage)
                                 .distinct()
                                 .subscribe(nodeEditionCodeArea::updateSyntaxHighlighter);

        languageVersionProperty().values()
                                 .filter(Objects::nonNull)
                                 .map(LanguageVersion::getShortName)
                                 .map(lang -> "Source Code (" + lang + ")")
                                 .subscribe(editorTitledPane::setTitle);

        nodeEditionCodeArea.plainTextChanges()
                           .filter(t -> !t.isIdentity())
                           .successionEnds(AST_REFRESH_DELAY)
                           // Refresh the AST anytime the text, classloader, or language version changes
                           .or(auxclasspathClassLoader.changes())
                           .or(languageVersionProperty().changes())
                           .subscribe(tick -> {
                               // Discard the AST if the language version has changed
                               tick.ifRight(c -> astTreeView.setRoot(null));
                               Platform.runLater(parent::refreshAST);
                           });


    }


    @Override
    protected void afterParentInit() {
        DesignerUtil.rewire(astManager.languageVersionProperty(), languageVersionUIProperty);
        nodeEditionCodeArea.moveCaret(0, 0);
    }


    private void initializeLanguageSelector() {

        ToggleGroup languageToggleGroup = new ToggleGroup();

        DesignerUtil.getSupportedLanguageVersions()
                    .stream()
                    .sorted(LanguageVersion::compareTo)
                    .map(lv -> {
                        RadioMenuItem item = new RadioMenuItem(lv.getShortName());
                        item.setUserData(lv);
                        return item;
                    })
                    .forEach(item -> {
                        languageToggleGroup.getToggles().add(item);
                        languageSelectionMenuButton.getItems().add(item);
                    });

        languageVersionUIProperty = DesignerUtil.mapToggleGroupToUserData(languageToggleGroup, DesignerUtil::defaultLanguageVersion);
    }


    @Override
    public ObservableSet<? extends NodeSelectionSource> getSubSelectionSources() {
        return FXCollections.observableSet(nodeEditionCodeArea, astTreeView);
    }


    /**
     * Refreshes the AST and returns the new compilation unit if the parse didn't fail.
     */
    public Optional<Node> refreshAST() {
        String source = getText();

        if (StringUtils.isBlank(source)) {
            astTreeView.setRoot(null);
            return Optional.empty();
        }

        Optional<Node> current;

        try {
            current = astManager.updateIfChanged(source, auxclasspathClassLoader.getValue());
        } catch (ParseAbortedException e) {
            astTitledPane.setTitle("Abstract syntax tree (error)");
            return Optional.empty();
        }

        current.ifPresent(this::setUpToDateCompilationUnit);
        return current;
    }


    public void showAuxclasspathSetupPopup() {
        new AuxclasspathSetupController(getDesignerRoot()).show(getMainStage(), auxclasspathFiles.getValue(), auxclasspathFiles::setValue);
    }


    private void setUpToDateCompilationUnit(Node node) {
        parent.invalidateAst();
        astTitledPane.setTitle("Abstract syntax tree");
        ASTTreeItem root = ASTTreeItem.getRoot(node);
        astTreeView.setRoot(root);
    }

    public Var<List<Node>> currentRuleResultsProperty() {
        return nodeEditionCodeArea.currentRuleResultsProperty();
    }


    public Var<List<Node>> currentErrorNodesProperty() {
        return nodeEditionCodeArea.currentErrorNodesProperty();
    }


    public TextAwareNodeWrapper wrapNode(Node node) {
        return nodeEditionCodeArea.wrapNode(node);
    }


    @PersistentProperty
    public LanguageVersion getLanguageVersion() {
        return languageVersionUIProperty.getValue();
    }


    public void setLanguageVersion(LanguageVersion version) {
        languageVersionUIProperty.setValue(version);
    }


    public Var<LanguageVersion> languageVersionProperty() {
        return languageVersionUIProperty;
    }


    /**
     * Returns the most up-to-date compilation unit, or empty if it can't be parsed.
     */
    public Optional<Node> getCompilationUnit() {
        return astManager.getCompilationUnit();
    }


    @PersistentProperty
    public String getText() {
        return nodeEditionCodeArea.getText();
    }


    public void setText(String expression) {
        nodeEditionCodeArea.replaceText(expression);
    }


    public Val<String> textProperty() {
        return Val.wrap(nodeEditionCodeArea.textProperty());
    }


    @PersistentProperty
    public String getAuxclasspathFiles() {
        return auxclasspathFiles.getValue().stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));
    }


    public void setAuxclasspathFiles(String files) {
        List<File> newVal = Arrays.stream(files.split(File.pathSeparator)).map(File::new).collect(Collectors.toList());
        auxclasspathFiles.setValue(newVal);
    }


    @Override
    public String getDebugName() {
        return "editor";
    }
}

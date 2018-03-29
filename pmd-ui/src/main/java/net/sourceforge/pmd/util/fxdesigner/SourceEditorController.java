/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.EventStreams;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.model.ASTManager;
import net.sourceforge.pmd.util.fxdesigner.model.ParseAbortedException;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.AvailableSyntaxHighlighters;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.CustomCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.SyntaxHighlighter;
import net.sourceforge.pmd.util.fxdesigner.util.controls.ASTTreeItem;
import net.sourceforge.pmd.util.fxdesigner.util.controls.TreeViewWrapper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;


/**
 * One editor, i.e. source editor and ast tree view.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SourceEditorController implements Initializable, SettingsOwner {
    private final MainDesignerController parent;

    @FXML
    private Label astTitleLabel;
    @FXML
    private TreeView<Node> astTreeView;
    @FXML
    private CustomCodeArea codeEditorArea;

    private ASTManager astManager;
    private TreeViewWrapper<Node> treeViewWrapper;


    public SourceEditorController(DesignerRoot owner, MainDesignerController mainController) {
        parent = mainController;
        astManager = new ASTManager(owner);

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        treeViewWrapper = new TreeViewWrapper<>(astTreeView);

        languageVersionProperty().values()
                                 .filterMap(Objects::nonNull, LanguageVersion::getLanguage)
                                 .distinct()
                                 .subscribe(this::updateSyntaxHighlighter);

        EventStreams.valuesOf(astTreeView.getSelectionModel().selectedItemProperty())
                    .filterMap(Objects::nonNull, TreeItem::getValue)
                    .subscribe(parent::onNodeItemSelected);

        codeEditorArea.setParagraphGraphicFactory(LineNumberFactory.get(codeEditorArea));
    }


    /**
     * Refreshes the AST.
     */
    public void refreshAST() {
        String source = getText();
        Node previous = getCompilationUnit();
        Node current;

        if (StringUtils.isBlank(source)) {
            astTreeView.setRoot(null);
            return;
        }

        try {
            current = astManager.updateCompilationUnit(source);
        } catch (ParseAbortedException e) {
            invalidateAST(true);
            return;
        }
        if (!Objects.equals(previous, current)) {
            parent.invalidateAst();
            setUpToDateCompilationUnit(current);
        }
    }


    private void setUpToDateCompilationUnit(Node node) {
        astTitleLabel.setText("Abstract Syntax Tree");
        ASTTreeItem root = ASTTreeItem.getRoot(node);
        astTreeView.setRoot(root);
    }


    public void shutdown() {
        codeEditorArea.disableSyntaxHighlighting();
    }


    private void updateSyntaxHighlighter(Language language) {
        Optional<SyntaxHighlighter> highlighter = AvailableSyntaxHighlighters.getHighlighterForLanguage(language);
        
        if (highlighter.isPresent()) {
            codeEditorArea.setSyntaxHighlightingEnabled(highlighter.get());
        } else {
            codeEditorArea.disableSyntaxHighlighting();
        }
    }


    public void clearNodeHighlight() {
        codeEditorArea.clearPrimaryStyleLayer();
    }


    public void highlightNodePrimary(Node node) {
        highlightNodes(Collections.singleton(node), Collections.singleton("primary-highlight"));
    }


    private void highlightNodes(Collection<? extends Node> nodes, Set<String> cssClasses) {
        for (Node node : nodes) {
            if (codeEditorArea.isInRange(node)) {
                codeEditorArea.styleCss(node, cssClasses);
                codeEditorArea.paintCss();
                codeEditorArea.moveTo(node.getBeginLine() - 1, 0);
                codeEditorArea.requestFollowCaret();
            } else {
                codeEditorArea.clearPrimaryStyleLayer();
            }
        }
    }


    public void highlightNodesSecondary(Collection<? extends Node> nodes) {
        highlightNodes(nodes, Collections.singleton("secondary-highlight"));
    }


    public void focusNodeInTreeView(Node node) {
        ASTTreeItem found = ((ASTTreeItem) astTreeView.getRoot()).findItem(node);
        if (found != null) {
            SelectionModel<TreeItem<Node>> selectionModel = astTreeView.getSelectionModel();
            selectionModel.select(found);
            astTreeView.getFocusModel().focus(selectionModel.getSelectedIndex());
            if (!treeViewWrapper.isIndexVisible(selectionModel.getSelectedIndex())) {
                astTreeView.scrollTo(selectionModel.getSelectedIndex());
            }
        }
    }


    private void invalidateAST(boolean error) {
        astTitleLabel.setText("Abstract syntax tree (" + (error ? "error" : "outdated") + ")");
    }


    public void moveCaret(int line, int column) {
        codeEditorArea.moveTo(line, column);
        codeEditorArea.requestFollowCaret();
    }

    @PersistentProperty
    public LanguageVersion getLanguageVersion() {
        return astManager.getLanguageVersion();
    }


    public void setLanguageVersion(LanguageVersion version) {
        astManager.setLanguageVersion(version);
    }


    public Var<LanguageVersion> languageVersionProperty() {
        return astManager.languageVersionProperty();
    }


    public Node getCompilationUnit() {
        return astManager.getCompilationUnit();
    }


    public Val<Node> compilationUnitProperty() {
        return astManager.compilationUnitProperty();
    }


    @PersistentProperty
    public String getText() {
        return codeEditorArea.getText();
    }


    public void setText(String expression) {
        codeEditorArea.replaceText(expression);
    }


    public Val<String> textProperty() {
        return Val.wrap(codeEditorArea.textProperty());
    }


    public void clearStyleLayers() {
        codeEditorArea.clearStyleLayers();
    }

}

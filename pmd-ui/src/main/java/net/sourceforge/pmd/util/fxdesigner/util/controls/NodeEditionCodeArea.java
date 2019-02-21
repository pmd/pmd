/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.EventStreams;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;
import net.sourceforge.pmd.util.fxdesigner.SourceEditorController;
import net.sourceforge.pmd.util.fxdesigner.app.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.AvailableSyntaxHighlighters;
import net.sourceforge.pmd.util.fxdesigner.util.codearea.HighlightLayerCodeArea;
import net.sourceforge.pmd.util.fxdesigner.util.controls.NodeEditionCodeArea.StyleLayerIds;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.css.PseudoClass;


/**
 * A layered code area made to display nodes. Handles the presentation of nodes in place of {@link SourceEditorController}.
 *
 * @author Clément Fournier
 */
public class NodeEditionCodeArea extends HighlightLayerCodeArea<StyleLayerIds> implements NodeSelectionSource {

    private final Var<Node> currentFocusNode = Var.newSimpleVar(null);
    private final Var<List<Node>> currentRuleResults = Var.newSimpleVar(Collections.emptyList());
    private final Var<List<Node>> currentErrorNodes = Var.newSimpleVar(Collections.emptyList());
    private final Var<List<NameOccurrence>> currentNameOccurrences = Var.newSimpleVar(Collections.emptyList());
    private DesignerRoot designerRoot;


    public NodeEditionCodeArea(@NamedArg("designerRoot") DesignerRoot root) {
        super(StyleLayerIds.class);

        this.designerRoot = root;

        // never emits selection events itself for now, but handles events from other sources
        initNodeSelectionHandling(root, EventStreams.never(), false);

        setParagraphGraphicFactory(lineNumberFactory());

        currentRuleResultsProperty().values().subscribe(this::highlightXPathResults);
        currentErrorNodesProperty().values().subscribe(this::highlightErrorNodes);
        currentNameOccurrences.values().subscribe(this::highlightNameOccurrences);
    }

    /** Scroll the editor to a node and makes it visible. */
    private void scrollToNode(Node node) {

        moveTo(node.getBeginLine() - 1, 0);

        if (getVisibleParagraphs().size() < 1) {
            return;
        }

        int visibleLength = lastVisibleParToAllParIndex() - firstVisibleParToAllParIndex();

        if (node.getEndLine() - node.getBeginLine() > visibleLength
            || node.getBeginLine() < firstVisibleParToAllParIndex()) {
            showParagraphAtTop(Math.max(node.getBeginLine() - 2, 0));
        } else if (node.getEndLine() > lastVisibleParToAllParIndex()) {
            showParagraphAtBottom(Math.min(node.getEndLine(), getParagraphs().size()));
        }
    }


    private IntFunction<javafx.scene.Node> lineNumberFactory() {
        IntFunction<javafx.scene.Node> base = LineNumberFactory.get(this);
        Val<Integer> activePar = Val.wrap(currentParagraphProperty());

        return idx -> {

            javafx.scene.Node label = base.apply(idx);

            activePar.conditionOnShowing(label)
                     .values()
                     .subscribe(p -> label.pseudoClassStateChanged(PseudoClass.getPseudoClass("has-caret"), idx == p));

            // adds a pseudo class if part of the focus node appears on this line
            currentFocusNode.conditionOnShowing(label)
                            .values()
                            .subscribe(n -> label.pseudoClassStateChanged(PseudoClass.getPseudoClass("is-focus-node"),
                                                                          n != null && idx + 1 <= n.getEndLine() && idx + 1 >= n.getBeginLine()));

            return label;
        };
    }


    public final Var<List<Node>> currentRuleResultsProperty() {
        return currentRuleResults;
    }


    public final Var<List<Node>> currentErrorNodesProperty() {
        return currentErrorNodes;
    }


    public Var<List<NameOccurrence>> currentNameOccurrencesProperty() {
        return currentNameOccurrences;
    }


    /** Highlights xpath results (xpath highlight). */
    private void highlightXPathResults(Collection<? extends Node> nodes) {
        styleNodes(nodes, StyleLayerIds.XPATH_RESULT, true);
    }


    /** Highlights name occurrences (secondary highlight). */
    private void highlightNameOccurrences(Collection<? extends NameOccurrence> occs) {
        styleNodes(occs.stream().map(NameOccurrence::getLocation).collect(Collectors.toList()), StyleLayerIds.NAME_OCCURENCE, true);
    }


    /** Highlights nodes that are in error (secondary highlight). */
    private void highlightErrorNodes(Collection<? extends Node> nodes) {
        styleNodes(nodes, StyleLayerIds.ERROR, true);
        if (!nodes.isEmpty()) {
            scrollToNode(nodes.iterator().next());
        }
    }


    /** Moves the caret to a position and makes the view follow it. */
    public void moveCaret(int line, int column) {
        moveTo(line, column);
        requestFollowCaret();
    }


    @Override
    public void setFocusNode(Node node) {
        // editor is always scrolled when re-selecting a node
        if (node != null) {
            Platform.runLater(() -> scrollToNode(node));
        }

        if (Objects.equals(node, currentFocusNode.getValue())) {
            return;
        }

        currentFocusNode.setValue(node);

        // editor is only restyled if the selection has changed
        Platform.runLater(() -> styleNodes(node == null ? emptyList() : singleton(node), StyleLayerIds.FOCUS, true));

        if (node instanceof ScopedNode) {
            // not null as well
            Platform.runLater(() -> highlightNameOccurrences(DesignerUtil.getNameOccurrences((ScopedNode) node)));
        }
    }


    @Override
    public DesignerRoot getDesignerRoot() {
        return designerRoot;
    }


    public void updateSyntaxHighlighter(Language language) {
        setSyntaxHighlighter(AvailableSyntaxHighlighters.getHighlighterForLanguage(language).orElse(null));
    }


    /** Style layers for the code area. */
    enum StyleLayerIds implements LayerId {
        // caution, the name of the constants are used as style classes

        /** For the currently selected node. */
        FOCUS,
        /** For declaration usages. */
        NAME_OCCURENCE,
        /** For nodes in error. */
        ERROR,
        /** For xpath results. */
        XPATH_RESULT;

        private final String styleClass; // the id will be used as a style class


        StyleLayerIds() {
            this.styleClass = name().toLowerCase(Locale.ROOT).replace('_', '-') + "-highlight";
        }


        /** focus-highlight, xpath-highlight, error-highlight, name-occurrence-highlight */
        @Override
        public String getStyleClass() {
            return styleClass;
        }

    }

}

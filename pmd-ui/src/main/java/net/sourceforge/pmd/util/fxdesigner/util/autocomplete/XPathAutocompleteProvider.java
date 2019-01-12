/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.AWTException;
import java.awt.Robot;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.fxmisc.richtext.StyledTextArea;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;

import net.sourceforge.pmd.util.fxdesigner.util.controls.ContextMenuWithNoArrows;

import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


/**
 * Wraps a codearea to provide autocompletion support for it.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class XPathAutocompleteProvider {

    private final StyledTextArea<?, ?> myCodeArea;
    private final Supplier<CompletionResultSource> mySuggestionProvider;
    private final ContextMenu autoCompletePopup = new ContextMenuWithNoArrows();


    public XPathAutocompleteProvider(StyledTextArea<?, ?> codeArea, Supplier<CompletionResultSource> suggestionProvider) {
        myCodeArea = codeArea;
        this.mySuggestionProvider = suggestionProvider;

        autoCompletePopup.getStyleClass().add("autocomplete-menu");
        autoCompletePopup.setHideOnEscape(true);
    }


    public void initialiseAutoCompletion() {

        EventStream<Integer> changesEventStream = myCodeArea.plainTextChanges()
                                                            .map(characterChanges -> {
                                                                if (characterChanges.getRemoved().length() > 0) {
                                                                    return characterChanges.getRemovalEnd() - 1;
                                                                }
                                                                return characterChanges.getInsertionEnd();
                                                            });

        EventStream<Integer> keyCombo = EventStreams.eventsOf(myCodeArea, KeyEvent.KEY_PRESSED)
                                                    .filter(key -> key.isControlDown() && key.getCode().equals(KeyCode.SPACE))
                                                    .map(searchPoint -> myCodeArea.getCaretPosition());

        EventStreams.merge(keyCombo, changesEventStream)
                    .map(this::getInsertionPointAndQuery)
                    .hook(t -> {
                        if (t == null) {
                            autoCompletePopup.hide();
                        }
                    })
                    .filter(Objects::nonNull)
                    .subscribe(s -> showAutocompletePopup(s._1, s._2));
    }


    private Tuple2<Integer, String> getInsertionPointAndQuery(int searchPoint) {
        String input = myCodeArea.getText();

        int insertionPoint = getInsertionPoint(searchPoint, input);

        if (searchPoint > input.length()) {
            searchPoint = input.length();
        }

        input = input.substring(insertionPoint, searchPoint).trim();

        return StringUtils.isAlpha(input) ? Tuples.t(insertionPoint, input.trim()) : null;
    }


    private int getInsertionPoint(int searchPoint, String text) {

        int slashIdx = text.lastIndexOf("/", searchPoint);
        int colonIdx = text.lastIndexOf("::", searchPoint);

        slashIdx = slashIdx < 0 ? 0 : slashIdx + 1; // "/".length
        colonIdx = colonIdx < 0 ? 0 : colonIdx + 2; // "::".length

        return Math.max(slashIdx, colonIdx);
    }


    private void showAutocompletePopup(int insertionIndex, String input) {

        int curFocusIdx = getFocusIdx();

        CompletionResultSource suggestionMaker = mySuggestionProvider.get();

        List<MenuItem> suggestions =
            suggestionMaker.getSortedMatches(input, 5)
                           .map(result -> {

                               Label entryLabel = new Label();
                               entryLabel.setGraphic(result.getTextFlow());
                               entryLabel.setPrefHeight(5);
                               CustomMenuItem item = new CustomMenuItem(entryLabel, true);
                               item.setUserData(result);
                               item.setOnAction(e -> applySuggestion(insertionIndex, input, result.getNodeName()));
                               return item;
                           })
                           .collect(Collectors.toList());

        autoCompletePopup.getItems().setAll(suggestions);


        myCodeArea.getCharacterBoundsOnScreen(insertionIndex, insertionIndex + input.length())
                  .ifPresent(bounds -> autoCompletePopup.show(myCodeArea, bounds.getMinX(), bounds.getMaxY()));

        Platform.runLater(() -> focusFirstItem(curFocusIdx));
    }


    private void applySuggestion(int insertionIndex, String toReplace, String replacement) {
        myCodeArea.replaceText(insertionIndex, insertionIndex + toReplace.length(), replacement);
        Platform.runLater(autoCompletePopup::hide);
    }


    /**
     * Programmatically focuses the node at index 0. Doing it with key presses is
     * the only reliable way, because we're replacing the items.
     *
     * That allows using ENTER to insert the first completion without pressing DOWN first.
     */
    private void focusFirstItem(int curFocusIdx) {

        Robot r;
        try {
            r = new Robot();
        } catch (AWTException e) {
            return;
        }

        if (curFocusIdx == 0) {
            // the first item was previously focused
            // For some reason there's a bug where we can't be sure that the 0th
            // idx will stay focused when changing items unless we do the following:

            r.keyPress(VK_DOWN);
            r.keyRelease(VK_DOWN);
            r.keyPress(VK_UP);
            r.keyRelease(VK_UP);

            return;
        }

        int diff = Math.abs(curFocusIdx); // so if curFocusIdx was -1 we have to go 1 down
        boolean goUp = curFocusIdx > 0;

        while (diff-- > 0) {
            int key = goUp ? VK_UP : VK_DOWN;

            r.keyPress(key);
            r.keyRelease(key);
        }
    }


    /** Gets the index of the currently focused item. */
    private int getFocusIdx() {
        if (!autoCompletePopup.isShowing()) {
            return -1;
        }

        List<ObservableSet<PseudoClass>> collect =
            autoCompletePopup.getItems()
                             .stream()
                             .map(this::getStyleableNode)
                             .filter(Objects::nonNull)
                             .map(Node::getPseudoClassStates)
                             .collect(Collectors.toList());

        for (int i = 0; i < collect.size(); i++) {
            if (collect.get(i).contains(PseudoClass.getPseudoClass("focused"))) {
                return i;
            }
        }

        return -1;
    }


    /** Gets the index of the node. */
    private Node getStyleableNode(MenuItem item) {

        try {
            // Only since jdk 9 unfortunately
            return (Node) MethodUtils.invokeMethod(item, "getStyleableNode");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // then we're on jdk 8, in which case we do the work ourselves
        }

        ContextMenu parentPopup = item.getParentPopup();
        if (parentPopup == null) {
            return null;
        }

        if (parentPopup.getSkin() == null) {
            // popup not showing
            return null;
        }

        Parent nodes;
        try {
            nodes = (Parent) FieldUtils.readDeclaredField(parentPopup.getSkin().getNode(), "itemsContainer", true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        for (Node child : nodes.getChildrenUnmodifiable()) {

            // we can't check for instanceof bc that would not be cross-jdk
            try {
                Object childItem = MethodUtils.invokeExactMethod(child, "getItem");
                if (item.equals(childItem)) {
                    return child;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}

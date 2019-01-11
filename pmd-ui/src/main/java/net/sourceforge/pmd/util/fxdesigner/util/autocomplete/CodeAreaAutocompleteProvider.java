/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.autocomplete;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.StyledTextArea;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.util.Tuple2;
import org.reactfx.util.Tuples;

import net.sourceforge.pmd.util.fxdesigner.util.controls.ContextMenuWithNoArrows;

import javafx.application.Platform;
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
public final class CodeAreaAutocompleteProvider {

    private final StyledTextArea<?, ?> myCodeArea;
    private final Supplier<XPathSuggestionMaker> mySuggestionProvider;
    private final ContextMenu autoCompletePopup = new ContextMenuWithNoArrows();


    public CodeAreaAutocompleteProvider(StyledTextArea<?, ?> codeArea, Supplier<XPathSuggestionMaker> suggestionProvider) {
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

        initTabCompletion();
    }


    private void initTabCompletion() {
        // enable tab or enter completion
        EventStreams.eventsOf(myCodeArea, KeyEvent.KEY_PRESSED)
                    .filter(key -> key.getCode().equals(KeyCode.TAB) || key.getCode().equals(KeyCode.ENTER))
                    .conditionOn(autoCompletePopup.showingProperty())
                    .map(event -> myCodeArea.getCaretPosition())
                    .map(this::getInsertionPointAndQuery)
                    .filter(Objects::nonNull)
                    .subscribe(t -> {
                        Optional<MatchResult> focusedResult = getFocusedResult();
                        focusedResult.ifPresent(r -> applySuggestion(t._1, t._2, r.getNodeName()));
                    });
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

        XPathSuggestionMaker suggestionMaker = mySuggestionProvider.get();

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

        if (autoCompletePopup.getItems().size() > 0) {
            //            ((CustomMenuItem) autoCompletePopup.getItems().get(0))
            //                .getContent()
            //                .getPseudoClassStates().add(PseudoClass.getPseudoClass("focused"));
        }

        myCodeArea.getCharacterBoundsOnScreen(insertionIndex, insertionIndex + input.length())
                  .ifPresent(bounds -> autoCompletePopup.show(myCodeArea, bounds.getMinX(), bounds.getMaxY()));
    }


    private Optional<MatchResult> getFocusedResult() {

        if (autoCompletePopup.getItems().size() > 0) {
            return Optional.of((MatchResult) autoCompletePopup.getItems().get(0).getUserData());
        }

        return Optional.empty();
    }


    private void applySuggestion(int insertionIndex, String toReplace, String replacement) {
        myCodeArea.replaceText(insertionIndex, insertionIndex + toReplace.length(), replacement);
        Platform.runLater(autoCompletePopup::hide);
    }

}

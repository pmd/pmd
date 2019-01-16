/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import java.util.Collection;
import java.util.Objects;

import org.reactfx.value.Val;
import org.reactfx.value.Var;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.StackPane;


/**
 * A Titled pane that has a toolbar in its header region.
 * Supported by some CSS in designer.less.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class ToolbarTitledPane extends TitledPane {


    private ToolBar toolBar = new ToolBar();
    private Var<String> title = Var.newSimpleVar("Title");


    public ToolbarTitledPane() {

        getStyleClass().add("tool-bar-title");

        // change the default
        setCollapsible(false);

        toolBar.setPadding(Insets.EMPTY);

        Label titleLabel = new Label("Title");
        titleLabel.textProperty().bind(title);
        titleLabel.getStyleClass().add("title-label");

        toolBar.getItems().add(titleLabel);

        setGraphic(toolBar);

        // should be an empty string, binding prevents to set it
        textProperty().bind(Val.constant(""));

        // The toolbar is too large for the title region and is not
        // centered unless we bind the height, like follows

        Val.wrap(toolBar.parentProperty())
            .values()
            .filter(Objects::nonNull)
            .subscribe(parent -> {
                // The title region is provided by the skin,
                // this is the only way to access it outside of css
                StackPane titleRegion = (StackPane) parent;
                toolBar.maxHeightProperty().unbind();
                toolBar.maxHeightProperty().bind(titleRegion.heightProperty());
                toolBar.minHeightProperty().unbind();
                toolBar.minHeightProperty().bind(titleRegion.heightProperty());
                toolBar.prefHeightProperty().unbind();
                toolBar.prefHeightProperty().bind(titleRegion.heightProperty());
            });

    }


    public ObservableList<Node> getToolbarItems() {
        return toolBar.getItems();
    }


    public void setToolbarItems(Collection<? extends Node> nodes) {
        toolBar.getItems().setAll(nodes);
    }


    public String getTitle() {
        return title.getValue();
    }


    public void setTitle(String title) {
        this.title.setValue(title);
    }


    /** Title of the pane, not equivalent to {@link #textProperty()}. */
    public Var<String> titleProperty() {
        return title;
    }
}

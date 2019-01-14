/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;

import javafx.application.Platform;
import javafx.fxml.Initializable;


/**
 * Make the initialization cycle of JavaFX clearer. Children controller
 * are initialized before their parent, but sometimes it should only
 * perform some actions after its parent has been initialized, e.g. binding
 * properties that depend on a restored setting or stuff. This is part
 * of the reason why {@link Platform#runLater(Runnable)} can sometimes
 * be enough to solve initialization problems.
 *
 * This only works if all controllers in the tree extend this class.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class AbstractController implements Initializable, SettingsOwner {

    @Override
    public final void initialize(URL url, ResourceBundle resourceBundle) {
        beforeParentInit();
        for (AbstractController child : getChildren()) {
            child.afterParentInit();
        }
        afterChildrenInit();
    }


    /**
     * Executed before the parent's initialization.
     * Always executed once at the start of the initialization
     * of this controller.
     */
    protected void beforeParentInit() {
        // by default do nothing
    }


    /**
     * Executed after the parent's initialization (so after {@link #afterChildrenInit()}).
     * This also means, after persistent settings restoration. If this node has no
     * parent, then this is never executed.
     */
    protected void afterParentInit() {
        // by default do nothing
    }


    /**
     * Runs once after every child has finished their initialization.
     * This will be run in all cases. It's only useful if the children
     * do something useful in their {@link #afterParentInit()}.
     */
    protected void afterChildrenInit() {
        // by default do nothing
    }


    @Override
    public List<? extends SettingsOwner> getChildrenSettingsNodes() {
        return getChildren();
    }


    protected List<? extends AbstractController> getChildren() {
        return Collections.emptyList();
    }
}

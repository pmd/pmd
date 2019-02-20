/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.app;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;

import javafx.application.Platform;
import javafx.fxml.Initializable;


/**
 * Base class for controllers of the app. The main window of the app is split
 * into regions corresponding to some area of functionality. Each has its own
 * FXML file that can be found in the fxml resource directory. Each also has
 * its own independent controller. Since the FXML regions are nested like a
 * tree (the JavaFX scene graph), it's natural to link the controllers in a
 * tree too.
 *
 * <p>For now controllers mostly communicate by sending messages to their parent
 * and letting it forward the message to the rest of the app.
 * TODO I'm more and more convinced we should avoid that and stop having the controllers
 *  hold a reference to their parent. They should only communicate by exposing properties
 *  their parent binds to, but they shouldn't know about their parent.
 *  {@link MessageChannel}s can allow us to decouple them event more.
 *
 * <p>This class mainly to make the initialization cycle of JavaFX clearer. Children controllers
 * are initialized before their parent, but sometimes they should only
 * perform some actions after its parent has been initialized, e.g. binding
 * properties that depend on a restored setting or stuff. This is part
 * of the reason why {@link Platform#runLater(Runnable)} can sometimes
 * be enough to solve initialization problems.
 *
 * <p>This only works if all controllers in the initialization sequence of an
 * FXML file extend this class.
 *
 *
 * @param <T> Type of the parent controller
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class AbstractController<T extends AbstractController<?>> implements Initializable, SettingsOwner, ApplicationComponent {

    protected final T parent;
    private final DesignerRoot designerRoot;


    protected AbstractController(DesignerRoot root, T parent) {
        this.parent = parent;
        this.designerRoot = root;
    }


    protected AbstractController(T parent) {
        this(parent.getDesignerRoot(), parent);
    }


    @Override
    public DesignerRoot getDesignerRoot() {
        return designerRoot;
    }


    @Override
    public final void initialize(URL url, ResourceBundle resourceBundle) {
        beforeParentInit();
        for (AbstractController<?> child : getChildren()) {
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


    protected List<? extends AbstractController<?>> getChildren() {
        return Collections.emptyList();
    }
}

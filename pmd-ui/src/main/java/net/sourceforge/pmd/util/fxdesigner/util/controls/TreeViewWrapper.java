/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import javafx.application.Platform;
import javafx.scene.control.Skin;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;


/**
 * Reflective solution to know if a cell in a TreeView is
 * visible or not, to prevent confusing scrolling. Works
 * under Java 8, 9, 10. Under Java 9+, requires the
 * "--add-opens javafx.controls/javafx.scene.control.skin=ALL-UNNAMED"
 * VM option.
 *
 * @param <T> Element type of the treeview
 * @author Clément Fournier
 * @since 6.4.0
 */
class TreeViewWrapper<T> {
    

    private final TreeView<T> wrapped;
    private Method treeViewFirstVisibleMethod;
    private Method treeViewLastVisibleMethod;
    // We can't use strong typing
    // because the class has moved packages over different java versions
    private Object virtualFlow = null;


    TreeViewWrapper(TreeView<T> wrapped) {
        Objects.requireNonNull(wrapped);
        this.wrapped = wrapped;
        Platform.runLater(this::initialiseTreeViewReflection);
    }


    private void initialiseTreeViewReflection() {

        // we can't use wrapped.getSkin() because it may be null.
        // we don't care about the specific instance, we just want the class
        @SuppressWarnings("PMD.UselessOverridingMethod")
        Skin<?> dftSkin = new TreeView<Object>() {
            @Override
            protected Skin<?> createDefaultSkin() {
                return super.createDefaultSkin();
            }
        }.createDefaultSkin();

        Object flow = getVirtualFlow(dftSkin);

        if (flow == null) {
            return;
        }

        treeViewFirstVisibleMethod = MethodUtils.getMatchingMethod(flow.getClass(), "getFirstVisibleCell");
        treeViewLastVisibleMethod = MethodUtils.getMatchingMethod(flow.getClass(), "getLastVisibleCell");
    }


    /**
     * Returns true if the item at the given index
     * is visible in the TreeView.
     */
    boolean isIndexVisible(int index) {
        if (virtualFlow == null && wrapped.getSkin() == null) {
            return false;
        } else if (virtualFlow == null && wrapped.getSkin() != null) {
            // the flow is cached, so the skin must not be changed
            virtualFlow = getVirtualFlow(wrapped.getSkin());
        }

        if (virtualFlow == null) {
            return false;
        }

        Optional<TreeCell<T>> first = getFirstVisibleCell();
        Optional<TreeCell<T>> last = getLastVisibleCell();

        return first.isPresent()
                && last.isPresent()
                && first.get().getIndex() <= index
                && last.get().getIndex() >= index;
    }


    private Optional<TreeCell<T>> getCellFromAccessor(Method accessor) {
        return Optional.ofNullable(accessor).map(m -> {
            try {
                @SuppressWarnings("unchecked")
                TreeCell<T> cell = (TreeCell<T>) m.invoke(virtualFlow);
                return cell;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        });
    }


    private Optional<TreeCell<T>> getFirstVisibleCell() {
        return getCellFromAccessor(treeViewFirstVisibleMethod);
    }


    private Optional<TreeCell<T>> getLastVisibleCell() {
        return getCellFromAccessor(treeViewLastVisibleMethod);
    }


    private static Object getVirtualFlow(Skin<?> skin) {
        try {
            // On JRE 9 and 10, the field is declared in TreeViewSkin
            // http://hg.openjdk.java.net/openjfx/9/rt/file/c734b008e3e8/modules/javafx.controls/src/main/java/javafx/scene/control/skin/TreeViewSkin.java#l85
            // http://hg.openjdk.java.net/openjfx/10/rt/file/d14b61c6be12/modules/javafx.controls/src/main/java/javafx/scene/control/skin/TreeViewSkin.java#l85
            // On JRE 8, the field is declared in the VirtualContainerBase superclass
            // http://hg.openjdk.java.net/openjfx/8/master/rt/file/f89b7dc932af/modules/controls/src/main/java/com/sun/javafx/scene/control/skin/VirtualContainerBase.java#l68

            return FieldUtils.readField(skin, "flow", true);
        } catch (IllegalAccessException ignored) {

        }
        return null;
    }
}

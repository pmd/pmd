/*
 * Copyright 2006,2007 Enrico Boldrini, Lorenzo Bigagli This file is part of
 * CheckboxTree. CheckboxTree is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version. CheckboxTree is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details. You should have received a copy of the GNU
 * General Public License along with CheckboxTree; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 */
package net.sourceforge.pmd.jedit.checkboxtree;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * The model for checking/unchecking the nodes of a CheckboxTree. Alterations of
 * a node state may propagate on descendants/ascendants, according to the
 * behaviour of the checking model. See CheckingMode for the available
 * behaviours.
 *
 * @author bigagli
 * @author boldrini
 */
public interface TreeCheckingModel {

    /**
         * The checking behaviours provided by this class.
         *
         * @author boldrini
         */
    public enum CheckingMode {

    /**
         * The check is not propagated at all, toggles the just-clicked checkbox
         * only.
         */
    SIMPLE,

    /**
         * Toggles the just-clicked checkbox and propagates the change down. In
         * other words, if the clicked checkbox is checked all the descendants
         * will be checked; otherwise all the descendants will be unchecked
         */
    PROPAGATE,

    /**
         * The check is propagated, like the Propagate mode to descendants. If a
         * user unchecks a checkbox the uncheck will also be propagated to
         * ancestors.
         */
    PROPAGATE_UP_UNCHECK,

    /**
         * Propagates the change not only to descendants but also to ancestors.
         * With regard to descendants this mode behaves exactly like the
         * Propagate mode. With regard to ancestors it checks/unchecks them as
         * needed so that a node is unchecked if and only if all of its children
         * are unchecked.
         */
    PROPAGATE_PRESERVING_UNCHECK,

    /**
         * Propagates the change not only to descendants but also to ancestors.
         * With regard to descendants this mode behaves exactly like the
         * Propagate mode. With regard to ancestors it checks/unchecks them as
         * needed so that a node is checked if and only if all of its children
         * are checked.
         */
    PROPAGATE_PRESERVING_CHECK
    }

    /**
         * Returns whether the specified path is checked.
         */
    public boolean isPathChecked(TreePath path);

    /**
         * Returns whether the specified path checking state can be toggled.
         */
    public boolean isPathEnabled(TreePath path);

    /**
         * Returns whether the specified path is greyed.
         */
    public boolean isPathGreyed(TreePath path);

    /**
         * Alter (check/uncheck) the checking state of the specified path if
         * possible and also propagate the new state if needed by the mode.
         */
    public void toggleCheckingPath(TreePath pathForRow);

    /**
         * add a path to the checked paths set
         *
         * @param path the path to be added.
         */
    public void addCheckingPath(TreePath path);

    /**
         * add paths to the checked paths set
         *
         * @param paths the paths to be added.
         */
    public void addCheckingPaths(TreePath[] paths);

    /**
         * remove a path from the checked paths set
         *
         * @param path the path to be added.
         */
    public void removeCheckingPath(TreePath path);

    /**
         * remove paths from the checked paths set
         *
         * @param paths the paths to be added.
         */
    public void removeCheckingPaths(TreePath[] paths);

    /**
         * Sets whether or not the path is enabled.
         *
         * @param path the path to enable/disable
         */
    public void setPathEnabled(TreePath path, boolean enable);

    /**
         * Sets whether or not the paths are enabled.
         *
         * @param paths the paths to enable/disable
         */
    public void setPathsEnabled(TreePath[] paths, boolean enable);

    /**
         * @return Returns the paths that are in the checking set.
         */
    public TreePath[] getCheckingPaths();

    /**
         * @return Returns the paths that are in the checking set and are the
         *         (upper) roots of checked trees.
         */
    public TreePath[] getCheckingRoots();

    /**
         * @return Returns the paths that are in the greying set.
         */
    public TreePath[] getGreyingPaths();

    /**
         * Set the checking to paths.
         */
    public void setCheckingPaths(TreePath[] paths);

    /**
         * Set the checking to path.
         */
    public void setCheckingPath(TreePath path);

    /**
         * Clears the checking.
         */
    public void clearChecking();

    /**
         * Set the checking mode.
         *
         * @param mode The checkingMode to set.
         */
    public void setCheckingMode(CheckingMode mode);

    /**
         * @return Returns the CheckingMode.
         */
    public CheckingMode getCheckingMode();

    /**
         * Get the listener for the <code>TreeModelEvent</code> posted after
         * the tree changes.
         *
         * @deprecated use get/setTreeModel instead
         */
    @Deprecated
    public TreeModelListener getTreeModelListener();

    /**
         * Adds x to the list of listeners that are notified each time the set
         * of checking TreePaths changes.
         *
         * @param x the new listener to be added
         */
    public void addTreeCheckingListener(TreeCheckingListener x);

    /**
         * Removes x from the list of listeners that are notified each time the
         * set of checking TreePaths changes.
         *
         * @param x the listener to remove
         */
    public void removeTreeCheckingListener(TreeCheckingListener x);

    /**
         * Returns the tree model to which this checking model is bound, or null
         * if not set.
         */
    public TreeModel getTreeModel();

    /**
         * Set the tree model to which this checking model is (possibly) bound.
         * A checking model may use a tree model to propagate the checking. A
         * checking model may also listen to the model, to adjust the checking
         * upon model events. The current checking is cleared.
         */
    public void setTreeModel(TreeModel model);

}

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

import javax.swing.tree.TreePath;

/**
 * PropagateUpWhiteTreeCheckingMode define a TreeCheckingMode with down
 * recursion of the check when nodes are clicked and up only when uncheck. The
 * check is propagated, like the Propagate mode to descendants. If a user
 * unchecks a checkbox the uncheck will also be propagated to ancestors.
 *
 * @author Boldrini
 */
public class PropagateUpWhiteTreeCheckingMode extends TreeCheckingMode {

    PropagateUpWhiteTreeCheckingMode(DefaultTreeCheckingModel model) {
    super(model);
    }

    @Override
    public void checkPath(TreePath path) {
    // check is propagated to children
    this.model.checkSubTree(path);
    // check all the ancestors with subtrees checked
    TreePath[] parents = new TreePath[path.getPathCount()];
    parents[0] = path;
    TreePath parentPath = path;
    // uncheck is propagated to parents, too
    while ((parentPath = parentPath.getParentPath()) != null) {
        this.model.updatePathGreyness(parentPath);
    }
    }

    @Override
    public void uncheckPath(TreePath path) {
    // uncheck is propagated to children
    this.model.uncheckSubTree(path);
    TreePath parentPath = path;
    // uncheck is propagated to parents, too
    while ((parentPath = parentPath.getParentPath()) != null) {
        this.model.removeFromCheckedPathsSet(parentPath);
        this.model.updatePathGreyness(parentPath);
    }
    }

    /*
         * (non-Javadoc)
         *
         * @see net.sourceforge.pmd.jedit.checkboxtree.TreeCheckingMode#updateCheckAfterChildrenInserted(javax.swing.tree.TreePath)
         */
    @Override
    public void updateCheckAfterChildrenInserted(TreePath parent) {
    if (this.model.isPathChecked(parent)) {
        checkPath(parent);
    } else {
        uncheckPath(parent);
    }
    }

    /*
         * (non-Javadoc)
         *
         * @see net.sourceforge.pmd.jedit.checkboxtree.TreeCheckingMode#updateCheckAfterChildrenRemoved(javax.swing.tree.TreePath)
         */
    @Override
    public void updateCheckAfterChildrenRemoved(TreePath parent) {
    if (!this.model.isPathChecked(parent)) {
        // System.out.println(parent +" was removed (not checked)");
        if (this.model.getChildrenPath(parent).length != 0) {
        if (!this.model.pathHasChildrenWithValue(parent, false)) {
            // System.out.println("uncheking it");
            checkPath(parent);
        }
        }
    }
    this.model.updatePathGreyness(parent);
    this.model.updateAncestorsGreyness(parent);
    }

    /*
         * (non-Javadoc)
         *
         * @see net.sourceforge.pmd.jedit.checkboxtree.TreeCheckingMode#updateCheckAfterStructureChanged(javax.swing.tree.TreePath)
         */
    @Override
    public void updateCheckAfterStructureChanged(TreePath parent) {
    if (this.model.isPathChecked(parent)) {
        checkPath(parent);
    } else {
        uncheckPath(parent);
    }
    }

}
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
 * SimpleTreeCheckingMode defines a TreeCheckingMode without recursion. In this
 * simple mode the check state always changes only the current node: no
 * recursion.
 *
 * @author Boldrini
 */
public class SimpleTreeCheckingMode extends TreeCheckingMode {

    SimpleTreeCheckingMode(DefaultTreeCheckingModel model) {
    super(model);
    }

    @Override
    public void checkPath(TreePath path) {
    this.model.addToCheckedPathsSet(path);
    this.model.updatePathGreyness(path);
    this.model.updateAncestorsGreyness(path);
    }

    @Override
    public void uncheckPath(TreePath path) {
    this.model.removeFromCheckedPathsSet(path);
    this.model.updatePathGreyness(path);
    this.model.updateAncestorsGreyness(path);
    }

    /*
         * (non-Javadoc)
         *
         * @see net.sourceforge.pmd.jedit.checkboxtree.TreeCheckingMode#updateCheckAfterChildrenInserted(javax.swing.tree.TreePath)
         */
    @Override
    public void updateCheckAfterChildrenInserted(TreePath parent) {
    this.model.updatePathGreyness(parent);
    this.model.updateAncestorsGreyness(parent);
    }

    /*
         * (non-Javadoc)
         *
         * @see net.sourceforge.pmd.jedit.checkboxtree.TreeCheckingMode#updateCheckAfterChildrenRemoved(javax.swing.tree.TreePath)
         */
    @Override
    public void updateCheckAfterChildrenRemoved(TreePath parent) {
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
    this.model.updatePathGreyness(parent);
    this.model.updateAncestorsGreyness(parent);
    }

}
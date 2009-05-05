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
 * The model for checking/unchecking the nodes of a CheckboxTree. Alterations
 * of a node state may propagate on descendants/ascendants, according to the
 * behaviour of the model. Several default behavioural modes are defined. The
 * models must use the methods addToCheckedSet and removeFromCheckedSet from
 * DefaultTreeCheckingModel to add/remove the single paths from the checking
 * set.
 *
 * @author bigagli
 * @author boldrini
 */
public abstract class TreeCheckingMode {

    protected DefaultTreeCheckingModel model;

    // TODO: implementare Strategy in questo modo: TreeCheckingMode classe
    // interna al TreeCheckingModel, con un metodo getModel() protetto,
    // utile
    // alle sottoclassi
    TreeCheckingMode( DefaultTreeCheckingModel model ) {
        this.model = model;
    }

    /**
         * Checks the specified path and propagates the checking according to
         * the strategy
         *
         * @param path the path to be added.
         */

    public abstract void checkPath( TreePath path );

    /**
         * Unchecks the specified path and propagates the checking according to
         * the strategy
         *
         * @param path the path to be removed.
         */
    public abstract void uncheckPath( TreePath path );

    /**
         * Update the check of the given path after the insertion of some of its
         * children, according to the strategy
         *
         * @param path
         */
    public abstract void updateCheckAfterChildrenInserted( TreePath path );

    /**
         * Update the check of the given path after the removal of some of its
         * children, according to the strategy
         *
         * @param path
         */
    public abstract void updateCheckAfterChildrenRemoved( TreePath path );

    /**
         * Update the check of the given path after the structure change,
         * according to the strategy
         *
         * @param path
         */
    public abstract void updateCheckAfterStructureChanged( TreePath path );

}
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

import java.util.HashSet;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * The default tree checking model provides: - methods for store checked
 * TreePaths and retrieve them. It doesn't provide the implementation of
 * addCheckingPath and removeCheckingPath methods (are delegated to
 * CheckingMode). This implementation is based on TreePath only and does not
 * take advantage of TreeNode convenience methods. Alternative implementation
 * may assume that the tree nodes be TreeNode instances.
 *
 * @author Bigagli
 * @author Boldrini
 */
public class DefaultTreeCheckingModel implements TreeCheckingModel {

    private HashSet<TreePath> checkedPathsSet;

    private HashSet<TreePath> greyedPathsSet;

    private HashSet<TreePath> disabledPathsSet;

    private PropagateCheckingListener propagateCheckingListener;

    protected TreeCheckingMode checkingMode;

    protected TreeModel model;

    /** Event listener list. */
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Creates a DefaultTreeCheckingModel with PropagateTreeCheckingMode.
     */
    public DefaultTreeCheckingModel( TreeModel model ) {
        this.model = model;
        this.checkedPathsSet = new HashSet<TreePath>();
        this.greyedPathsSet = new HashSet<TreePath>();
        this.disabledPathsSet = new HashSet<TreePath>();
        this.propagateCheckingListener = new PropagateCheckingListener();
        this.setCheckingMode( CheckingMode.PROPAGATE );
    }

    /**
         * @deprecated
         */
    @Deprecated
    public TreeModelListener getTreeModelListener() {
        return null;
    }

    private class PropagateCheckingListener implements TreeModelListener {
        /**
             * Updates the check of the just inserted nodes.
             */
        public void treeNodesInserted( TreeModelEvent e ) {
            TreePath path = e.getTreePath();
            DefaultTreeCheckingModel.this.checkingMode.updateCheckAfterChildrenInserted( path );
        }

        /**
             * Nothing to do if nodes were removed.
             */
        public void treeNodesRemoved( TreeModelEvent e ) {
            TreePath path = e.getTreePath();
            DefaultTreeCheckingModel.this.checkingMode.updateCheckAfterChildrenRemoved( path );
        }

        /**
             * Updates the tree greyness in case of nodes changes.
             */
        public void treeNodesChanged( TreeModelEvent e ) {
            TreePath path = e.getTreePath();
            updateSubTreeGreyness( path );
            updateAncestorsGreyness( path );
        }

        /**
             * Updates the tree greyness in case of structure changes.
             */
        public void treeStructureChanged( TreeModelEvent e ) {
            TreePath path = e.getTreePath();
            DefaultTreeCheckingModel.this.checkingMode.updateCheckAfterStructureChanged( path );
        }
    }

    /**
         * Updates consistency of the checking. It's based on paths greyness.
         */
    public void updateCheckingConsistency() {
        updateSubTreeCheckingConsistency( new TreePath( this.model.getRoot() ) );
    }

    /**
         * Updates consistency of the checking of sub-tree starting at path.
         * It's based on paths greyness. TODO: test this method, never used
         *
         * @param path the root of the sub-tree to be grey-updated
         */
    public void updateSubTreeCheckingConsistency( TreePath path ) {
        if ( isPathGreyed( path ) ) {
            // greyed
            for ( TreePath childPath : getChildrenPath( path ) ) {
                updateSubTreeCheckingConsistency( childPath );
            }
            updatePathGreyness( path );
        }
        else {
            // not greyed
            if ( isPathChecked( path ) ) {
                checkSubTree( path );
            }
            else {
                uncheckSubTree( path );
            }
            return ;
        }
    }

    public boolean isPathChecked( TreePath path ) {
        return this.checkedPathsSet.contains( path );
    }

    public boolean isPathEnabled( TreePath path ) {
        return !this.disabledPathsSet.contains( path );
    }

    public boolean isPathGreyed( TreePath path ) {
        return this.greyedPathsSet.contains( path );
    }

    void addToGreyedPathsSet( TreePath path ) {
        this.greyedPathsSet.add( path );
    }

    void removeFromGreyedPathsSet( TreePath path ) {
        this.greyedPathsSet.remove( path );
    }

    /**
         * Sets whether or not the path is enabled.
         *
         * @param path the path to enable/disable
         */
    public void setPathEnabled( TreePath path, boolean enable ) {
        if ( enable ) {
            this.disabledPathsSet.remove( path );
        }
        else {
            this.disabledPathsSet.add( path );
        }
    }

    /**
         * Sets whether or not the paths are enabled.
         *
         * @param paths the paths to enable/disable
         */
    public void setPathsEnabled( TreePath[] paths, boolean enable ) {
        for ( TreePath path : paths ) {
            setPathEnabled( path, enable );
        }
    }

    void addToCheckedPathsSet( TreePath path ) {
        this.checkedPathsSet.add( path );
    }

    void removeFromCheckedPathsSet( TreePath path ) {
        this.checkedPathsSet.remove( path );
    }

    /**
         * Ungreys the subtree with root path.
         *
         * @param path root of the tree to be checked
         */
    public void ungreySubTree( TreePath path ) {
        removeFromGreyedPathsSet( path );
        for ( TreePath childPath : getChildrenPath( path ) ) {
            ungreySubTree( childPath );
        }
    }

    /**
         * Checks the subtree with root path.
         *
         * @param path root of the tree to be checked
         */
    public void checkSubTree( final TreePath path ) {
        addToCheckedPathsSet( path );
        removeFromGreyedPathsSet( path );
        Object node = path.getLastPathComponent();
        int childrenNumber = this.model.getChildCount( node );
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            TreePath childPath = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
            checkSubTree( childPath );
        }
    }

    /**
         * Unchecks the subtree with root path.
         *
         * @param path root of the tree to be unchecked
         */
    public void uncheckSubTree( TreePath path ) {
        removeFromCheckedPathsSet( path );
        removeFromGreyedPathsSet( path );
        Object node = path.getLastPathComponent();
        int childrenNumber = this.model.getChildCount( node );
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            TreePath childPath = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
            uncheckSubTree( childPath );
        }
    }

    /**
         * Delegates to the current checkingMode the toggling style, using the
         * Strategy Pattern.
         */
    public void toggleCheckingPath( TreePath path ) {
        if ( !isPathEnabled( path ) ) {
            return ;
        }
        if ( isPathChecked( path ) ) {
            removeCheckingPath( path );
        }
        else {
            addCheckingPath( path );
        }

    }

    /**
         * Sets the checking to path.
         */
    public void setCheckingPath( TreePath path ) {
        clearChecking();
        addCheckingPath( path );
    }

    /**
         * Sets the checking to paths.
         */
    public void setCheckingPaths( TreePath[] paths ) {
        clearChecking();
        for ( TreePath path : paths ) {
            addCheckingPath( path );
        }
    }

    /**
         * Clears the checking.
         */
    public void clearChecking() {
        this.checkedPathsSet.clear();
        this.greyedPathsSet.clear();
        fireValueChanged( new TreeCheckingEvent( new TreePath( model.getRoot() ) ) );
    }

    /**
         * @return The paths that are in the greying.
         */
    public TreePath[] getGreyingPaths() {
        return greyedPathsSet.toArray( new TreePath[ greyedPathsSet.size() ] );
    }

    /**
         * @return Returns the paths that are in the checking.
         */
    public TreePath[] getCheckingPaths() {
        return checkedPathsSet.toArray( new TreePath[ checkedPathsSet.size() ] );
    }

    /**
         * @return Returns the paths that are in the checking set and are the
         *         (upper) roots of checked trees.
         */
    public TreePath[] getCheckingRoots() {
        Vector<TreePath> roots = getCheckingRoots( new TreePath( this.model.getRoot() ) );
        return roots.toArray( new TreePath[] {} );
    }

    /**
         * @param path
         * @return
         */
    private Vector<TreePath> getCheckingRoots( TreePath path ) {
        Object node = path.getLastPathComponent();
        Vector<TreePath> roots = new Vector<TreePath>();
        if ( !isPathGreyed( path ) ) {
            if ( isPathChecked( path ) ) {
                roots.add( path );
            }
            return roots;
        }
        // path is greyed
        int childrenNumber = this.model.getChildCount( node );
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            TreePath childPath = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
            roots.addAll( getCheckingRoots( childPath ) );
        }
        return roots;
    }

    /**
         * @return The CheckingMode.
         */
    public CheckingMode getCheckingMode() {
        if ( this.checkingMode instanceof SimpleTreeCheckingMode ) {
            return CheckingMode.SIMPLE;
        }
        if ( this.checkingMode instanceof PropagateTreeCheckingMode ) {
            return CheckingMode.PROPAGATE;
        }
        if ( this.checkingMode instanceof PropagatePreservingCheckTreeCheckingMode ) {
            return CheckingMode.PROPAGATE_PRESERVING_CHECK;
        }
        if ( this.checkingMode instanceof PropagatePreservingUncheckTreeCheckingMode ) {
            return CheckingMode.PROPAGATE_PRESERVING_UNCHECK;
        }
        if ( this.checkingMode instanceof PropagateUpWhiteTreeCheckingMode ) {
            return CheckingMode.PROPAGATE_UP_UNCHECK;
        }
        return null;
    }

    /**
     * Sets the specified checking mode. The consistence of the existing
     * checking is not enforced nor controlled.
     */
    public void setCheckingMode( CheckingMode mode ) {
        /*
         * CheckingMode implements togglePath method. (Strategy Pattern was
         * used).
         */
        switch ( mode ) {
            case SIMPLE:
                this.checkingMode = new SimpleTreeCheckingMode( this );
                break;
            case PROPAGATE:
                this.checkingMode = new PropagateTreeCheckingMode( this );
                break;
            case PROPAGATE_PRESERVING_CHECK:
                this.checkingMode = new PropagatePreservingCheckTreeCheckingMode( this );
                break;
            case PROPAGATE_PRESERVING_UNCHECK:
                this.checkingMode = new PropagatePreservingUncheckTreeCheckingMode( this );
                break;
            case PROPAGATE_UP_UNCHECK:
                this.checkingMode = new PropagateUpWhiteTreeCheckingMode( this );
                break;
            default:
                break;
        }
        // // TODO: safe to delete???
        // updateTreeGreyness();
    }

    /**
     * Sets the specified checking mode. The consistence of the existing
     * checking is not enforced nor controlled.
     */
    public void setCheckingMode( TreeCheckingMode mode ) {
        this.checkingMode = mode;
    }

    /**
     * Adds the paths to the checked paths set
     *
     * @param paths the paths to be added.
     */
    public void addCheckingPaths( TreePath[] paths ) {
        for ( TreePath path : paths ) {
            addCheckingPath( path );
        }
    }

    /**
     * Adds a path to the checked paths set
     *
     * @param path the path to be added.
     */
    public void addCheckingPath( TreePath path ) {
        this.checkingMode.checkPath( path );
        TreeCheckingEvent event = new TreeCheckingEvent( path );
        fireValueChanged( event );
    }

    /**
     * Removes a path from the checked paths set
     *
     * @param path the path to be removed
     */
    public void removeCheckingPath( TreePath path ) {
        this.checkingMode.uncheckPath( path );
        TreeCheckingEvent event = new TreeCheckingEvent( path );
        fireValueChanged( event );
    }

    /**
     * Removes the paths from the checked paths set
     *
     * @param paths the paths to be removed
     */
    public void removeCheckingPaths( TreePath[] paths ) {
        for ( TreePath path : paths ) {
            removeCheckingPath( path );
        }
    }

    /**
     * Notifies all listeners that are registered for tree selection events
     * on this object.
     *
     * @see #addTreeCheckingListener
     * @see EventListenerList
     */
    protected void fireValueChanged( TreeCheckingEvent e ) {
        // Guaranteed to return a non-null array
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
            if ( listeners[ i ] == TreeCheckingListener.class ) {
                ( ( TreeCheckingListener ) listeners[ i + 1 ] ).valueChanged( e );
            }
        }
    }

    /**
         * Adds x to the list of listeners that are notified each time the set
         * of checking TreePaths changes.
         *
         * @param x the new listener to be added
         */
    public void addTreeCheckingListener( TreeCheckingListener x ) {
        this.listenerList.add( TreeCheckingListener.class, x );
    }

    /**
         * Removes x from the list of listeners that are notified each time the
         * set of checking TreePaths changes.
         *
         * @param x the listener to remove
         */
    public void removeTreeCheckingListener( TreeCheckingListener x ) {
        this.listenerList.remove( TreeCheckingListener.class, x );
    }

    /**
         * Updates the greyness value value for the given path if there are
         * children with different values. Note: the greyness and cheking of
         * children MUST BE consistent.
         *
         * @param ancestor the path to be grey-updated.
         */
    protected void updatePathGreyness( TreePath ancestor ) {
        boolean value = isPathChecked( ancestor );
        Object ancestorNode = ancestor.getLastPathComponent();
        int childrenNumber = this.model.getChildCount( ancestorNode );
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            Object childNode = this.model.getChild( ancestorNode, childIndex );
            TreePath childPath = ancestor.pathByAddingChild( childNode );
            if ( isPathGreyed( childPath ) ) {
                addToGreyedPathsSet( ancestor );
                return ;
            }
            if ( isPathChecked( childPath ) != value ) {
                addToGreyedPathsSet( ancestor );
                return ;
            }
        }
        removeFromGreyedPathsSet( ancestor );
    }

    /**
         * Updates the greyness of sub-tree starting at path.
         *
         * @param path the root of the sub-tree to be grey-updated
         */
    public void updateSubTreeGreyness( TreePath path ) {
        if ( pathHasChildrenWithValue( path, !isPathChecked( path ) ) ) {
            addToGreyedPathsSet( path );
        }
        else {
            removeFromGreyedPathsSet( path );
        }
        if ( isPathGreyed( path ) ) {
            for ( TreePath childPath : getChildrenPath( path ) ) {
                updateSubTreeGreyness( childPath );
            }
            return ;
        }
        else {
            ungreySubTree( path );
        }
    }

    /**
         * Updates the greyness state of the entire tree.
         */
    public void updateTreeGreyness() {
        updateSubTreeGreyness( new TreePath( this.model.getRoot() ) );
    }

    public enum ChildrenChecking {
        ALL_CHECKED, HALF_CHECKED, ALL_UNCHECKED, NO_CHILDREN
    }

    public ChildrenChecking getChildrenChecking( TreePath path ) {
        Object node = path.getLastPathComponent();
        int childrenNumber = this.model.getChildCount( node );
        boolean someChecked = false;
        boolean someUnchecked = false;
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            TreePath childPath = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
            if ( isPathGreyed( childPath ) ) {
                return ChildrenChecking.HALF_CHECKED;
            }
            // not greyed
            if ( isPathChecked( childPath ) ) {
                if ( someUnchecked ) {
                    return ChildrenChecking.HALF_CHECKED;
                }
                someChecked = true;
            }
            else {
                if ( someChecked ) {
                    return ChildrenChecking.HALF_CHECKED;
                }
                someUnchecked = true;
            }
        }
        if ( someChecked ) {
            return ChildrenChecking.ALL_CHECKED;
        }
        if ( someUnchecked ) {
            return ChildrenChecking.ALL_UNCHECKED;
        }
        return ChildrenChecking.NO_CHILDREN;
    }

    /**
     * Note: The checking and the greyness of children MUST be consistent to
     * work properly.
     *
     * @return true if exists an unchecked node in the subtree of path.
     * @param path the root of the subtree to be checked.
     */
    public boolean pathHasUncheckedChildren( TreePath path ) {
        Object node = path.getLastPathComponent();
        int childrenNumber = this.model.getChildCount( node );
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            TreePath childPath = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
            if ( isPathGreyed( childPath ) | !isPathChecked( childPath ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if exists a checked node in the subtree of path.
     * @param path the root of the subtree to be checked.
     */
    public boolean pathHasCheckedChildren( TreePath path ) {
        return pathHasChildrenWithValue( path, true );
    }

    /**
     * @return true if exists a node with checked status value in the
     *         subtree of path.
     * @param path the root of the subtree to be searched.
     * @param value the value to be found.
     */
    protected boolean pathHasChildrenWithValue( TreePath path, boolean value ) {
        Object node = path.getLastPathComponent();
        int childrenNumber = this.model.getChildCount( node );
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            TreePath childPath = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
            if ( isPathChecked( childPath ) == value ) {
                return true;
            }
        }
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            TreePath childPath = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
            if ( pathHasChildrenWithValue( childPath, value ) ) {
                return true;
            }
        }
        return false;
    }

    /**
         * @return true if exists a child of node with a value different from
         *         itself.
         * @param path the root path of the tree to be checked.
         */
    public boolean hasDifferentChildren( TreePath path ) {
        return pathHasChildrenWithValue( path, !isPathChecked( path ) );
    }

    /**
         * Update the grayness value of the parents of path. Note: the greyness
         * and checking of the other nodes (not ancestors) MUST BE consistent.
         *
         * @param path the treepath containing the ancestors to be grey-updated
         */
    public void updateAncestorsGreyness( TreePath path ) {
        TreePath[] parents = new TreePath[ path.getPathCount() ];
        parents[ 0 ] = path;
        boolean greyAll = isPathGreyed( path );
        for ( int i = 1; i < parents.length; i++ ) {
            parents[ i ] = parents[ i - 1 ].getParentPath();
            if ( greyAll ) {
                addToGreyedPathsSet( parents[ i ] );
            }
            else {
                updatePathGreyness( parents[ i ] );
                greyAll = isPathGreyed( parents[ i ] );
            }
        }
    }

    /**
         * Return the paths that are children of path, using methods of
         * TreeModel. Nodes don't have to be of type TreeNode.
         *
         * @param path the parent path
         * @return the array of children path
         */
    protected TreePath[] getChildrenPath( TreePath path ) {
        Object node = path.getLastPathComponent();
        int childrenNumber = this.model.getChildCount( node );
        TreePath[] childrenPath = new TreePath[ childrenNumber ];
        for ( int childIndex = 0; childIndex < childrenNumber; childIndex++ ) {
            childrenPath[ childIndex ] = path.pathByAddingChild( this.model.getChild( node, childIndex ) );
        }
        return childrenPath;
    }

    public TreeModel getTreeModel() {
        return this.model;
    }

    /**
         * Sets the specified tree model. The current cheking is cleared.
         */
    public void setTreeModel( TreeModel newModel ) {
        TreeModel oldModel = this.model;
        if ( oldModel != null ) {
            oldModel.removeTreeModelListener( this.propagateCheckingListener );
        }
        this.model = newModel;
        if ( newModel != null ) {
            newModel.addTreeModelListener( this.propagateCheckingListener );
        }
        clearChecking();
    }

    /**
         * Return a string that describes the tree model including the values of
         * checking, enabling, greying.
         */
    @Override
    public String toString() {
        return toString( new TreePath( this.model.getRoot() ) );
    }

    /**
         * Convenience method for getting a string that describes the tree
         * starting at path.
         *
         * @param path the treepath root of the tree
         */
    private String toString( TreePath path ) {
        String checkString = "n";
        String greyString = "n";
        String enableString = "n";
        if ( isPathChecked( path ) ) {
            checkString = "y";
        }
        if ( isPathEnabled( path ) ) {
            enableString = "y";
        }
        if ( isPathGreyed( path ) ) {
            greyString = "y";
        }
        String description = "Path checked: " + checkString + " greyed: " + greyString + " enabled: " + enableString + " Name: "
                + path.toString() + "\n";
        for ( TreePath childPath : getChildrenPath( path ) ) {
            description += toString( childPath );
        }
        return description;
    }

}
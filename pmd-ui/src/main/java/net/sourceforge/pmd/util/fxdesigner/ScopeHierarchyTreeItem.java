/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.util.List;
import java.util.Map.Entry;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

import javafx.scene.control.TreeItem;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ScopeHierarchyTreeItem extends TreeItem<Object> {

    private ScopeHierarchyTreeItem(Object scopeOrDecl) {
        super(scopeOrDecl);
        setExpanded(true);
    }


    /**
     * Gets the scope hierarchy of a node.
     *
     * @param node Node
     *
     * @return Root of the tree
     */
    public static ScopeHierarchyTreeItem buildAscendantHierarchy(Node node) {
        ScopeHierarchyTreeItem item = buildAscendantHierarchyHelper(getScope(node));

        if (item == null) {
            return null;
        }

        while (item.getParent() != null) {
            item = (ScopeHierarchyTreeItem) item.getParent();
        }

        return item;

    }


    private static ScopeHierarchyTreeItem buildAscendantHierarchyHelper(Scope scope) {
        if (scope == null) {
            return null;
        }

        ScopeHierarchyTreeItem scopeTreeNode = new ScopeHierarchyTreeItem(scope);

        for (Entry<NameDeclaration, List<NameOccurrence>> entry : scope.getDeclarations().entrySet()) {
            ScopeHierarchyTreeItem nameDeclaration = new ScopeHierarchyTreeItem(entry.getKey());
            scopeTreeNode.getChildren().add(nameDeclaration);
        }

        ScopeHierarchyTreeItem parent = buildAscendantHierarchyHelper(scope.getParent());

        if (parent == null) {
            return scopeTreeNode;
        } else {
            parent.getChildren().add(scopeTreeNode);
            return scopeTreeNode;
        }
    }


    private static Scope getScope(Node node) {
        if (node instanceof ScopedNode) {
            return ((ScopedNode) node).getScope();
        }
        return null;
    }


}

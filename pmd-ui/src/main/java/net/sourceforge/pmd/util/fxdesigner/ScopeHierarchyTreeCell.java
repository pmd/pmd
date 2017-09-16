/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

import javafx.scene.control.TreeCell;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ScopeHierarchyTreeCell extends TreeCell<Object> {

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item instanceof Scope ? getTextForScope((Scope) item)
                                          : getTextForDeclaration((NameDeclaration) item));
        }
    }


    private String getTextForScope(Scope scope) {
        return scope.getClass().getSimpleName();
    }


    private String getTextForDeclaration(NameDeclaration declaration) {

        Class<?> type = declaration.getNode() instanceof TypeNode ? ((TypeNode) declaration.getNode()).getType()
                                                                   : null;

        return declaration.getName() + (type != null ? " : " + type.getSimpleName() : "");
    }

}

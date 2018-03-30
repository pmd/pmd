/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.controls;

import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.util.fxdesigner.MainDesignerController;

import javafx.scene.control.TreeCell;


/**
 * Renders scope nodes and declaration in the scope treeview.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ScopeHierarchyTreeCell extends TreeCell<Object> {

    private final MainDesignerController controller;


    public ScopeHierarchyTreeCell(MainDesignerController controller) {
        this.controller = controller;
    }
    
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

        StringBuilder sb = new StringBuilder();
        if (declaration instanceof MethodNameDeclaration
            || declaration instanceof net.sourceforge.pmd.lang.plsql.symboltable.MethodNameDeclaration) {
            sb.append("Method ");
        } else if (declaration instanceof VariableNameDeclaration
                   || declaration instanceof net.sourceforge.pmd.lang.plsql.symboltable.VariableNameDeclaration) {
            sb.append("Variable ");
        } else if (declaration instanceof ClassNameDeclaration
                   || declaration instanceof net.sourceforge.pmd.lang.plsql.symboltable.ClassNameDeclaration) {
            sb.append("Class ");
        }

        Class<?> type = declaration.getNode() instanceof TypeNode ? ((TypeNode) declaration.getNode()).getType()
                                                                  : null;

        sb.append(declaration.getName());

        if (type != null) {
            sb.append(" : ").append(type.getSimpleName());
        }

        sb.append(" (l. ").append(declaration.getNode().getBeginLine()).append(")");

        return sb.toString();
    }

}

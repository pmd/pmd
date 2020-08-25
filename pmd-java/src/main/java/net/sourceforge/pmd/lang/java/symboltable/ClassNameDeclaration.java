/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;

public class ClassNameDeclaration extends AbstractNameDeclaration implements TypedNameDeclaration {

    public ClassNameDeclaration(JavaNode node) {
        super(node);
    }

    @Override
    public String toString() {
        if (node instanceof ASTAnyTypeDeclaration) {
            return PrettyPrintingUtil.kindName((ASTAnyTypeDeclaration) node) + node.getImage();
        }
        return "anonymous";
    }

    public Node getAccessNodeParent() {
        return node;
    }

    @Override
    public String getTypeImage() {
        return getTypeNode().getImage();
    }

    @Override
    public Class<?> getType() {
        if (node instanceof ASTAnyTypeDeclaration) {
            return ((ASTAnyTypeDeclaration) node).getType();
        }
        return null;
    }

    /**
     * Null for anonymous classes.
     */
    @Override
    public TypeNode getTypeNode() {
        return node instanceof TypeNode ? (TypeNode) node : null;
    }
}

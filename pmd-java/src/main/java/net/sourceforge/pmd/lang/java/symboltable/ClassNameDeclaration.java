/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;

public class ClassNameDeclaration extends AbstractNameDeclaration implements TypedNameDeclaration {

    public ClassNameDeclaration(ASTClassOrInterfaceDeclaration node) {
        super(node);
    }

    public ClassNameDeclaration(ASTEnumDeclaration node) {
        super(node);
    }

    public String toString() {
        if (node instanceof ASTClassOrInterfaceDeclaration) {
            if (((ASTClassOrInterfaceDeclaration) node).isInterface()) {
                return "Interface " + node.getImage();
            } else {
                return "Class " + node.getImage();
            }
        } else {
            return "Enum " + node.getImage();
        }
    }

    public Node getAccessNodeParent() {
        return node;
    }

    public String getTypeImage() {
        return ((ASTClassOrInterfaceDeclaration) node).getImage();
    }

    public Class<?> getType() {
        return ((ASTClassOrInterfaceDeclaration) node).getType();
    }
}

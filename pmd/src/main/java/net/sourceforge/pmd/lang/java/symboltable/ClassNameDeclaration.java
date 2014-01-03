/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.symboltable.AbstractNameDeclaration;

public class ClassNameDeclaration extends AbstractNameDeclaration implements TypedNameDeclaration {

    public ClassNameDeclaration(ASTClassOrInterfaceDeclaration node) {
        super(node);
    }

    public String toString() {
        return "Class " + node.getImage();
    }

    public Node getAccessNodeParent() {
        return node;
    }

    public String getTypeImage() {
        return ((ASTClassOrInterfaceDeclaration)node).getImage();
    }

    public Class<?> getType() {
        return ((ASTClassOrInterfaceDeclaration)node).getType();
    }
}

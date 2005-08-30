package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;

public class ClassNameDeclaration extends AbstractNameDeclaration {

    public ClassNameDeclaration(ASTClassOrInterfaceDeclaration node) {
        super(node);
    }

    public String toString() {
        return "Class " + node.getImage();
    }

}

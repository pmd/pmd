package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;

public class ClassNameDeclaration extends AbstractNameDeclaration implements NameDeclaration {

    public ClassNameDeclaration(ASTUnmodifiedClassDeclaration node) {
        super(node);
    }

    public String toString() {
        return "Class " + node.getImage() + ":" + node.getBeginLine();
    }

}

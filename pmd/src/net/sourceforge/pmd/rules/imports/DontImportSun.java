package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

public class DontImportSun extends AbstractRule {

    public Object visit(ASTImportDeclaration node, Object data) {
        String img = ((SimpleNode) node.jjtGetChild(0)).getImage();
        if (img.startsWith("sun.") && !img.startsWith("sun.misc.Signal")) {
            addViolation(data, node);
        }
        return data;
    }

}

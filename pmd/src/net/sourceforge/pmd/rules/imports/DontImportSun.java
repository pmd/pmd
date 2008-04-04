package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTImportDeclaration;

public class DontImportSun extends AbstractRule {

    public Object visit(ASTImportDeclaration node, Object data) {
        String img = node.jjtGetChild(0).getImage();
        if (img.startsWith("sun.") && !img.startsWith("sun.misc.Signal")) {
            addViolation(data, node);
        }
        return data;
    }

}

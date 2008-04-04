package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class DontImportSun extends AbstractJavaRule {

    public Object visit(ASTImportDeclaration node, Object data) {
        String img = node.jjtGetChild(0).getImage();
        if (img.startsWith("sun.") && !img.startsWith("sun.misc.Signal")) {
            addViolation(data, node);
        }
        return data;
    }

}

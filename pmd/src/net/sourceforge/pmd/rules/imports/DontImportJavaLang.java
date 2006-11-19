package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

public class DontImportJavaLang extends AbstractRule {

    public Object visit(ASTImportDeclaration node, Object data) {
        if (node.isStatic()) {
            return data;
        }
        String img = ((SimpleNode) node.jjtGetChild(0)).getImage();
        if (img.startsWith("java.lang")) {
            if (img.startsWith("java.lang.ref")
                    || img.startsWith("java.lang.reflect")
                    || img.startsWith("java.lang.annotation")
                    || img.startsWith("java.lang.instrument")
                    || img.startsWith("java.lang.management")) {
                return data;
            }

            addViolation(data, node);
        }
        return data;
    }

}

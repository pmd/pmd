package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;

public class UnnecessaryCaseChange extends AbstractRule {

    public Object visit(ASTPrimaryExpression exp, Object data) {
        if (exp.jjtGetNumChildren() < 4) {
            return data;
        }

        // verify PrimaryPrefix/Name[ends-with(@Image, toUpperCase]
        if (!(exp.jjtGetChild(0) instanceof ASTPrimaryPrefix)) {
            return data;
        }

        ASTPrimaryPrefix prefix = (ASTPrimaryPrefix)exp.jjtGetChild(0);
        if (prefix.jjtGetNumChildren() != 1 || !(prefix.jjtGetChild(0) instanceof ASTName)) {
           return data;
        }

        ASTName name = (ASTName)prefix.jjtGetChild(0);
        if (name.getImage() == null || !(name.getImage().endsWith("toUpperCase") || name.getImage().endsWith("toLowerCase"))){
            return data;
        }

        // verify PrimarySuffix[@Image='equals']
        if (!(exp.jjtGetChild(2) instanceof ASTPrimarySuffix)) {
            return data;
        }

        ASTPrimarySuffix suffix = (ASTPrimarySuffix)exp.jjtGetChild(2);
        if (suffix.getImage() == null || !suffix.getImage().equals("equals")) {
            return data;
        }

        addViolation(data, exp);

        return data;
    }
}

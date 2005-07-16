package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.List;

public class PositionLiteralsFirstInComparisons extends AbstractRule {

    public Object visit(ASTPrimaryExpression exp, Object data) {
        // the first prefix ends with a '.equals'
        if (exp.jjtGetNumChildren() < 2 ||  !(exp.jjtGetChild(0) instanceof ASTPrimaryPrefix)) {
            return data;
        }
        ASTPrimaryPrefix prefix = (ASTPrimaryPrefix)exp.jjtGetChild(0);
        if (prefix.jjtGetNumChildren() != 1 || !(prefix.jjtGetChild(0) instanceof ASTName)) {
            return data;
        }
        ASTName name = (ASTName)prefix.jjtGetChild(0);
        if (name.getImage() == null || !name.getImage().endsWith(".equals")) {
            return data;
        }

        // second child is suffix that has a Literal child
        if (!(exp.jjtGetChild(1) instanceof ASTPrimarySuffix)) {
            return data;
        }
        ASTPrimarySuffix suffix = (ASTPrimarySuffix)exp.jjtGetChild(1);
        List literals = suffix.findChildrenOfType(ASTLiteral.class);
        if (literals.isEmpty()) {
            return data;
        }

        addViolation(data, exp);

        return data;
    }
}

package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.Iterator;
import java.util.List;

public class UseIndexOfChar extends AbstractRule {
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (!node.getNameDeclaration().getTypeImage().equals("String")) {
            return data;
        }
        for (Iterator i = node.getUsages().iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence) i.next();
            if (occ.getNameForWhichThisIsAQualifier() != null && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("indexOf") != -1) {
                SimpleNode parent = (SimpleNode) occ.getLocation().jjtGetParent().jjtGetParent();
                if (parent instanceof ASTPrimaryExpression) {
                    List literals = parent.findChildrenOfType(ASTLiteral.class);
                    for (Iterator j = literals.iterator(); j.hasNext();) {
                        ASTLiteral literal = (ASTLiteral) j.next();
                        if (literal.getImage().length() == 3 && literal.getImage().charAt(0) == '\"') {
                            addViolation(data, occ.getLocation());
                        }
                    }
                }
            }
        }
        return data;
    }
}


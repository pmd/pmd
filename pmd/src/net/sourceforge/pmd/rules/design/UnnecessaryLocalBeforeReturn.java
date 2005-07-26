package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnnecessaryLocalBeforeReturn extends AbstractRule {

    public Object visit(ASTMethodDeclaration meth, Object data) {
        // skip void/abstract/native method
        if (meth.isVoid() || meth.isAbstract() || meth.isNative()) {
            return data;
        }
        return super.visit(meth, data);
    }

    public Object visit(ASTReturnStatement rtn, Object data) {
        // skip returns of literals
        ASTName name = (ASTName)rtn.getFirstChildOfType(ASTName.class);
        if (name == null) {
            return data;
        }

        // skip 'complicated' expressions
        if (rtn.findChildrenOfType(ASTExpression.class).size() > 1 || rtn.findChildrenOfType(ASTPrimaryExpression.class).size() > 1) {
            return data;
        }

        Map vars = name.getScope().getVariableDeclarations();
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration key = (VariableNameDeclaration)i.next();
            List usages = (List)vars.get(key);
            for (Iterator j = usages.iterator(); j.hasNext();) {
                NameOccurrence occ = (NameOccurrence)j.next();
                if (occ.getLocation().equals(name)) {
                    // only check declarations that occur one line earlier
                    if (key.getNode().getBeginLine() == name.getBeginLine()-1) {
                        String var = name.getImage();
                        if (var.indexOf('.') != -1) {
                            var = var.substring(0, var.indexOf('.'));
                        }
                        addViolation(data, rtn, var);
                    }
                }
            }
        }
        return data;
    }
}

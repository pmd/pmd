/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class UnnecessaryLocalBeforeReturnRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTMethodDeclaration meth, Object data) {
        // skip void/abstract/native method
        if (meth.isVoid() || meth.isAbstract() || meth.isNative()) {
            return data;
        }
        return super.visit(meth, data);
    }

    @Override
    public Object visit(ASTReturnStatement rtn, Object data) {
        // skip returns of literals
        ASTName name = rtn.getFirstDescendantOfType(ASTName.class);
        if (name == null) {
            return data;
        }

        // skip 'complicated' expressions
        if (rtn.findDescendantsOfType(ASTExpression.class).size() > 1
                || rtn.findDescendantsOfType(ASTPrimaryExpression.class).size() > 1 || isMethodCall(rtn)) {
            return data;
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = name.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration variableDeclaration = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();

            if (usages.size() == 1) { // If there is more than 1 usage, then it's not only returned
                NameOccurrence occ = usages.get(0);

                if (occ.getLocation().equals(name) && isNotAnnotated(variableDeclaration)) {
                    String var = name.getImage();
                    if (var.indexOf('.') != -1) {
                        var = var.substring(0, var.indexOf('.'));
                    }
                    addViolation(data, rtn, var);
                }
            }
        }
        return data;
    }

    private boolean isNotAnnotated(VariableNameDeclaration variableDeclaration) {
        AccessNode accessNodeParent = variableDeclaration.getAccessNodeParent();
        return !accessNodeParent.hasDescendantOfType(ASTAnnotation.class);
    }

    /**
     * Determine if the given return statement has any embedded method calls.
     *
     * @param rtn
     *            return statement to analyze
     * @return true if any method calls are made within the given return
     */
    private boolean isMethodCall(ASTReturnStatement rtn) {
        List<ASTPrimarySuffix> suffix = rtn.findDescendantsOfType(ASTPrimarySuffix.class);
        for (ASTPrimarySuffix element : suffix) {
            if (element.isArguments()) {
                return true;
            }
        }
        return false;
    }
}

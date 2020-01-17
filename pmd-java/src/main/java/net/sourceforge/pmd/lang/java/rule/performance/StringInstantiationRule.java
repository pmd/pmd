/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class StringInstantiationRule extends AbstractJavaRule {

    public StringInstantiationRule() {
        addRuleChainVisit(ASTAllocationExpression.class);
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (!(node.getChild(0) instanceof ASTClassOrInterfaceType)) {
            return data;
        }

        if (!TypeHelper.isA((ASTClassOrInterfaceType) node.getChild(0), String.class)) {
            return data;
        }

        if (isArrayAccess(node)) {
            addViolation(data, node);
            return data;
        }

        List<ASTExpression> exp = node.findDescendantsOfType(ASTExpression.class);
        if (exp.size() >= 2) {
            return data;
        }

        if (node.hasDescendantOfAnyType(ASTArrayDimsAndInits.class, ASTAdditiveExpression.class)) {
            return data;
        }

        ASTName name = node.getFirstDescendantOfType(ASTName.class);
        // Literal, i.e., new String("foo")
        if (name == null) {
            addViolation(data, node);
            return data;
        }

        NameDeclaration nd = name.getNameDeclaration();
        if (nd == null) {
            return data;
        }

        if (nd instanceof TypedNameDeclaration && TypeHelper.isExactlyAny((TypedNameDeclaration) nd, String.class)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean isArrayAccess(ASTAllocationExpression node) {
        ASTArguments arguments = node.getFirstChildOfType(ASTArguments.class);
        if (arguments == null || arguments.getArgumentCount() != 1) {
            return false;
        }

        Node firstArg = arguments.getFirstChildOfType(ASTArgumentList.class).getChild(0);
        ASTPrimaryExpression primary = firstArg.getFirstChildOfType(ASTPrimaryExpression.class);
        if (primary == null || primary.getType() != String.class) {
            return false;
        }

        ASTPrimarySuffix suffix = primary.getFirstChildOfType(ASTPrimarySuffix.class);
        return suffix != null && suffix.isArrayDereference();
    }
}

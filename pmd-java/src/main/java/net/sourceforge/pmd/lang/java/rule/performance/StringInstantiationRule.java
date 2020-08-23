/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static net.sourceforge.pmd.lang.ast.NodeStream.asInstanceOf;

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
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
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

        if (!TypeTestUtil.isA(String.class, (ASTClassOrInterfaceType) node.getChild(0))) {
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

        if (node.descendants().map(asInstanceOf(ASTArrayDimsAndInits.class, ASTAdditiveExpression.class)).nonEmpty()) {
            return data;
        }

        ASTName name = node.getFirstDescendantOfType(ASTName.class);
        // Literal, i.e., new String("foo")
        if (name == null) {
            addViolation(data, node);
            return data;
        }

        NameDeclaration nd = name.getNameDeclaration();
        if (!(nd instanceof TypedNameDeclaration)) {
            return data;
        }

        if (TypeTestUtil.isA(String.class, ((TypedNameDeclaration) nd).getTypeNode())) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean isArrayAccess(ASTAllocationExpression node) {
        ASTArguments arguments = node.getFirstChildOfType(ASTArguments.class);
        if (arguments == null || arguments.size() != 1) {
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

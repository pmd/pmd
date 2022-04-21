/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * If a method or constructor receives an array as an argument, the array should
 * be cloned instead of directly stored. This prevents future changes from the
 * user from affecting the original array.
 *
 * @since Created on Jan 17, 2005
 * @author mgriffa
 */
public class ArrayIsStoredDirectlyRule extends AbstractSunSecureRule {
    private static final PropertyDescriptor<Boolean> ALLOW_PRIVATE =
            PropertyFactory.booleanProperty("allowPrivate")
                .defaultValue(true)
                .desc("If true, allow private methods/constructors to store arrays directly")
                .build();

    public ArrayIsStoredDirectlyRule() {
        definePropertyDescriptor(ALLOW_PRIVATE);
        addRuleChainVisit(ASTConstructorDeclaration.class);
        addRuleChainVisit(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.isPrivate() && getProperty(ALLOW_PRIVATE)) {
            return data;
        }

        ASTFormalParameter[] arrs = getArrays(node.getFormalParameters());
        // TODO check if one of these arrays is stored in a non local
        // variable
        List<ASTBlockStatement> bs = node.findDescendantsOfType(ASTBlockStatement.class);
        checkAll(data, arrs, bs);
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isPrivate() && getProperty(ALLOW_PRIVATE)) {
            return data;
        }

        final ASTFormalParameters params = node.getFirstDescendantOfType(ASTFormalParameters.class);
        ASTFormalParameter[] arrs = getArrays(params);
        checkAll(data, arrs, node.findDescendantsOfType(ASTBlockStatement.class));
        return data;
    }

    private void checkAll(Object context, ASTFormalParameter[] arrs, List<ASTBlockStatement> bs) {
        for (ASTFormalParameter element : arrs) {
            checkForDirectAssignment(context, element, bs);
        }
    }

    private String getExpressionVarName(Node e) {
        String assignedVar = getFirstNameImage(e);
        if (isMethodCall(e)) {
            return null;
        }
        if (assignedVar == null) {
            ASTPrimaryPrefix prefix = null;
            ASTPrimarySuffix suffix = null;
            if (e.getNumChildren() > 0 && e.getChild(0) instanceof ASTPrimaryExpression) {
                ASTPrimaryExpression primaryExpression = (ASTPrimaryExpression) e.getChild(0);
                prefix = (ASTPrimaryPrefix) primaryExpression.getChild(0);
                suffix = primaryExpression.getFirstChildOfType(ASTPrimarySuffix.class);
            }
            if (suffix != null) {
                assignedVar = suffix.getImage();
                if (prefix != null) {
                    if (prefix.usesThisModifier()) {
                        assignedVar = "this." + assignedVar;
                    } else if (prefix.usesSuperModifier()) {
                        assignedVar = "super." + assignedVar;
                    }
                }
            }
        }
        return assignedVar;
    }

    private boolean isMethodCall(Node e) {
        if (e.getNumChildren() == 1 && e.getChild(0) instanceof ASTPrimaryExpression) {
            ASTPrimaryExpression primaryExpression = (ASTPrimaryExpression) e.getChild(0);
            if (primaryExpression.getNumChildren() > 1) {
                ASTPrimarySuffix suffix = (ASTPrimarySuffix) primaryExpression.getChild(1);
                return suffix.isArguments();
            }
        }
        return false;
    }

    /**
     * Checks if the variable designed in parameter is written to a field (not
     * local variable) in the statements.
     */
    private void checkForDirectAssignment(Object ctx, final ASTFormalParameter parameter,
            final List<ASTBlockStatement> bs) {
        final ASTVariableDeclaratorId vid = parameter.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        final String varName = vid.getName();
        for (ASTBlockStatement b : bs) {
            if (b.getChild(0) instanceof ASTStatement && b.getChild(0).getChild(0) instanceof ASTStatementExpression) {
                final ASTStatementExpression se = b.getFirstDescendantOfType(ASTStatementExpression.class);
                if (se == null
                        || se.getNumChildren() < 2
                        || !(se.getChild(0) instanceof ASTPrimaryExpression)
                        || !(se.getChild(1) instanceof ASTAssignmentOperator)) {
                    continue;
                }
                String assignedVar = getExpressionVarName(se);
                if (assignedVar == null) {
                    continue;
                }

                ASTPrimaryExpression pe = (ASTPrimaryExpression) se.getChild(0);
                Node n = pe.getFirstParentOfType(ASTMethodDeclaration.class);
                if (n == null) {
                    n = pe.getFirstParentOfType(ASTConstructorDeclaration.class);
                    if (n == null) {
                        continue;
                    }
                }
                if (!isLocalVariable(assignedVar, n)) {
                    // TODO could this be more clumsy? We really
                    // need to build out the PMD internal framework more
                    // to support simply queries like "isAssignedTo()" or
                    // something
                    if (se.getNumChildren() < 3) {
                        continue;
                    }
                    ASTExpression e = (ASTExpression) se.getChild(2);
                    if (e.hasDescendantOfType(ASTEqualityExpression.class)) {
                        continue;
                    }
                    String val = getExpressionVarName(e);
                    if (val == null) {
                        continue;
                    }
                    ASTPrimarySuffix foo = e.getFirstDescendantOfType(ASTPrimarySuffix.class);
                    if (foo != null && foo.isArrayDereference()) {
                        continue;
                    }

                    if (val.equals(varName)) {
                        Node md = parameter.getFirstParentOfType(ASTMethodDeclaration.class);
                        if (md == null) {
                            md = pe.getFirstParentOfType(ASTConstructorDeclaration.class);
                        }
                        if (!isLocalVariable(varName, md)) {
                            RuleContext ruleContext = (RuleContext) ctx;
                            ruleContext.addViolation(e, varName);
                        }
                    }
                }
            }
        }
    }

    private ASTFormalParameter[] getArrays(ASTFormalParameters params) {
        final List<ASTFormalParameter> l = params.findChildrenOfType(ASTFormalParameter.class);
        if (l != null && !l.isEmpty()) {
            List<ASTFormalParameter> l2 = new ArrayList<>();
            for (ASTFormalParameter fp : l) {
                if (fp.getVariableDeclaratorId().hasArrayType() || fp.isVarargs()) {
                    l2.add(fp);
                }
            }
            return l2.toArray(new ASTFormalParameter[0]);
        }
        return new ASTFormalParameter[0];
    }

}

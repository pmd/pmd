/*
 * Created on Jan 17, 2005
 *
 * $Id$
 */
package net.sourceforge.pmd.lang.java.rule.sunsecure;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;

/**
 * @author mgriffa
 */
public class ArrayIsStoredDirectlyRule extends AbstractSunSecureRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        ASTFormalParameter[] arrs = getArrays(node.getParameters());
        if (arrs != null) {
            //TODO check if one of these arrays is stored in a non local variable
            List<ASTBlockStatement> bs = node.findDescendantsOfType(ASTBlockStatement.class);
            checkAll(data, arrs, bs);
        }
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        final ASTFormalParameters params = node.getFirstDescendantOfType(ASTFormalParameters.class);
        ASTFormalParameter[] arrs = getArrays(params);
        if (arrs != null) {
            checkAll(data, arrs, node.findDescendantsOfType(ASTBlockStatement.class));
        }
        return data;
    }

    private void checkAll(Object context, ASTFormalParameter[] arrs, List<ASTBlockStatement> bs) {
        for (ASTFormalParameter element : arrs) {
            checkForDirectAssignment(context, element, bs);
        }
    }

    /**
     * Checks if the variable designed in parameter is written to a field (not local variable) in the statements.
     */
    private boolean checkForDirectAssignment(Object ctx, final ASTFormalParameter parameter, final List<ASTBlockStatement> bs) {
        final ASTVariableDeclaratorId vid = parameter.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        final String varName = vid.getImage();
        for (ASTBlockStatement b: bs) {
            if (b.hasDescendantOfType(ASTAssignmentOperator.class)) {
                final ASTStatementExpression se = b.getFirstDescendantOfType(ASTStatementExpression.class);
                if (se == null || !(se.jjtGetChild(0) instanceof ASTPrimaryExpression)) {
                    continue;
                }
                ASTPrimaryExpression pe = (ASTPrimaryExpression) se.jjtGetChild(0);
                String assignedVar = getFirstNameImage(pe);
                if (assignedVar == null) {
                    ASTPrimarySuffix suffix = se.getFirstDescendantOfType(ASTPrimarySuffix.class);
                    if (suffix == null) {
                        continue;
                    }
                    assignedVar = suffix.getImage();
                }

                Node n = pe.getFirstParentOfType(ASTMethodDeclaration.class);
                if (n == null) {
					n = pe.getFirstParentOfType(ASTConstructorDeclaration.class);
					if (n == null) {
						continue;
					}
				}
                if (!isLocalVariable(assignedVar, n)) {
                    // TODO could this be more clumsy?  We really
                    // need to build out the PMD internal framework more
                    // to support simply queries like "isAssignedTo()" or something
                    if (se.jjtGetNumChildren() < 3) {
                        continue;
                    }
                    ASTExpression e = (ASTExpression) se.jjtGetChild(2);
                    if (e.hasDescendantOfType(ASTEqualityExpression.class)) {
                        continue;
                    }
                    String val = getFirstNameImage(e);
                    if (val == null) {
                        ASTPrimarySuffix foo = se.getFirstDescendantOfType(ASTPrimarySuffix.class);
                        if (foo == null) {
                            continue;
                        }
                        val = foo.getImage();
                    }
                    if (val == null) {
                        continue;
                    }
                    ASTPrimarySuffix foo = se.getFirstDescendantOfType(ASTPrimarySuffix.class);
                    if (foo != null && foo.isArrayDereference()) {
                        continue;
                    }

                    if (val.equals(varName)) {
                	Node md = parameter.getFirstParentOfType(ASTMethodDeclaration.class);
                        if (md == null) {
                        	md = pe.getFirstParentOfType(ASTConstructorDeclaration.class);
        				}
                        if (!isLocalVariable(varName, md)) {
                            addViolation(ctx, parameter, varName);
                        }
                    }
                }
            }
        }
        return false;
    }

    private final ASTFormalParameter[] getArrays(ASTFormalParameters params) {
        final List<ASTFormalParameter> l = params.findChildrenOfType(ASTFormalParameter.class);
        if (l != null && !l.isEmpty()) {
            List<ASTFormalParameter> l2 = new ArrayList<ASTFormalParameter>();
            for (ASTFormalParameter fp: l) {
                if (fp.isArray()) {
		    l2.add(fp);
		}
            }
            return l2.toArray(new ASTFormalParameter[l2.size()]);
        }
        return null;
    }

}

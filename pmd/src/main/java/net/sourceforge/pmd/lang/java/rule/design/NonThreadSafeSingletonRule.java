/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;

public class NonThreadSafeSingletonRule extends AbstractJavaRule {

    private Map<String, ASTFieldDeclaration> fieldDecls = new HashMap<String, ASTFieldDeclaration>();

    private boolean checkNonStaticMethods = true;
    private boolean checkNonStaticFields = true;

    private static final BooleanProperty CHECK_NON_STATIC_METHODS_DESCRIPTOR = new BooleanProperty(
	    "checkNonStaticMethods", "Check for non-static methods.  Do not set this to false and checkNonStaticFields to true.", true, 1.0f);
    private static final BooleanProperty CHECK_NON_STATIC_FIELDS_DESCRIPTOR = new BooleanProperty(
	    "checkNonStaticFields", "Check for non-static fields.  Do not set this to true and checkNonStaticMethods to false.", false, 2.0f);

    public NonThreadSafeSingletonRule() {
	definePropertyDescriptor(CHECK_NON_STATIC_METHODS_DESCRIPTOR);
	definePropertyDescriptor(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
	fieldDecls.clear();
	checkNonStaticMethods = getProperty(CHECK_NON_STATIC_METHODS_DESCRIPTOR);
	checkNonStaticFields = getProperty(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
	if (checkNonStaticFields || node.isStatic()) {
	    fieldDecls.put(node.getVariableName(), node);
	}
	return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

	if (checkNonStaticMethods && !node.isStatic() || node.isSynchronized()) {
	    return super.visit(node, data);
	}

	List<ASTIfStatement> ifStatements = node.findDescendantsOfType(ASTIfStatement.class);
	for (ASTIfStatement ifStatement : ifStatements) {
	    if (ifStatement.getFirstParentOfType(ASTSynchronizedStatement.class) == null) {
		if (!ifStatement.hasDescendantOfType(ASTNullLiteral.class)) {
		    continue;
		}
		ASTName n = ifStatement.getFirstDescendantOfType(ASTName.class);
		if (n == null || !fieldDecls.containsKey(n.getImage())) {
		    continue;
		}
		List<ASTAssignmentOperator> assigmnents = ifStatement.findDescendantsOfType(ASTAssignmentOperator.class);
		boolean violation = false;
		for (int ix = 0; ix < assigmnents.size(); ix++) {
		    ASTAssignmentOperator oper = assigmnents.get(ix);
		    if (!(oper.jjtGetParent() instanceof ASTStatementExpression)) {
			continue;
		    }
		    ASTStatementExpression expr = (ASTStatementExpression) oper.jjtGetParent();
		    if ((expr.jjtGetChild(0) instanceof ASTPrimaryExpression)
	            && ((ASTPrimaryExpression) expr.jjtGetChild(0)).jjtGetNumChildren() == 1
			    && (((ASTPrimaryExpression) expr.jjtGetChild(0)).jjtGetChild(0) instanceof ASTPrimaryPrefix)) {
			ASTPrimaryPrefix pp = (ASTPrimaryPrefix) ((ASTPrimaryExpression) expr.jjtGetChild(0))
				.jjtGetChild(0);
			String name = null;
			if (pp.usesThisModifier()) {
			    ASTPrimarySuffix priSuf = expr.getFirstDescendantOfType(ASTPrimarySuffix.class);
			    name = priSuf.getImage();
			} else {
			    ASTName astName = (ASTName) pp.jjtGetChild(0);
			    name = astName.getImage();
			}
			if (fieldDecls.containsKey(name)) {
			    violation = true;
			}
		    }
		}
		if (violation) {
		    addViolation(data, ifStatement);
		}
	    }
	}
	return super.visit(node, data);
    }
}

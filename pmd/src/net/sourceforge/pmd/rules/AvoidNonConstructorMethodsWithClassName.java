/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.text.MessageFormat;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;



public class AvoidNonConstructorMethodsWithClassName extends AbstractRule {
	
	public Object visit(ASTInterfaceDeclaration node, Object data) {
		// Skip interfaces
		return super.visit(node, data);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.ast.JavaParserVisitor#visit(net.sourceforge.pmd.ast.ASTFieldDeclaration, java.lang.Object)
	 */
	public Object visit(ASTMethodDeclaration node, Object data) {
		String methodName = node.getMethodName();
		String declaringType = getDeclaringType (node);
		if (methodName!=null && declaringType!=null) {
			if (methodName.equals(declaringType)) {
				RuleContext ctx = (RuleContext) data;
				RuleViolation ruleViolation = createRuleViolation(ctx, node, MessageFormat.format(getMessage(), new Object[]{methodName}));
                ctx.getReport().addRuleViolation(ruleViolation);
			}
		}
		return data;
	}
	
}

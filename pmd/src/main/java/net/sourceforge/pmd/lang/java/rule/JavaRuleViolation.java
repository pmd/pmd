/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.CanSuppressWarnings;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.MethodScope;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * This is a Java RuleViolation. It knows how to try to extract the following
 * extra information from the violation node:
 * <ul>
 * <li>Package name</li>
 * <li>Class name</li>
 * <li>Method name</li>
 * <li>Variable name</li>
 * <li>Suppression indicator</li>
 * </ul>
 */
public class JavaRuleViolation extends ParametricRuleViolation<JavaNode> {

	public JavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node, String message, int beginLine, int endLine) {
		this(rule, ctx, node, message);

		setLines(beginLine, endLine);
	}

	public JavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node, String message) {
		super(rule, ctx, node, message);

		if (node != null) {
			final Scope scope = node.getScope();
			final SourceFileScope sourceFileScope = scope.getEnclosingScope(SourceFileScope.class);

			// Package name is on SourceFileScope
			packageName = sourceFileScope.getPackageName() == null ? ""	: sourceFileScope.getPackageName();

			// Class name is built from enclosing ClassScopes
			setClassNameFrom(node);
			
			// Method name comes from 1st enclosing MethodScope
			if (node.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
				methodName = scope.getEnclosingScope(MethodScope.class).getName();
			}
			// Variable name node specific
			setVariableNameIfExists(node);

			// Check for suppression on this node, on parents, and on contained
			// types for ASTCompilationUnit
			if (!suppressed) {
				suppressed = suppresses(node);
			}
			if (!suppressed && node instanceof ASTCompilationUnit) {
				for (int i = 0; !suppressed && i < node.jjtGetNumChildren(); i++) {
					suppressed = suppresses(node.jjtGetChild(i));
				}
			}
			if (!suppressed) {
				Node parent = node.jjtGetParent();
				while (!suppressed && parent != null) {
					suppressed = suppresses(parent);
					parent = parent.jjtGetParent();
				}
			}
		}
	}

	private void setClassNameFrom(JavaNode node) {
		
		String qualifiedName = null;
		for (ASTClassOrInterfaceDeclaration parent : node.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) {
			String clsName = parent.getScope().getEnclosingScope(ClassScope.class).getClassName();
			if (qualifiedName == null) {
				qualifiedName = clsName;
			} else {
				qualifiedName = clsName + '$' + qualifiedName;
			}
		}
		if (qualifiedName != null) {
			className = qualifiedName;
		}
	}

	private boolean suppresses(final Node node) {
		return node instanceof CanSuppressWarnings
				&& ((CanSuppressWarnings) node).hasSuppressWarningsAnnotationFor(getRule());
	}

	private void setVariableNameIfExists(Node node) {
		if (node instanceof ASTFieldDeclaration) {
			variableName = ((ASTFieldDeclaration) node).getVariableName();
		} else if (node instanceof ASTLocalVariableDeclaration) {
			variableName = ((ASTLocalVariableDeclaration) node)
					.getVariableName();
		} else if (node instanceof ASTVariableDeclarator) {
			variableName = node.jjtGetChild(0).getImage();
		} else if (node instanceof ASTVariableDeclaratorId) {
			variableName = node.getImage();
		} else if (node instanceof ASTFormalParameter) {
		    setVariableNameIfExists(node.getFirstChildOfType(ASTVariableDeclaratorId.class));
		} else {
		    variableName = "";
		}
	}
}

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

			if (!suppressed) {
			    suppressed = isSupressed(node, getRule());
			}
		}
	}

    /**
     * Check for suppression on this node, on parents, and on contained types
     * for ASTCompilationUnit
     * 
     * @param node
     */
    public static boolean isSupressed(Node node, Rule rule) {
        boolean result = suppresses(node, rule);

        if (!result && node instanceof ASTCompilationUnit) {
            for (int i = 0; !result && i < node.jjtGetNumChildren(); i++) {
                result = suppresses(node.jjtGetChild(i), rule);
            }
        }
        if (!result) {
            Node parent = node.jjtGetParent();
            while (!result && parent != null) {
                result = suppresses(parent, rule);
                parent = parent.jjtGetParent();
            }
        }
        return result;
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

	private static boolean suppresses(final Node node, Rule rule) {
		return node instanceof CanSuppressWarnings
				&& ((CanSuppressWarnings) node).hasSuppressWarningsAnnotationFor(rule);
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

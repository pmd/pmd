/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.AbstractRuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.CanSuppressWarnings;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.SourceFileScope;

// FUTURE Move to Java specific language location
/**
 * This is a Java RuleViolation.  It knows how to try to extract the following
 * extra information from the violation node:
 * <ul>
 *    <li>Package name</li>
 *    <li>Class name</li>
 *    <li>Method name</li>
 *    <li>Variable name</li>
 *    <li>Suppression indicator</li>
 * </ul>
 */
public class JavaRuleViolation extends AbstractRuleViolation {
    public JavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node) {
	this(rule, ctx, node, rule.getMessage());
    }

    public JavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node, String specificMsg) {
	super(rule, ctx, node, specificMsg);

	if (node != null) {
	    Scope scope = node.getScope();

	    // Source file does not have an enclosing class scope...
	    if (!SourceFileScope.class.equals(scope.getClass())) {
		className = scope.getEnclosingClassScope().getClassName() == null ? "" : scope.getEnclosingClassScope()
			.getClassName();
	    }

	    // Default to symbol table lookup
	    String qualifiedName = null;
	    for (ASTClassOrInterfaceDeclaration parent : node.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) {
		if (qualifiedName == null) {
		    qualifiedName = parent.getScope().getEnclosingClassScope().getClassName();
		} else {
		    qualifiedName = parent.getScope().getEnclosingClassScope().getClassName() + "$" + qualifiedName;
		}
	    }
	    packageName = scope.getEnclosingSourceFileScope().getPackageName() == null ? "" : scope
		    .getEnclosingSourceFileScope().getPackageName();
	    // Source file does not have an enclosing class scope...
	    if (!SourceFileScope.class.equals(scope.getClass())) {
		className = scope.getEnclosingClassScope().getClassName() == null ? "" : qualifiedName;
	    }
	    methodName = node.getFirstParentOfType(ASTMethodDeclaration.class) == null ? "" : scope
		    .getEnclosingMethodScope().getName();
	    setVariableNameIfExists(node);

	    // Check for suppression
	    List<Node> parentTypes = new ArrayList<Node>();
	    if (node instanceof ASTTypeDeclaration || node instanceof ASTClassOrInterfaceBodyDeclaration
		    || node instanceof ASTFormalParameter || node instanceof ASTLocalVariableDeclaration) {
		parentTypes.add(node);
	    }
	    parentTypes.addAll(node.getParentsOfType(ASTTypeDeclaration.class));
	    parentTypes.addAll(node.getParentsOfType(ASTClassOrInterfaceBodyDeclaration.class));
	    parentTypes.addAll(node.getParentsOfType(ASTFormalParameter.class));
	    parentTypes.addAll(node.getParentsOfType(ASTLocalVariableDeclaration.class));
	    for (Node parentType : parentTypes) {
		CanSuppressWarnings t = (CanSuppressWarnings) parentType;
		if (t.hasSuppressWarningsAnnotationFor(getRule())) {
		    isSuppressed = true;
		    break;
		}
	    }
	}
    }

    private void setVariableNameIfExists(Node node) {
	variableName = "";
	if (node instanceof ASTFieldDeclaration) {
	    variableName = ((ASTFieldDeclaration) node).getVariableName();
	} else if (node instanceof ASTLocalVariableDeclaration) {
	    variableName = ((ASTLocalVariableDeclaration) node).getVariableName();
	} else if (node instanceof ASTVariableDeclaratorId) {
	    variableName = node.getImage();
	}
    }
}

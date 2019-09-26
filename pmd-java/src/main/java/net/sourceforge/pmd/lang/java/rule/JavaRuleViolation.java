/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

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
            ASTCompilationUnit root = node.getRoot();

            packageName = root.getPackageName();

            className = getClassName(node);
            methodName = getMethodName(node);
            variableName = getVariableNameIfExists(node);

            if (!suppressed) {
                suppressed = AnnotationSuppressionUtil.contextSuppresses(node, getRule());
            }
        }
    }


    @Nullable
    private static String getClassName(JavaNode node) {
        ASTAnyTypeDeclaration enclosing = node.getEnclosingType();

        if (enclosing == null) {
            List<ASTAnyTypeDeclaration> tds = node.getRoot().getTypeDeclarations();

            enclosing = tds.stream()
                           .filter(AccessNode::isPublic)
                           .findFirst()
                           .orElseGet(
                               () -> tds.isEmpty() ? null : tds.get(0)
                           );
        }

        if (enclosing == null) {
            return null;
        } else {
            return String.join("$", enclosing.getQualifiedName().getClassList());
        }
    }

    @Nullable
    private static String getMethodName(JavaNode node) {
        // ancestorsOrSelf..........
        ASTMethodOrConstructorDeclaration enclosing =
            node instanceof ASTMethodOrConstructorDeclaration ? (ASTMethodOrConstructorDeclaration) node
                                                              : node.getFirstParentOfType(ASTMethodOrConstructorDeclaration.class);
        return enclosing == null ? null : enclosing.getName();
    }

    private static String getVariableNames(Iterable<ASTVariableDeclaratorId> iterable) {

        Iterator<ASTVariableDeclaratorId> it = iterable.iterator();
        StringBuilder builder = new StringBuilder();
        builder.append(it.next());

        while (it.hasNext()) {
            builder.append(", ").append(it.next());
        }
        return builder.toString();
    }

    private static String getVariableNameIfExists(Node node) {
        if (node instanceof ASTFieldDeclaration) {
            return getVariableNames((ASTFieldDeclaration) node);
        } else if (node instanceof ASTLocalVariableDeclaration) {
            return getVariableNames((ASTLocalVariableDeclaration) node);
        } else if (node instanceof ASTVariableDeclarator) {
            return ((ASTVariableDeclarator) node).getVariableId().getVariableName();
        } else if (node instanceof ASTVariableDeclaratorId) {
            return ((ASTVariableDeclaratorId) node).getVariableName();
        } else if (node instanceof ASTFormalParameter) {
            return ((ASTFormalParameter) node).getVariableDeclaratorId().getVariableName();
        } else {
            return "";
        }
    }
}

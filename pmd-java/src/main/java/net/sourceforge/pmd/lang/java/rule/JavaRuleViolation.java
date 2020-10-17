/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
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
 * @deprecated See {@link RuleViolation}
 */
@Deprecated
public class JavaRuleViolation extends ParametricRuleViolation<JavaNode> {

    public JavaRuleViolation(Rule rule, @NonNull JavaNode node, String filename, String message) {
        super(rule, filename, node, message);

        ASTCompilationUnit root = node.getRoot();

        packageName = root.getPackageName();

        className = getClassName(node);
        methodName = getMethodName(node);
        variableName = getVariableNameIfExists(node);
    }


    @Nullable
    private static String getClassName(JavaNode node) {
        ASTAnyTypeDeclaration enclosing = node instanceof ASTAnyTypeDeclaration ? (ASTAnyTypeDeclaration) node
                                                                                : node.getEnclosingType();

        ASTCompilationUnit file = node.getRoot();
        if (enclosing == null) {
            NodeStream<ASTAnyTypeDeclaration> tds = file.getTypeDeclarations();
            enclosing = tds.first(AccessNode::isPublic);
            if (enclosing == null) {
                enclosing = tds.first();
            }
        }

        if (enclosing == null) {
            return null;
        } else {
            String binaryName = enclosing.getBinaryName();
            String packageName = enclosing.getPackageName();
            return packageName.isEmpty()
                   ? binaryName
                   // plus 1 for the '.'
                   : binaryName.substring(packageName.length() + 1);
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
            return ((ASTVariableDeclarator) node).getVarId().getVariableName();
        } else if (node instanceof ASTVariableDeclaratorId) {
            return ((ASTVariableDeclaratorId) node).getVariableName();
        } else if (node instanceof ASTFormalParameter) {
            return ((ASTFormalParameter) node).getVarId().getVariableName();
        } else {
            return "";
        }
    }
}

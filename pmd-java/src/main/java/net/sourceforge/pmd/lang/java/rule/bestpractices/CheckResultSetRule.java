/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Rule that verifies, that the return values of next(), first(), last(), etc.
 * calls to a java.sql.ResultSet are actually verified.
 *
 */
public class CheckResultSetRule extends AbstractJavaRule {

    private Map<String, Node> resultSetVariables = new HashMap<>();

    private static Set<String> methods = new HashSet<>();

    static {
        methods.add(".next");
        methods.add(".previous");
        methods.add(".last");
        methods.add(".first");
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        resultSetVariables.clear();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        ASTClassOrInterfaceType type = null;
        if (!node.isTypeInferred()) {
            type = node.getFirstChildOfType(ASTType.class).getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        }
        if (type != null && (type.getType() != null && "java.sql.ResultSet".equals(type.getType().getName())
                || "ResultSet".equals(type.getImage()))) {
            ASTVariableDeclarator declarator = node.getFirstChildOfType(ASTVariableDeclarator.class);
            if (declarator != null) {
                ASTName name = declarator.getFirstDescendantOfType(ASTName.class);
                if (type.getType() != null || name != null && name.getImage().endsWith("executeQuery")) {
                    ASTVariableDeclaratorId id = declarator.getFirstChildOfType(ASTVariableDeclaratorId.class);
                    resultSetVariables.put(id.getImage(), node);
                }
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        String image = node.getImage();
        String var = getResultSetVariableName(image);
        if (var != null && resultSetVariables.containsKey(var)
                && node.getFirstParentOfType(ASTIfStatement.class) == null
                && node.getFirstParentOfType(ASTWhileStatement.class) == null
                && node.getFirstParentOfType(ASTReturnStatement.class) == null) {

            addViolation(data, resultSetVariables.get(var));
        }
        return super.visit(node, data);
    }

    private String getResultSetVariableName(String image) {
        if (image.contains(".")) {
            for (String method : methods) {
                if (image.endsWith(method)) {
                    return image.substring(0, image.lastIndexOf(method));
                }
            }
        }
        return null;
    }
}

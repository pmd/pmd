/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Finds hard coded encryption keys that are passed to
 * javax.crypto.spec.SecretKeySpec(key, algorithm).
 *
 * @author sergeygorbaty
 * @since 6.4.0
 */
public class HardCodedCryptoKeyRule extends AbstractJavaRule {

    private static final Class<?> SECRET_KEY_SPEC = javax.crypto.spec.SecretKeySpec.class;
    private final Set<VariableNameDeclaration> checkedVars = new HashSet<>();

    public HardCodedCryptoKeyRule() {
        addRuleChainVisit(ASTAllocationExpression.class);
    }

    @Override
    public void start(RuleContext ctx) {
        checkedVars.clear();
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (TypeTestUtil.isA(SECRET_KEY_SPEC, node.getFirstChildOfType(ASTClassOrInterfaceType.class))) {
            Node firstArgument = null;

            ASTArguments arguments = node.getFirstChildOfType(ASTArguments.class);
            if (arguments.size() > 0) {
                firstArgument = arguments.getFirstChildOfType(ASTArgumentList.class).getChild(0);
            }

            if (firstArgument != null) {
                ASTPrimaryPrefix prefix = firstArgument.getFirstDescendantOfType(ASTPrimaryPrefix.class);
                validateProperKeyArgument(data, prefix);
            }
        }
        return data;
    }

    /**
     * Recursively resolves the argument again, if the variable initializer
     * is itself a expression.
     *
     * Then checks the expression for being a string literal or array
     *
     * @param data
     * @param firstArgumentExpression
     */
    private void validateProperKeyArgument(Object data, ASTPrimaryPrefix firstArgumentExpression) {
        if (firstArgumentExpression == null) {
            return;
        }

        // named variable
        ASTName namedVar = firstArgumentExpression.getFirstDescendantOfType(ASTName.class);
        // find where it's declared, if possible
        if (namedVar != null && namedVar.getNameDeclaration() instanceof VariableNameDeclaration
                && !checkedVars.contains(namedVar.getNameDeclaration())) {
            VariableNameDeclaration varDecl = (VariableNameDeclaration) namedVar.getNameDeclaration();
            checkedVars.add(varDecl);

            ASTVariableInitializer initializer = varDecl.getAccessNodeParent().getFirstDescendantOfType(ASTVariableInitializer.class);
            if (initializer != null) {
                validateProperKeyArgument(data, initializer.getFirstDescendantOfType(ASTPrimaryPrefix.class));
            }
            
            List<NameOccurrence> usages = varDecl.getNode().getScope().getDeclarations().get(varDecl);
            for (NameOccurrence occurrence : usages) {
                ASTStatementExpression parentExpr = occurrence.getLocation().getFirstParentOfType(ASTStatementExpression.class);
                if (isAssignment(occurrence.getLocation(), parentExpr)) {
                    validateProperKeyArgument(data, parentExpr.getChild(2).getFirstDescendantOfType(ASTPrimaryPrefix.class));
                }
            }
        }

        // hard coded array
        ASTArrayInitializer arrayInit = firstArgumentExpression.getFirstDescendantOfType(ASTArrayInitializer.class);
        if (arrayInit != null) {
            addViolation(data, arrayInit);
        }

        // string literal
        ASTLiteral literal = firstArgumentExpression.getFirstDescendantOfType(ASTLiteral.class);
        if (literal != null && literal.isStringLiteral()) {
            addViolation(data, literal);
        }
    }

    private boolean isAssignment(Node node, ASTStatementExpression statement) {
        return statement != null && statement.getNumChildren() >= 3
                && node == statement.getChild(0).getFirstDescendantOfType(ASTName.class)
                && statement.getChild(1) instanceof ASTAssignmentOperator;
    }
}

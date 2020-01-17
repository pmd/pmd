/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;

/**
 * 1. Note all private constructors. 2. Note all instantiations from outside of
 * the class by way of the private constructor. 3. Flag instantiations.
 *
 * <p>
 * Parameter types can not be matched because they can come as exposed members
 * of classes. In this case we have no way to know what the type is. We can make
 * a best effort though which can filter some?
 * </p>
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 * @author David Konecny (david.konecny@)
 * @author Romain PELISSE, belaran@gmail.com, patch bug#1807370
 * @author Juan Martin Sotuyo Dodero (juansotuyo@gmail.com), complete rewrite
 */
public class AccessorClassGenerationRule extends AbstractJavaRule {

    private Map<String, List<ASTConstructorDeclaration>> privateConstructors = new HashMap<>();

    public AccessorClassGenerationRule() {
        super();
        /*
         * Order is important. Visit constructors first to find the private
         * ones, then visit allocations to find violations
         */
        addRuleChainVisit(ASTConstructorDeclaration.class);
        addRuleChainVisit(ASTAllocationExpression.class);
    }

    @Override
    public void end(final RuleContext ctx) {
        super.end(ctx);
        // Clean up all references to the AST
        privateConstructors.clear();
    }

    @Override
    public Object visit(final ASTConstructorDeclaration node, final Object data) {
        if (node.isPrivate()) {
            final String className = node.getParent().getParent().getParent().getImage();
            if (!privateConstructors.containsKey(className)) {
                privateConstructors.put(className, new ArrayList<ASTConstructorDeclaration>());
            }
            privateConstructors.get(className).add(node);
        }
        return data;
    }

    @Override
    public Object visit(final ASTAllocationExpression node, final Object data) {
        if (node.getChild(0) instanceof ASTClassOrInterfaceType) { // Ignore primitives
            final ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) node.getChild(0);
            final List<ASTConstructorDeclaration> constructors = privateConstructors.get(type.getImage());

            if (constructors != null) {
                final ASTArguments callArguments = node.getFirstChildOfType(ASTArguments.class);
                // Is this really a constructor call and not an array?
                if (callArguments != null) {
                    final ClassScope enclosingScope = node.getScope().getEnclosingScope(ClassScope.class);

                    for (final ASTConstructorDeclaration cd : constructors) {
                        // Are we within the same class scope?
                        if (cd.getScope().getEnclosingScope(ClassScope.class) == enclosingScope) {
                            break;
                        }

                        if (cd.getArity() == callArguments.getArgumentCount()) {
                            // TODO : Check types
                            addViolation(data, node);
                            break;
                        }
                    }
                }
            }
        }

        return data;
    }
}

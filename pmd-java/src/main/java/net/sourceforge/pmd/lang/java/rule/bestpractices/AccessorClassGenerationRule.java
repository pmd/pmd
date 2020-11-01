/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Modifier;
import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

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
public class AccessorClassGenerationRule extends AbstractJavaRulechainRule {

    public AccessorClassGenerationRule() {
        super(ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (node.isAnonymousClass()
            || Objects.equals(node.getTypeNode().getTypeMirror().getSymbol(),
                              node.getEnclosingType().getSymbol())) {
            return null;
        }

        // we're not in the same class

        if (Modifier.isPrivate(node.getMethodType().getModifiers())) {
            addViolation(data, node);
        }
        return null;
    }
}

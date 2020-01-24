/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds <code>catch</code> statements containing <code>throwable</code> as the
 * type definition.
 *
 * @author <a href="mailto:trondandersen@c2i.net">Trond Andersen</a>
 */
public class AvoidCatchingThrowableRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        ASTType type = node.getFirstDescendantOfType(ASTType.class);
        ASTClassOrInterfaceType name = type.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (name.hasImageEqualTo("Throwable")) {
            addViolation(data, name);
        }
        return super.visit(node, data);
    }
}

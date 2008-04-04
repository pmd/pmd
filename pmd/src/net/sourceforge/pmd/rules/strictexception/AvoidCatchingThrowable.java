package net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds <code>catch</code> statements
 * containing <code>throwable</code> as the type definition.
 * <p/>
 *
 * @author <a mailto:trondandersen@c2i.net>Trond Andersen</a>
 */
public class AvoidCatchingThrowable extends AbstractJavaRule {

    public Object visit(ASTCatchStatement node, Object data) {
        ASTType type = node.findChildrenOfType(ASTType.class).get(0);
        ASTClassOrInterfaceType name = type.findChildrenOfType(ASTClassOrInterfaceType.class).get(0);
        if (name.hasImageEqualTo("Throwable")) {
            addViolation(data, name);
        }
        return super.visit(node, data);
    }
}

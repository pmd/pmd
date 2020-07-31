/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds <code>catch</code> statements containing <code>throwable</code> as the
 * type definition.
 *
 * @author <a href="mailto:trondandersen@c2i.net">Trond Andersen</a>
 */
public class AvoidCatchingThrowableRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTCatchClause catchStatement, Object data) {
        for (@NonNull ASTClassOrInterfaceType caughtException : catchStatement.getParameter().getAllExceptionTypes()) {
            if (Throwable.class.equals(caughtException.getType())) {
                addViolation(data, catchStatement);
            }
        }
        return super.visit(catchStatement, data);
    }
}

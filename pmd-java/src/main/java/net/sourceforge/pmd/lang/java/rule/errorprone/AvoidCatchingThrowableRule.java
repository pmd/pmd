/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds <code>catch</code> statements containing <code>throwable</code> as the
 * type definition.
 *
 * @author <a href="mailto:trondandersen@c2i.net">Trond Andersen</a>
 */
public class AvoidCatchingThrowableRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTCatchStatement catchStatement, Object data) {
        for (Class<? extends Exception> caughtException : catchStatement.getCaughtExceptionTypes()) {
            if (Throwable.class.equals(caughtException)) {
                addViolation(data, catchStatement);
            }
        }
        return super.visit(catchStatement, data);
    }
}

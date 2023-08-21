/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;




/**
 * Detects redundant field initializers, i.e. the field initializer expressions
 * the JVM would assign by default.
 *
 * @author lucian.ciufudean@gmail.com
 * @since Apr 10, 2009
 */
public class RedundantFieldInitializerRule extends AbstractJavaRulechainRule {

    private static final Set<String> MAKE_FIELD_FINAL_CLASS_ANNOT =
        setOf(
                "lombok.Value"
        );

    public RedundantFieldInitializerRule() {
        super(ASTFieldDeclaration.class);
    }

    @Override
    public Object visit(ASTFieldDeclaration fieldDeclaration, Object data) {
        if (!fieldDeclaration.hasModifiers(JModifier.FINAL) && !JavaAstUtils.hasAnyAnnotation(fieldDeclaration.getEnclosingType(), MAKE_FIELD_FINAL_CLASS_ANNOT)) {
            for (ASTVariableDeclaratorId varId : fieldDeclaration.getVarIds()) {
                ASTExpression init = varId.getInitializer();
                if (init != null) {
                    if (!isWhitelisted(init) && JavaAstUtils.isDefaultValue(varId.getTypeMirror(), init)) {
                        addViolation(data, varId);
                    }
                }
            }
        }
        return data;
    }

    // whitelist if there are named variables in there
    private static boolean isWhitelisted(ASTExpression e) {
        return e.descendantsOrSelf().any(it -> it instanceof ASTVariableAccess || it instanceof ASTFieldAccess);
    }
}

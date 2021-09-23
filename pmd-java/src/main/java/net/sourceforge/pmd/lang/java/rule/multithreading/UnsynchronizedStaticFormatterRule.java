/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import java.text.Format;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Using a Formatter (e.g. SimpleDateFormatter, DecimalFormatter) which is static can cause
 * unexpected results when used in a multi-threaded environment. This rule will
 * find static Formatters which are used in an unsynchronized
 * manner.
 *
 * @author Allan Caplan
 * @see <a href="https://sourceforge.net/p/pmd/feature-requests/226/">feature #226 Check for SimpleDateFormat as singleton?</a>
 */
public class UnsynchronizedStaticFormatterRule extends AbstractJavaRulechainRule {
    private static final List<String> THREAD_SAFE_FORMATTER = Arrays.asList(
        "org.apache.commons.lang3.time.FastDateFormat"
    );

    private static final PropertyDescriptor<Boolean> ALLOW_METHOD_LEVEL_SYNC =
        PropertyFactory.booleanProperty("allowMethodLevelSynchronization")
            .desc("If true, method level synchronization is allowed as well as synchronized block. Otherwise"
                + " only synchronized blocks are allowed.")
            .defaultValue(false)
            .build();

    private Class<?> formatterClassToCheck = Format.class;

    public UnsynchronizedStaticFormatterRule() {
        super(ASTFieldDeclaration.class);
        definePropertyDescriptor(ALLOW_METHOD_LEVEL_SYNC);
    }

    UnsynchronizedStaticFormatterRule(Class<?> formatterClassToCheck) {
        this();
        this.formatterClassToCheck = formatterClassToCheck;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!node.hasModifiers(JModifier.STATIC)) {
            return data;
        }
        ASTClassOrInterfaceType cit = node.descendants(ASTClassOrInterfaceType.class).first();
        if (cit == null || !TypeTestUtil.isA(formatterClassToCheck, cit)) {
            return data;
        }

        ASTVariableDeclaratorId var = node.descendants(ASTVariableDeclaratorId.class).first();
        for (String formatter: THREAD_SAFE_FORMATTER) {
            if (TypeTestUtil.isA(formatter, var)) {
                return data;
            }
        }
        for (ASTNamedReferenceExpr ref : var.getLocalUsages()) {
            ASTMethodCall methodCall = null;
            if (ref.getParent() instanceof ASTMethodCall) {
                methodCall = (ASTMethodCall) ref.getParent();
            }
            // ignore usages, that don't call a method.
            if (methodCall == null) {
                continue;
            }

            Node n = ref;

            // is there a block-level synch?
            ASTSynchronizedStatement syncStatement = ref.ancestors(ASTSynchronizedStatement.class).first();
            if (syncStatement != null) {
                ASTExpression lockExpression = syncStatement.getLockExpression();
                if (JavaRuleUtil.isReferenceToSameVar(lockExpression, methodCall.getQualifier())) {
                    continue;
                }
            }

            // method level synch enabled and used?
            if (getProperty(ALLOW_METHOD_LEVEL_SYNC)) {
                ASTMethodDeclaration method = ref.ancestors(ASTMethodDeclaration.class).first();
                if (method != null && method.hasModifiers(JModifier.SYNCHRONIZED, JModifier.STATIC)) {
                    continue;
                }
            }

            addViolation(data, n);
        }
        return data;
    }
}

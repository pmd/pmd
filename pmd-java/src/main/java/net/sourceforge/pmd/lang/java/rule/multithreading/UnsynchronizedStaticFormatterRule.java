/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import java.text.Format;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
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
public class UnsynchronizedStaticFormatterRule extends AbstractJavaRule {
    private Class<?> formatterClassToCheck = Format.class;
    private static final List<String> THREAD_SAFE_FORMATTER = Arrays.asList(
        "org.apache.commons.lang3.time.FastDateFormat"
    );

    private static final PropertyDescriptor<Boolean> ALLOW_METHOD_LEVEL_SYNC =
        PropertyFactory.booleanProperty("allowMethodLevelSynchronization")
            .desc("If true, method level synchronization is allowed as well as synchronized block. Otherwise"
                + " only synchronized blocks are allowed.")
            .defaultValue(false)
            .build();

    public UnsynchronizedStaticFormatterRule() {
        addRuleChainVisit(ASTFieldDeclaration.class);
        definePropertyDescriptor(ALLOW_METHOD_LEVEL_SYNC);
    }

    UnsynchronizedStaticFormatterRule(Class<?> formatterClassToCheck) {
        this();
        this.formatterClassToCheck = formatterClassToCheck;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!node.isStatic()) {
            return data;
        }
        ASTClassOrInterfaceType cit = node.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (cit == null || !TypeHelper.isA(cit, formatterClassToCheck)) {
            return data;
        }

        ASTVariableDeclaratorId var = node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        for (String formatter: THREAD_SAFE_FORMATTER) {
            if (TypeHelper.isA(var, formatter)) {
                return data;
            }
        }
        for (NameOccurrence occ : var.getUsages()) {
            Node n = occ.getLocation();
            // ignore usages, that don't call a method.
            if (!n.getImage().contains(".")) {
                continue;
            }

            // is there a block-level synch?
            ASTSynchronizedStatement syncStatement = n.getFirstParentOfType(ASTSynchronizedStatement.class);
            if (syncStatement != null) {
                ASTExpression expression = syncStatement.getFirstChildOfType(ASTExpression.class);
                if (expression != null) {
                    ASTName name = expression.getFirstDescendantOfType(ASTName.class);
                    if (name != null && name.hasImageEqualTo(var.getVariableName())) {
                        continue;
                    }
                }
            }

            // method level synch enabled and used?
            if (getProperty(ALLOW_METHOD_LEVEL_SYNC)) {
                ASTMethodDeclaration method = n.getFirstParentOfType(ASTMethodDeclaration.class);
                if (method != null && method.isSynchronized() && method.isStatic()) {
                    continue;
                }
            }

            addViolation(data, n);
        }
        return data;
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import java.text.Format;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

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

    public UnsynchronizedStaticFormatterRule() {
        addRuleChainVisit(ASTFieldDeclaration.class);
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
            if (n.getFirstParentOfType(ASTSynchronizedStatement.class) != null) {
                continue;
            }
            // ignore usages, that don't call a method.
            if (!n.getImage().contains(".")) {
                continue;
            }

            ASTMethodDeclaration method = n.getFirstParentOfType(ASTMethodDeclaration.class);
            if (method != null && !method.isSynchronized()) {
                addViolation(data, n);
            }
        }
        return data;
    }
}

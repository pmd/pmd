/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaPropertyUtil;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.CollectionUtil;

public class UnusedPrivateFieldRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> IGNORED_ANNOTATIONS =
        JavaPropertyUtil.ignoredAnnotationsDescriptor(
            CollectionUtil.union(JavaRuleUtil.LOMBOK_ANNOTATIONS,
                                 setOf("java.lang.Deprecated",
                                       "javafx.fxml.FXML"))
        );

    public UnusedPrivateFieldRule() {
        super(ASTAnyTypeDeclaration.class);
        definePropertyDescriptor(IGNORED_ANNOTATIONS);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        if (node instanceof ASTAnyTypeDeclaration) {
            ASTAnyTypeDeclaration type = (ASTAnyTypeDeclaration) node;
            if (hasIgnoredAnnotation(type)) {
                return null;
            }

            for (ASTFieldDeclaration field : type.getDeclarations().filterIs(ASTFieldDeclaration.class)) {
                if (!isIgnored(field)) {
                    for (ASTVariableDeclaratorId varId : field.getVarIds()) {
                        if (JavaRuleUtil.isNeverUsed(varId)) {
                            addViolation(data, varId, varId.getName());
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean isIgnored(ASTFieldDeclaration field) {
        return field.getVisibility() != Visibility.V_PRIVATE
            || JavaRuleUtil.isSerialPersistentFields(field)
            || JavaRuleUtil.isSerialVersionUID(field)
            || hasIgnoredAnnotation(field);
    }

    private boolean hasIgnoredAnnotation(Annotatable node) {
        return JavaRuleUtil.hasAnyAnnotation(node, getProperty(IGNORED_ANNOTATIONS));
    }
}

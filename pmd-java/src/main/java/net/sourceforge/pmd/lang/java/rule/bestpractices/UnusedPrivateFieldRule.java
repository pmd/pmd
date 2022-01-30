/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaPropertyUtil;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class UnusedPrivateFieldRule extends AbstractJavaRulechainRule {

    private static final Set<String> INVALIDATING_CLASS_ANNOT = setOf(
        "lombok.Builder",
        "lombok.EqualsAndHashCode",
        "lombok.Getter",
        "lombok.Setter",
        "lombok.Data",
        "lombok.Value"
    );

    private static final PropertyDescriptor<List<String>> IGNORED_FIELD_ANNOTATIONS =
        JavaPropertyUtil.ignoredAnnotationsDescriptor(
            "lombok.Setter",
            "lombok.Getter",
            "java.lang.Deprecated",
            "lombok.experimental.Delegate",
            "javafx.fxml.FXML"
        );

    public UnusedPrivateFieldRule() {
        super(ASTAnyTypeDeclaration.class);
        definePropertyDescriptor(IGNORED_FIELD_ANNOTATIONS);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        if (node instanceof ASTAnyTypeDeclaration) {
            ASTAnyTypeDeclaration type = (ASTAnyTypeDeclaration) node;
            if (JavaRuleUtil.hasAnyAnnotation(type, INVALIDATING_CLASS_ANNOT)) {
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
            || JavaRuleUtil.hasAnyAnnotation(field, getProperty(IGNORED_FIELD_ANNOTATIONS));
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.Collection;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractLombokAwareRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UnusedPrivateFieldRule extends AbstractLombokAwareRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTAnyTypeDeclaration.class);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        Collection<String> defaultValues = new ArrayList<>(super.defaultSuppressionAnnotations());
        defaultValues.add("java.lang.Deprecated");
        defaultValues.add("javafx.fxml.FXML");
        defaultValues.add("lombok.experimental.Delegate");
        defaultValues.add("lombok.EqualsAndHashCode");
        return defaultValues;
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        if (node instanceof ASTAnyTypeDeclaration) {
            ASTAnyTypeDeclaration type = (ASTAnyTypeDeclaration) node;
            if (hasIgnoredAnnotation(type) || hasLombokAnnotation(type)) {
                return null;
            }

            for (ASTFieldDeclaration field : type.getDeclarations()
                                                 .filterIs(ASTFieldDeclaration.class)) {
                if (field.getVisibility() == Visibility.V_PRIVATE
                    && !hasIgnoredAnnotation(field)) {
                    for (ASTVariableDeclaratorId varId : field.getVarIds()) {
                        if (!isOK(varId) && UnusedLocalVariableRule.isNeverUsed(varId)) {
                            addViolation(data, varId, varId.getName());
                        }
                    }
                }
            }
        }
        return null;
    }


    private boolean isOK(ASTVariableDeclaratorId node) {
        return "serialVersionUID".equals(node.getName())
            || "serialPersistentFields".equals(node.getName())
            || "IDENT".equals(node.getName());
    }
}

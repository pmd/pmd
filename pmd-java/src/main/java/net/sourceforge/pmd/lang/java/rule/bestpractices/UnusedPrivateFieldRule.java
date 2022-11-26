/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class UnusedPrivateFieldRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> IGNORED_FIELD_NAMES =
            PropertyFactory.stringListProperty("ignoredFieldNames")
                    .defaultValues("serialVersionUID", "serialPersistentFields")
                    .desc("Field Names that are ignored from the unused check")
                    .build();

    public UnusedPrivateFieldRule() {
        super(ASTAnyTypeDeclaration.class);
        definePropertyDescriptor(IGNORED_FIELD_NAMES);
    }

    @Override
    public Object visitJavaNode(JavaNode node, Object data) {
        if (node instanceof ASTAnyTypeDeclaration) {
            ASTAnyTypeDeclaration type = (ASTAnyTypeDeclaration) node;
            if (hasAnyAnnotation(type)) {
                return null;
            }

            for (ASTFieldDeclaration field : type.getDeclarations().filterIs(ASTFieldDeclaration.class)) {
                if (!isIgnored(field)) {
                    for (ASTVariableDeclaratorId varId : field.getVarIds()) {
                        if (JavaAstUtils.isNeverUsed(varId)) {
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
            || isOK(field)
            || hasAnyAnnotation(field);
    }

    private boolean isOK(ASTFieldDeclaration field) {
        return field.getVarIds().any(it -> getProperty(IGNORED_FIELD_NAMES).contains(it.getName()));
    }

    private static boolean hasAnyAnnotation(Annotatable node) {
        return !node.getDeclaredAnnotations().isEmpty();
    }
}

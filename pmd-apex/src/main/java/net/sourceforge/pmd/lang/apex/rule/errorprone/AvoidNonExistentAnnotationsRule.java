/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.errorprone;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import apex.jorje.semantic.ast.compilation.UserClass;
import apex.jorje.semantic.ast.compilation.UserEnum;
import apex.jorje.semantic.ast.compilation.UserInterface;
import apex.jorje.semantic.ast.member.Field;
import apex.jorje.semantic.ast.member.Method;
import apex.jorje.semantic.ast.member.Property;
import apex.jorje.semantic.ast.modifier.Annotation;
import apex.jorje.semantic.ast.modifier.ModifierNode;
import apex.jorje.semantic.symbol.type.AnnotationTypeInfos;
import apex.jorje.semantic.symbol.type.StandardAnnotationTypeInfo;

/**
 * Apex supported non existent annotations for legacy reasons.
 * In the future, use of such non-existent annotations could result in broken apex code that will not compile.
 * This will prevent users of garbage annotations from being able to use legitimate annotations added to apex in the future.
 * A full list of supported annotations can be found at https://developer.salesforce.com/docs/atlas.en-us.apexcode.meta/apexcode/apex_classes_annotation.htm
 *
 * @author a.subramanian
 */
public class AvoidNonExistentAnnotationsRule extends AbstractApexRule {

    private static final Set<StandardAnnotationTypeInfo> SUPPORTED_APEX_ANNOTATIONS = getSupportedApexAnnotations();

    @Override
    public Object visit(final ASTUserClass node, final Object data) {
        final UserClass userClass = node.getNode();
        checkForNonExistentAnnotation(node, userClass.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public final Object visit(ASTUserInterface node, final Object data) {
        final UserInterface userInterface = node.getNode();
        checkForNonExistentAnnotation(node, userInterface.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTUserEnum node, final Object data) {
        final UserEnum userEnum = node.getNode();
        checkForNonExistentAnnotation(node, userEnum.getModifiers(), data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(final ASTMethod node, final Object data) {
        final Method method = node.getNode();
        return checkForNonExistentAnnotation(node, method.getModifiersNode(), data);
    }

    @Override
    public Object visit(final ASTProperty node, final Object data) {
        final Property property = node.getNode();
        // may have nested methods, don't visit children
        return checkForNonExistentAnnotation(node, property.getModifiersNode(), data);
    }

    @Override
    public Object visit(final ASTField node, final Object data) {
        final Field field = node.getNode();
        return checkForNonExistentAnnotation(node, field.getModifiers(), data);
    }

    private Object checkForNonExistentAnnotation(final AbstractApexNode<?> node, final ModifierNode modifierNode, final Object data) {
        for (final Annotation annotation : modifierNode.getModifiers().getAnnotations()) {
            if (!SUPPORTED_APEX_ANNOTATIONS.contains(annotation.getType())) {
                addViolationWithMessage(data, node, "Use of non existent annotations will lead to broken Apex code which will not compile in the future.");
            }
        }
        return data;
    }

    private static Set<StandardAnnotationTypeInfo> getSupportedApexAnnotations() {
        final java.lang.reflect.Field[] fields = AnnotationTypeInfos.class.getFields();
        final Set<StandardAnnotationTypeInfo> annotationTypeInfos = new HashSet<>();
        for (final java.lang.reflect.Field field : fields) {
            if (field.getType().isAssignableFrom(StandardAnnotationTypeInfo.class)) {
                field.setAccessible(true);
                try {
                    annotationTypeInfos.add((StandardAnnotationTypeInfo) field.get(null));
                } catch (final Exception illegalAccessException) {
                    continue;
                }
            }
        }
        return annotationTypeInfos;
    }
}

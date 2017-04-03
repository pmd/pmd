package net.sourceforge.pmd.lang.apex.rule.style;

import java.util.*;

import net.sourceforge.pmd.lang.apex.ast.*;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import apex.jorje.semantic.ast.compilation.*;
import apex.jorje.semantic.ast.member.*;
import apex.jorje.semantic.ast.modifier.Annotation;
import apex.jorje.semantic.ast.modifier.ModifierNode;
import apex.jorje.semantic.symbol.type.*;

/**
 * Apex supported non existent annotations for legacy reasons.
 * In the future, use of such non-existent annotations could result in broken apex code that will not copile.
 * This will prevent users of garbage annotations from being able to use legitimate annotations added to apex in the future.
 *
 * @author a.subramanian
 */
public class AvoidNonExistentAnnotationsRule extends AbstractApexRule {

    private static final Set<StandardAnnotationTypeInfo> supportedApexAnnotations = getSupportedApexAnnotations();

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
        return checkForNonExistentAnnotation(node, property.getModifiersNode(), data);
    }

    @Override
    public Object visit(final ASTField node, final Object data) {
        final Field field = node.getNode();
        return checkForNonExistentAnnotation(node, field.getModifiers(), data);
    }

    private Object checkForNonExistentAnnotation(final AbstractApexNode node, final ModifierNode modifierNode, final Object data) {
        for (final Annotation annotation : modifierNode.getModifiers().getAnnotations()) {
            if (!supportedApexAnnotations.contains(annotation.getType())) {
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

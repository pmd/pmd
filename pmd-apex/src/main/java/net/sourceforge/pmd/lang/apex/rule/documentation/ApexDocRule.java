/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.documentation;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTFormalComment;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class ApexDocRule extends AbstractApexRule {

    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("@description\\s");
    private static final Pattern RETURN_PATTERN = Pattern.compile("@return\\s");
    private static final Pattern PARAM_PATTERN = Pattern.compile("@param\\s+(\\w+)\\s");

    private static final String MISSING_COMMENT_MESSAGE = "Missing ApexDoc comment";
    private static final String MISSING_DESCRIPTION_MESSAGE = "Missing ApexDoc @description";
    private static final String MISSING_RETURN_MESSAGE = "Missing ApexDoc @return";
    private static final String UNEXPECTED_RETURN_MESSAGE = "Unexpected ApexDoc @return";
    private static final String MISMATCHED_PARAM_MESSAGE = "Missing or mismatched ApexDoc @param";

    private static final PropertyDescriptor<Boolean> REPORT_PRIVATE_DESCRIPTOR =
            booleanProperty("reportPrivate")
                    .desc("Report private classes, methods and properties").defaultValue(false).build();

    private static final PropertyDescriptor<Boolean> REPORT_PROTECTED_DESCRIPTOR =
            booleanProperty("reportProtected")
                    .desc("Report protected classes, methods and properties").defaultValue(false).build();

    private static final PropertyDescriptor<Boolean> REPORT_MISSING_DESCRIPTION_DESCRIPTOR =
            booleanProperty("reportMissingDescription")
                .desc("Report missing @description").defaultValue(true).build();

    private static final PropertyDescriptor<Boolean> REPORT_PROPERTY_DESCRIPTOR =
            booleanProperty("reportProperty")
                .desc("Report properties without comments").defaultValue(true).build();

    public ApexDocRule() {
        definePropertyDescriptor(REPORT_PRIVATE_DESCRIPTOR);
        definePropertyDescriptor(REPORT_PROTECTED_DESCRIPTOR);
        definePropertyDescriptor(REPORT_MISSING_DESCRIPTION_DESCRIPTOR);
        definePropertyDescriptor(REPORT_PROPERTY_DESCRIPTOR);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTUserClass.class, ASTUserInterface.class, ASTMethod.class, ASTProperty.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        handleClassOrInterface(node, data);
        return data;
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        handleClassOrInterface(node, data);
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (node.getParent() instanceof ASTProperty) {
            // Skip property methods, doc is required on the property itself
            return data;
        }

        ApexDocComment comment = getApexDocComment(node);
        if (comment == null) {
            if (shouldHaveApexDocs(node)) {
                asCtx(data).addViolationWithMessage(node, MISSING_COMMENT_MESSAGE);
            }
        } else {
            if (getProperty(REPORT_MISSING_DESCRIPTION_DESCRIPTOR) && !comment.hasDescription) {
                asCtx(data).addViolationWithMessage(node, MISSING_DESCRIPTION_MESSAGE);
            }

            String returnType = node.getReturnType();
            boolean shouldHaveReturn = !(returnType.isEmpty() || "void".equalsIgnoreCase(returnType));
            if (comment.hasReturn != shouldHaveReturn) {
                if (shouldHaveReturn) {
                    asCtx(data).addViolationWithMessage(node, MISSING_RETURN_MESSAGE);
                } else {
                    asCtx(data).addViolationWithMessage(node, UNEXPECTED_RETURN_MESSAGE);
                }
            }

            // Collect parameter names in order
            final List<String> params = node.children(ASTParameter.class).toStream()
                    .map(ASTParameter::getImage).collect(Collectors.toList());

            if (!comment.params.equals(params)) {
                asCtx(data).addViolationWithMessage(node, MISMATCHED_PARAM_MESSAGE);
            }
        }

        return data;
    }

    @Override
    public Object visit(ASTProperty node, Object data) {
        ApexDocComment comment = getApexDocComment(node);

        if (comment == null) {
            if (shouldHaveApexDocs(node)) {
                asCtx(data).addViolationWithMessage(node, MISSING_COMMENT_MESSAGE);
            }
        } else {
            if (getProperty(REPORT_MISSING_DESCRIPTION_DESCRIPTOR) && !comment.hasDescription) {
                asCtx(data).addViolationWithMessage(node, MISSING_DESCRIPTION_MESSAGE);
            }
        }

        return data;
    }

    private void handleClassOrInterface(ApexNode<?> node, Object data) {
        ApexDocComment comment = getApexDocComment(node);
        if (comment == null) {
            if (shouldHaveApexDocs(node)) {
                asCtx(data).addViolationWithMessage(node, MISSING_COMMENT_MESSAGE);
            }
        } else {
            if (getProperty(REPORT_MISSING_DESCRIPTION_DESCRIPTOR) && !comment.hasDescription) {
                asCtx(data).addViolationWithMessage(node, MISSING_DESCRIPTION_MESSAGE);
            }
        }
    }

    private boolean shouldHaveApexDocs(ApexNode<?> node) {
        if (!node.hasRealLoc()) {
            return false;
        }

        // is this a test?
        for (final ASTAnnotation annotation : node.descendants(ASTAnnotation.class)) {
            if ("IsTest".equalsIgnoreCase(annotation.getName())) {
                return false;
            }
        }

        // is it a property?
        if (node instanceof ASTProperty && !getProperty(REPORT_PROPERTY_DESCRIPTOR)) {
            return false;
        }

        ASTModifierNode modifier = node.firstChild(ASTModifierNode.class);
        if (modifier != null) {
            boolean flagPrivate = getProperty(REPORT_PRIVATE_DESCRIPTOR) && modifier.isPrivate();
            boolean flagProtected = getProperty(REPORT_PROTECTED_DESCRIPTOR) && modifier.isProtected();
            return (modifier.isPublic() || modifier.isGlobal() || flagPrivate || flagProtected) && !modifier.isOverride();
        }

        return false;
    }

    private ApexDocComment getApexDocComment(ApexNode<?> node) {
        ASTFormalComment comment = node.firstChild(ASTFormalComment.class);
        if (comment != null) {
            String token = comment.getImage();

            boolean hasDescription = DESCRIPTION_PATTERN.matcher(token).find();
            boolean hasReturn = RETURN_PATTERN.matcher(token).find();

            List<String> params = new ArrayList<>();
            Matcher paramMatcher = PARAM_PATTERN.matcher(token);
            while (paramMatcher.find()) {
                params.add(paramMatcher.group(1));
            }

            return new ApexDocComment(hasDescription, hasReturn, params);
        }
        return null;
    }

    private static class ApexDocComment {
        boolean hasDescription;
        boolean hasReturn;
        List<String> params;

        ApexDocComment(boolean hasDescription, boolean hasReturn, List<String> params) {
            this.hasDescription = hasDescription;
            this.hasReturn = hasReturn;
            this.params = params;
        }
    }
}

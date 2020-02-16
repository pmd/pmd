/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.documentation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTFormalComment;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class ApexDocRule extends AbstractApexRule {
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("@description\\s");
    private static final Pattern RETURN_PATTERN = Pattern.compile("@return\\s");
    private static final Pattern PARAM_PATTERN = Pattern.compile("@param\\s+(\\w+)\\s");

    private static final String MISSING_COMMENT_MESSAGE = "Missing ApexDoc comment";
    private static final String MISSING_DESCRIPTION_MESSAGE = "Missing ApexDoc @description";
    private static final String MISSING_RETURN_MESSAGE = "Missing ApexDoc @return";
    private static final String UNEXPECTED_RETURN_MESSAGE = "Unexpected ApexDoc @return";
    private static final String MISMATCHED_PARAM_MESSAGE = "Missing or mismatched ApexDoc @param";

    public ApexDocRule() {
        addRuleChainVisit(ASTUserClass.class);
        addRuleChainVisit(ASTUserInterface.class);
        addRuleChainVisit(ASTMethod.class);
        addRuleChainVisit(ASTProperty.class);
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
                addViolationWithMessage(data, node, MISSING_COMMENT_MESSAGE);
            }
        } else {
            if (!comment.hasDescription) {
                addViolationWithMessage(data, node, MISSING_DESCRIPTION_MESSAGE);
            }

            String returnType = node.getReturnType();
            boolean shouldHaveReturn = !(returnType.isEmpty() || "void".equalsIgnoreCase(returnType));
            if (comment.hasReturn != shouldHaveReturn) {
                if (shouldHaveReturn) {
                    addViolationWithMessage(data, node, MISSING_RETURN_MESSAGE);
                } else {
                    addViolationWithMessage(data, node, UNEXPECTED_RETURN_MESSAGE);
                }
            }

            // Collect parameter names in order
            final List<String> params = node.findChildrenOfType(ASTParameter.class)
                    .stream().map(p -> p.getImage()).collect(Collectors.toList());

            if (!comment.params.equals(params)) {
                addViolationWithMessage(data, node, MISMATCHED_PARAM_MESSAGE);
            }
        }

        return data;
    }

    @Override
    public Object visit(ASTProperty node, Object data) {
        ApexDocComment comment = getApexDocComment(node);
        if (comment == null) {
            if (shouldHaveApexDocs(node)) {
                addViolationWithMessage(data, node, MISSING_COMMENT_MESSAGE);
            }
        } else {
            if (!comment.hasDescription) {
                addViolationWithMessage(data, node, MISSING_DESCRIPTION_MESSAGE);
            }
        }

        return data;
    }

    private void handleClassOrInterface(AbstractApexNode<?> node, Object data) {
        ApexDocComment comment = getApexDocComment(node);
        if (comment == null) {
            if (shouldHaveApexDocs(node)) {
                addViolationWithMessage(data, node, MISSING_COMMENT_MESSAGE);
            }
        } else {
            if (!comment.hasDescription) {
                addViolationWithMessage(data, node, MISSING_DESCRIPTION_MESSAGE);
            }
        }
    }

    private boolean shouldHaveApexDocs(AbstractApexNode<?> node) {
        if (!node.hasRealLoc()) {
            return false;
        }

        // is this a test?
        for (final ASTAnnotation annotation : node.findDescendantsOfType(ASTAnnotation.class)) {
            if (annotation.getImage().equals("IsTest")) {
                return false;
            }
        }

        ASTModifierNode modifier = node.getFirstChildOfType(ASTModifierNode.class);
        if (modifier != null) {
            return (modifier.isPublic() || modifier.isGlobal()) && !modifier.isOverride();
        }
        return false;
    }

    private ApexDocComment getApexDocComment(ApexNode<?> node) {
        ASTFormalComment comment = node.getFirstChildOfType(ASTFormalComment.class);
        if (comment != null) {
            String token = comment.getToken();

            boolean hasDescription = DESCRIPTION_PATTERN.matcher(token).find();
            boolean hasReturn = RETURN_PATTERN.matcher(token).find();

            ArrayList<String> params = new ArrayList<>();
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

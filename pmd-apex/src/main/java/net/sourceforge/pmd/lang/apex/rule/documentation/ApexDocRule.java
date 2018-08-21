/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.documentation;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.GLOBAL;
import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.OVERRIDE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexRootNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

import apex.jorje.data.Locations;
import apex.jorje.parser.impl.ApexLexer;
import apex.jorje.semantic.ast.member.Parameter;
import apex.jorje.semantic.ast.modifier.ModifierGroup;

public class ApexDocRule extends AbstractApexRule {
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile("@description\\s");
    private static final Pattern RETURN_PATTERN = Pattern.compile("@return\\s");
    private static final Pattern PARAM_PATTERN = Pattern.compile("@param\\s+(\\w+)\\s");

    private static final String MISSING_COMMENT_MESSAGE = "Missing ApexDoc comment";
    private static final String MISSING_DESCRIPTION_MESSAGE = "Missing ApexDoc @description";
    private static final String MISSING_RETURN_MESSAGE = "Missing ApexDoc @return";
    private static final String UNEXPECTED_RETURN_MESSAGE = "Unexpected ApexDoc @return";
    private static final String MISMATCHED_PARAM_MESSAGE = "Missing or mismatched ApexDoc @param";

    private boolean inClass;
    private boolean inTestClass;
    private String source;
    private List<TokenLocation> tokenLocations;

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (inClass) {
            super.visit(node, data);
        } else {
            inClass = true;
            inTestClass = false;
            buildTokens(node);
            super.visit(node, data);
            inClass = false;
        }

        handleClassOrInterface(node, data);

        return data;
    }


    @Override
    public Object visit(ASTUserInterface node, Object data) {
        if (inClass) {
            super.visit(node, data);
        } else {
            buildTokens(node);
            super.visit(node, data);
        }

        handleClassOrInterface(node, data);

        return data;
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        if (node.getImage().equals("IsTest")) {
            inTestClass = true;
        }
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        ApexDocComment comment = getApexDocComment(node);
        if (comment == null) {
            if (shouldHaveApexDocs(node)) {
                addViolationWithMessage(data, node, MISSING_COMMENT_MESSAGE);
            }
        } else {
            if (!comment.hasDescription) {
                addViolationWithMessage(data, node, MISSING_DESCRIPTION_MESSAGE);
            }

            String returnType = node.getNode().getReturnTypeRef().toString();
            boolean shouldHaveReturn = !(returnType.isEmpty() || "void".equalsIgnoreCase(returnType));
            if (comment.hasReturn != shouldHaveReturn) {
                if (shouldHaveReturn) {
                    addViolationWithMessage(data, node, MISSING_RETURN_MESSAGE);
                } else {
                    addViolationWithMessage(data, node, UNEXPECTED_RETURN_MESSAGE);
                }
            }

            ArrayList<String> params = new ArrayList<>();
            for (Parameter x : node.getNode().getMethodInfo().getParameters()) {
                String value = x.getName().getValue();
                params.add(value);
            }

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

    private void buildTokens(ApexRootNode<?> node) {
        source = node.getSource();
        ANTLRStringStream stream = new ANTLRStringStream(source);
        ApexLexer lexer = new ApexLexer(stream);

        tokenLocations = new ArrayList<>();
        Integer startIndex = 0;
        Token token = lexer.nextToken();
        Integer endIndex = lexer.getCharIndex();
        while (token.getType() != Token.EOF) {
            if (token.getType() != ApexLexer.WS) {
                tokenLocations.add(new TokenLocation(startIndex, token.getText()));
            }
            startIndex = endIndex;
            token = lexer.nextToken();
            endIndex = lexer.getCharIndex();
        }
    }

    private void handleClassOrInterface(ApexNode<?> node, Object data) {
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

    private boolean shouldHaveApexDocs(ApexNode<?> node) {
        if (inTestClass || node.getNode().getLoc() == Locations.NONE) {
            return false;
        }
        ASTModifierNode modifier = node.getFirstChildOfType(ASTModifierNode.class);
        if (modifier != null) {
            boolean isPublic = modifier.isPublic();
            ModifierGroup modifierGroup = modifier.getNode().getModifiers();
            boolean isGlobal = modifierGroup.has(GLOBAL);
            boolean isOverride = modifierGroup.has(OVERRIDE);
            return (isPublic || isGlobal) && !isOverride;
        }
        return false;
    }

    private ApexDocComment getApexDocComment(ApexNode<?> node) {
        String token = getApexDocToken(getApexDocIndex(node));
        if (token == null) {
            return null;
        }

        boolean hasDescription = DESCRIPTION_PATTERN.matcher(token).find();
        boolean hasReturn = RETURN_PATTERN.matcher(token).find();

        ArrayList<String> params = new ArrayList<>();
        Matcher paramMatcher = PARAM_PATTERN.matcher(token);
        while (paramMatcher.find()) {
            params.add(paramMatcher.group(1));
        }

        return new ApexDocComment(hasDescription, hasReturn, params);
    }

    private int getApexDocIndex(ApexNode<?> node) {
        ASTAnnotation annotation = node.getFirstDescendantOfType(ASTAnnotation.class);
        ApexNode<?> firstNode = annotation == null ? node : annotation;
        int index = firstNode.getNode().getLoc().getStartIndex();
        return source.lastIndexOf('\n', index);
    }

    private String getApexDocToken(int index) {
        TokenLocation last = null;
        for (TokenLocation location : tokenLocations) {
            if (location.index >= index) {
                if (last != null && last.token.startsWith("/**")) {
                    return last.token;
                }
                return null;
            }
            last = location;
        }
        return null;
    }

    private class TokenLocation {
        int index;
        String token;

        TokenLocation(int index, String token) {
            this.index = index;
            this.token = token;
        }
    }

    private class ApexDocComment {
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

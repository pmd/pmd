/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class InvalidLogMessageFormatRule extends AbstractJavaRule {
    private static final Map<String, Set<String>> LOGGERS;

    static {
        Map<String, Set<String>> loggersMap = new HashMap<>();

        loggersMap.put("org.slf4j.Logger", Collections
                .unmodifiableSet(new HashSet<String>(Arrays.asList("trace", "debug", "info", "warn", "error"))));
        loggersMap.put("org.apache.logging.log4j.Logger", Collections
                .unmodifiableSet(new HashSet<String>(Arrays.asList("trace", "debug", "info", "warn", "error", "fatal", "all"))));

        LOGGERS = loggersMap;
    }

    public InvalidLogMessageFormatRule() {
        addRuleChainVisit(ASTName.class);
    }

    @Override
    public Object visit(final ASTName node, final Object data) {
        final NameDeclaration nameDeclaration = node.getNameDeclaration();
        // ignore imports or methods
        if (!(nameDeclaration instanceof VariableNameDeclaration)) {
            return data;
        }
        final String loggingClass;
        // ignore unsupported logger
        Class<?> type = ((VariableNameDeclaration) nameDeclaration).getType();
        if (type == null || !LOGGERS.containsKey(type.getName())) {
            return data;
        } else {
            loggingClass = type.getName();
        }

        // get the node that contains the logger
        final ASTPrimaryExpression parentNode = node.getFirstParentOfType(ASTPrimaryExpression.class);

        // get the log level
        final String method = parentNode.getFirstChildOfType(ASTPrimaryPrefix.class).getFirstChildOfType(ASTName.class)
                .getImage().replace(nameDeclaration.getImage() + ".", "");

        // ignore if not a log level
        if (!LOGGERS.get(loggingClass).contains(method)) {
            return data;
        }

        // find the arguments
        final List<ASTExpression> argumentList = parentNode.getFirstChildOfType(ASTPrimarySuffix.class)
                .getFirstDescendantOfType(ASTArgumentList.class).findChildrenOfType(ASTExpression.class);

        // ignore the first argument if it is a known non-string value, e.g. a slf4j-Marker
        if (argumentList.get(0).getType() != null && !argumentList.get(0).getType().equals(String.class)) {
            argumentList.remove(0);
        }

        // remove the message parameter
        final ASTExpression messageParam = argumentList.remove(0);
        final int expectedArguments = expectedArguments(messageParam);

        if (expectedArguments == -1) {
            // ignore if we couldn't analyze the message parameter
            return data;
        }

        // Remove throwable param, since it is shown separately.
        // But only, if it is not used as a placeholder argument
        if (argumentList.size() > expectedArguments) {
            removeThrowableParam(argumentList);
        }

        if (argumentList.size() < expectedArguments) {
            addViolationWithMessage(data, node,
                    "Missing arguments," + getExpectedMessage(argumentList, expectedArguments));
        } else if (argumentList.size() > expectedArguments) {
            addViolationWithMessage(data, node,
                    "Too many arguments," + getExpectedMessage(argumentList, expectedArguments));
        }

        return data;
    }

    private boolean isNewThrowable(ASTPrimaryExpression last) {
        // in case a new exception is created or the exception class is
        // mentioned.
        ASTClassOrInterfaceType classOrInterface = last.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        return classOrInterface != null && classOrInterface.getType() != null
                && TypeHelper.isA(classOrInterface, Throwable.class);
    }

    private boolean hasTypeThrowable(ASTPrimaryExpression last) {
        // if the type could be determined already
        return last.getType() != null && TypeHelper.isA(last, Throwable.class);
    }

    private boolean isReferencingThrowable(ASTPrimaryExpression last) {
        // check the variable type, if there is a reference by name
        ASTName variable = last.getFirstDescendantOfType(ASTName.class);
        if (variable != null && variable.getNameDeclaration() != null
                && variable.getNameDeclaration() instanceof VariableNameDeclaration) {
            VariableNameDeclaration declaration = (VariableNameDeclaration) variable.getNameDeclaration();
            if (declaration.getType() != null && Throwable.class.isAssignableFrom(declaration.getType())) {
                return true;
            }
            // convention: Exception type names should end with Exception
            if (declaration.getTypeImage() != null && declaration.getTypeImage().endsWith("Exception")) {
                return true;
            }
        }
        return false;
    }

    private void removeThrowableParam(final List<ASTExpression> params) {
        // Throwable parameters are the last one in the list, if any.
        if (params.isEmpty()) {
            return;
        }
        int lastIndex = params.size() - 1;
        ASTPrimaryExpression last = params.get(lastIndex).getFirstDescendantOfType(ASTPrimaryExpression.class);

        if (isNewThrowable(last) || hasTypeThrowable(last) || isReferencingThrowable(last)) {
            params.remove(lastIndex);
        }
    }

    private String getExpectedMessage(final List<ASTExpression> params, final int expectedArguments) {
        return " expected " + expectedArguments + (expectedArguments > 1 ? " arguments " : " argument ") + "but have "
                + params.size();
    }

    private int expectedArguments(final ASTExpression node) {
        int count = -1;
        // look if the logger has a literal message
        if (node.getFirstDescendantOfType(ASTLiteral.class) != null) {
            count = countPlaceholders(node);
        } else if (node.getFirstDescendantOfType(ASTName.class) != null) {
            final String variableName = node.getFirstDescendantOfType(ASTName.class).getImage();
            // look if the message is defined locally in a method/constructor, initializer block or lambda expression
            final JavaNode parentBlock = node.getFirstParentOfAnyType(ASTMethodOrConstructorDeclaration.class, ASTInitializer.class, ASTLambdaExpression.class);
            if (parentBlock != null) {
                final List<ASTVariableDeclarator> localVariables = parentBlock.findDescendantsOfType(ASTVariableDeclarator.class);
                count = getAmountOfExpectedArguments(variableName, localVariables);
            }

            if (count == -1) {
                // look if the message is defined in a field
                final List<ASTFieldDeclaration> fieldlist = node.getFirstParentOfAnyType(ASTClassOrInterfaceBody.class, ASTEnumBody.class)
                        .findDescendantsOfType(ASTFieldDeclaration.class);
                // only look for ASTVariableDeclarator that are Fields
                final List<ASTVariableDeclarator> fields = new ArrayList<>(fieldlist.size());
                for (final ASTFieldDeclaration astFieldDeclaration : fieldlist) {
                    fields.add(astFieldDeclaration.getFirstChildOfType(ASTVariableDeclarator.class));
                }
                count = getAmountOfExpectedArguments(variableName, fields);
            }
        }
        return count;
    }

    private int getAmountOfExpectedArguments(final String variableName, final List<ASTVariableDeclarator> variables) {
        for (final ASTVariableDeclarator astVariableDeclarator : variables) {
            if (astVariableDeclarator.getFirstChildOfType(ASTVariableDeclaratorId.class).getImage()
                    .equals(variableName)) {
                ASTVariableInitializer variableInitializer = astVariableDeclarator
                        .getFirstDescendantOfType(ASTVariableInitializer.class);
                ASTExpression expression = null;
                if (variableInitializer != null) {
                    expression = variableInitializer.getFirstChildOfType(ASTExpression.class);
                }
                if (expression != null) {
                    return countPlaceholders(expression);
                }
            }
        }
        return -1;
    }

    private int countPlaceholders(final ASTExpression node) {
        List<ASTLiteral> literals = getStringLiterals(node);
        if (literals.isEmpty()) {
            // -1 we could not analyze the message parameter
            return -1;
        }

        // if there are multiple literals, we just assume, they are concatenated
        // together...
        int result = 0;
        for (ASTLiteral stringLiteral : literals) {
            result += StringUtils.countMatches(stringLiteral.getImage(), "{}");
        }
        return result;
    }

    private List<ASTLiteral> getStringLiterals(final Node node) {
        List<ASTLiteral> stringLiterals = new ArrayList<>();
        for (ASTLiteral literal : node.findDescendantsOfType(ASTLiteral.class)) {
            if (literal.isStringLiteral()) {
                stringLiterals.add(literal);
            }
        }
        return stringLiterals;
    }
}

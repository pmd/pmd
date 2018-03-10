/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class InvalidSlf4jMessageFormatRule extends AbstractJavaRule {

    private static final Set<String> LOGGER_LEVELS;
    private static final String LOGGER_CLASS = "org.slf4j.Logger";

    static {
        LOGGER_LEVELS = Collections
                .unmodifiableSet(new HashSet<String>(Arrays.asList("trace", "debug", "info", "warn", "error")));
    }

    @Override
    public Object visit(final ASTName node, final Object data) {
        final NameDeclaration nameDeclaration = node.getNameDeclaration();
        // ignore imports or methods
        if (!(nameDeclaration instanceof VariableNameDeclaration)) {
            return super.visit(node, data);
        }

        // ignore non slf4j logger
        Class<?> type = ((VariableNameDeclaration) nameDeclaration).getType();
        if (type == null || !type.getName().equals(LOGGER_CLASS)) {
            return super.visit(node, data);
        }

        // get the node that contains the logger
        final ASTPrimaryExpression parentNode = node.getFirstParentOfType(ASTPrimaryExpression.class);

        // get the log level
        final String method = parentNode.getFirstChildOfType(ASTPrimaryPrefix.class).getFirstChildOfType(ASTName.class)
                .getImage().replace(nameDeclaration.getImage() + ".", "");

        // ignore if not a log level
        if (!LOGGER_LEVELS.contains(method)) {
            return super.visit(node, data);
        }

        // find the arguments
        final List<ASTExpression> argumentList = parentNode.getFirstChildOfType(ASTPrimarySuffix.class)
                .getFirstDescendantOfType(ASTArgumentList.class).findChildrenOfType(ASTExpression.class);

        // remove the message parameter
        final ASTPrimaryExpression messageParam = argumentList.remove(0).getFirstDescendantOfType(ASTPrimaryExpression.class);
        final int expectedArguments = expectedArguments(messageParam);

        if (expectedArguments == 0) {
            // ignore if we are not expecting arguments to format the message
            // or if we couldn't analyze the message parameter
            return super.visit(node, data);
        }

        // Remove throwable param, since it is shown separately.
        // But only, if it is not used as a placeholder argument
        if (argumentList.size() > expectedArguments) {
            removeThrowableParam(argumentList);
        }

        if (argumentList.size() < expectedArguments) {
            addViolationWithMessage(data, node, "Missing arguments," + getExpectedMessage(argumentList, expectedArguments));
        } else if (argumentList.size() > expectedArguments) {
            addViolationWithMessage(data, node, "Too many arguments," + getExpectedMessage(argumentList, expectedArguments));
        }

        return super.visit(node, data);
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

    private int expectedArguments(final ASTPrimaryExpression node) {
        int count = 0;
        // look if the logger have a literal message
        if (node.getFirstDescendantOfType(ASTLiteral.class) != null) {
            count = countPlaceholders(node);
        } else if (node.getFirstDescendantOfType(ASTName.class) != null) {
            final String variableName = node.getFirstDescendantOfType(ASTName.class).getImage();
            // look if the message is defined locally
            final List<ASTVariableDeclarator> localVariables = node.getFirstParentOfType(ASTMethodOrConstructorDeclaration.class)
                    .findDescendantsOfType(ASTVariableDeclarator.class);
            count = getAmountOfExpectedArguments(variableName, localVariables);

            if (count == 0) {
                // look if the message is defined in a field
                final List<ASTFieldDeclaration> fieldlist = node.getFirstParentOfType(ASTClassOrInterfaceBody.class)
                        .findDescendantsOfType(ASTFieldDeclaration.class);
                // only look for ASTVariableDeclarator that are Fields
                final List<ASTVariableDeclarator> fields = new ArrayList<ASTVariableDeclarator>(fieldlist.size());
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
                return countPlaceholders(astVariableDeclarator);
            }
        }
        return 0;
    }

    private int countPlaceholders(final AbstractJavaTypeNode node) {
        int result = 0; // zero means, no placeholders, or we could not analyze the message parameter
        ASTLiteral stringLiteral = node.getFirstDescendantOfType(ASTLiteral.class);
        if (stringLiteral != null) {
            result = StringUtils.countMatches(stringLiteral.getImage(), "{}");
        }
        return result;
    }
}

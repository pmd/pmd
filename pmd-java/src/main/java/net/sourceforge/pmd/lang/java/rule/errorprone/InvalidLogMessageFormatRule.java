/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class InvalidLogMessageFormatRule extends AbstractJavaRule {

    /**
     * Finds placeholder for ParameterizedMessages and format specifiers
     * for StringFormattedMessages.
     */
    private static final Pattern PLACEHOLDER_AND_FORMAT_SPECIFIER = 
            Pattern.compile("(\\{\\})|(%(?:\\d\\$)?(?:\\w+)?(?:\\d+)?(?:\\.\\d+)?\\w)");

    private static final Map<String, Set<String>> LOGGERS;

    /**
     * Whitelisted methods of net.logstash.logback.argument.StructuredArguments
     */
    private static final Set<String> STRUCTURED_ARGUMENTS_METHODS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("a", "array", "defer", "e",
                    "entries", "f", "fields", "keyValue",
                    "kv", "r", "raw", "v", "value")));

    static {
        Map<String, Set<String>> loggersMap = new HashMap<>();

        loggersMap.put("org.slf4j.Logger", Collections
                .unmodifiableSet(new HashSet<>(Arrays.asList("trace", "debug", "info", "warn", "error"))));
        loggersMap.put("org.apache.logging.log4j.Logger", Collections
                .unmodifiableSet(new HashSet<>(Arrays.asList("trace", "debug", "info", "warn", "error", "fatal", "all"))));

        LOGGERS = loggersMap;
    }

    private boolean formatIsStringFormat;

    public InvalidLogMessageFormatRule() {
        addRuleChainVisit(ASTImportDeclaration.class);
        addRuleChainVisit(ASTName.class);
    }

    @Override
    public void start(RuleContext ctx) {
        formatIsStringFormat = false;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        if (node.isStatic()) {
            if ("java.lang.String.format".equals(node.getImportedName())) {
                formatIsStringFormat = true;
            }
            if ("java.lang.String".equals(node.getImportedName()) && node.isImportOnDemand()) {
                formatIsStringFormat = true;
            }
        }
        return data;
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

        // remove any arguments before the string message - these might be method calls for
        // logstash markers or structured arguments
        // this also removes any non-string value, e.g. a slf4j-Marker
        // if the type cannot be determined, it is considered not to be a string...
        Iterator<ASTExpression> iterator = argumentList.iterator();
        while (iterator.hasNext()) {
            ASTExpression argument = iterator.next();
            if (!TypeTestUtil.isA(String.class, argument)) {
                iterator.remove();
            } else {
                break;
            }
        }

        if (argumentList.isEmpty()) {
            // no need to check for message params in case no string message found
            return data;
        }

        // remove the message parameter
        final ASTExpression messageParam = argumentList.remove(0);

        // ignore if String.format
        if (isStringFormatCall(messageParam)) {
            return data;
        }

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
        
        // remove any logstash structured arguments at the end
        // but only, if there are not enough placeholders
        if (argumentList.size() > expectedArguments) {
            removePotentialStructuredArguments(argumentList.size() - expectedArguments, argumentList);
        }

        int providedArguments = argumentList.size();

        // last argument could be an array with parameters
        if (argumentList.size() == 1 && TypeTestUtil.isA("java.lang.Object[]", argumentList.get(0))) {
            ASTArrayInitializer arrayInitializer = argumentList.get(0).getFirstDescendantOfType(ASTArrayInitializer.class);
            if (arrayInitializer != null) {
                providedArguments = arrayInitializer.getNumChildren();
            }
        }

        if (providedArguments < expectedArguments) {
            addViolationWithMessage(data, node,
                    "Missing arguments," + getExpectedMessage(argumentList, expectedArguments));
        } else if (providedArguments > expectedArguments) {
            addViolationWithMessage(data, node,
                    "Too many arguments," + getExpectedMessage(argumentList, expectedArguments));
        }

        return data;
    }

    private boolean isNewThrowable(ASTPrimaryExpression last) {
        // in case a new exception is created or the exception class is
        // mentioned.
        return TypeTestUtil.isA(Throwable.class, last.getFirstDescendantOfType(ASTClassOrInterfaceType.class));
    }

    private boolean hasTypeThrowable(TypeNode last) {
        // if the type could be determined already
        return last.getType() != null && TypeTestUtil.isA(Throwable.class, last);
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
        ASTExpression lastExpression = params.get(lastIndex);
        ASTPrimaryExpression last = lastExpression.getFirstDescendantOfType(ASTPrimaryExpression.class);

        if (isNewThrowable(last) || hasTypeThrowable(lastExpression) || isReferencingThrowable(last) || isLambdaParameter(last)) {
            params.remove(lastIndex);
        }
    }

    private boolean isLambdaParameter(ASTPrimaryExpression last) {
        String varName = null;
        ASTPrimaryPrefix prefix = last.getFirstChildOfType(ASTPrimaryPrefix.class);
        if (prefix != null) {
            ASTName name = prefix.getFirstChildOfType(ASTName.class);
            if (name != null) {
                varName = name.getImage();
            }
        }
        if (varName == null) {
            return false;
        }

        Scope scope = prefix == null ? null : prefix.getScope();
        while (scope != null) {
            // Try recursively to find the expected NameDeclaration
            for (NameDeclaration decl : scope.getDeclarations().keySet()) {
                // anonymous classes have no names, so decl.getName() can be null
                if (varName.equals(decl.getName())) {
                    // If the last parameter is a lambda parameter, then we also ignore it - regardless of the type.
                    // This is actually a workaround, since type resolution doesn't resolve the types of lambda parameters.
                    return decl.getNode().getParent() instanceof ASTLambdaExpression;
                }
            }
            scope = scope.getParent();
        }
        return false;
    }

    private String getExpectedMessage(final List<ASTExpression> params, final int expectedArguments) {
        return " expected " + expectedArguments + (expectedArguments > 1 ? " arguments " : " argument ") + "but have "
                + params.size();
    }

    private boolean isStringFormatCall(ASTExpression node) {
        if (node.getNumChildren() > 0 && node.getChild(0) instanceof ASTPrimaryExpression
                && node.getChild(0).getNumChildren() > 0 && node.getChild(0).getChild(0) instanceof ASTPrimaryPrefix
                && node.getChild(0).getChild(0).getNumChildren() > 0 && node.getChild(0).getChild(0).getChild(0) instanceof ASTName) {
            String name = node.getChild(0).getChild(0).getChild(0).getImage();

            return "String.format".equals(name) || formatIsStringFormat && "format".equals(name);
        }
        return false;
    }

    private int expectedArguments(final ASTExpression node) {
        int count = -1;
        // look if the logger has a literal message
        if (node.getFirstDescendantOfType(ASTLiteral.class) != null) {
            count = countPlaceholders(node);
        } else if (node.getFirstDescendantOfType(ASTName.class) != null) {
            NameDeclaration nameDeclaration = node.getFirstDescendantOfType(ASTName.class).getNameDeclaration();
            if (nameDeclaration instanceof VariableNameDeclaration) {
                ASTVariableDeclarator varDecl = ((VariableNameDeclaration) nameDeclaration).getDeclaratorId()
                    .getFirstParentOfType(ASTVariableDeclarator.class);
                // ASTVariableDeclaratorId is also used in formal parameters, lambda parameters, exception vars
                // in that case, there is no variable declaration
                // for local vars and fields, there is a varDecl
                if (varDecl != null) {
                    count = getAmountOfExpectedArguments(varDecl);
                }
            }
        }
        return count;
    }

    private int getAmountOfExpectedArguments(ASTVariableDeclarator astVariableDeclarator) {
        ASTVariableInitializer variableInitializer = astVariableDeclarator
                .getFirstDescendantOfType(ASTVariableInitializer.class);
        ASTExpression expression = null;
        if (variableInitializer != null) {
            expression = variableInitializer.getFirstChildOfType(ASTExpression.class);
        }
        if (expression != null) {
            return countPlaceholders(expression);
        }
        return -1;
    }

    private int countPlaceholders(final ASTExpression node) {
        // ignore if String.format
        if (isStringFormatCall(node)) {
            return -1;
        }

        List<ASTLiteral> literals = getStringLiterals(node);
        if (literals.isEmpty()) {
            // -1 we could not analyze the message parameter
            return -1;
        }

        // if there are multiple literals, we just assume, they are concatenated
        // together...
        int result = 0;
        for (ASTLiteral stringLiteral : literals) {
            Matcher matcher = PLACEHOLDER_AND_FORMAT_SPECIFIER.matcher(stringLiteral.getImage());
            while (matcher.find()) {
                String format = matcher.group();
                if (!"%%".equals(format) && !"%n".equals(format)) {
                    result++;
                }
            }
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

    /**
     * Removes up to {@code maxArgumentsToRemove} arguments from the end of the {@code argumentList},
     * if the argument is a method call to one of the whitelisted StructuredArguments methods.
     *
     * @param maxArgumentsToRemove
     * @param argumentList
     */
    private void removePotentialStructuredArguments(int maxArgumentsToRemove, List<ASTExpression> argumentList) {
        int removed = 0;
        while (!argumentList.isEmpty() && removed < maxArgumentsToRemove) {
            int lastIndex = argumentList.size() - 1;
            ASTExpression argument = argumentList.get(lastIndex);
            if (isStructuredArgumentMethodCall(argument)) {
                argumentList.remove(lastIndex);
                removed++;
            } else {
                // stop if something else is encountered
                break;
            }
        }
    }

    /*
     * └─ Expression == argument
     *    └─ PrimaryExpression
     *       ├─ PrimaryPrefix
     *       │  └─ Name: eg. "keyValue" or "StructuredArguments.keyValue"
     *       └─ PrimarySuffix
     *          └─ Arguments
     */
    private boolean isStructuredArgumentMethodCall(ASTExpression argument) {
        if (argument.getNumChildren() == 1 && argument.getChild(0) instanceof ASTPrimaryExpression) {
            ASTPrimaryExpression primary = (ASTPrimaryExpression) argument.getChild(0);
            if (primary.getNumChildren() == 2 && primary.getChild(1) instanceof ASTPrimarySuffix) {
                ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) primary.getChild(0);
                ASTPrimarySuffix suffix = (ASTPrimarySuffix) primary.getChild(1);
                if (suffix.isArguments() && prefix.getNumChildren() == 1 && prefix.getChild(0) instanceof ASTName) {
                    ASTName name = (ASTName) prefix.getChild(0);
                    return name.getImage().startsWith("StructuredArguments.")
                        || STRUCTURED_ARGUMENTS_METHODS.contains(name.getImage());
                }
            }
        }
        return false;
    }
}

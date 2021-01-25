/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule.security.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.vf.DataType;
import net.sourceforge.pmd.lang.vf.ast.ASTArguments;
import net.sourceforge.pmd.lang.vf.ast.ASTDotExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTExpression;
import net.sourceforge.pmd.lang.vf.ast.ASTIdentifier;
import net.sourceforge.pmd.lang.vf.ast.ASTNegationExpression;
import net.sourceforge.pmd.lang.vf.ast.VfNode;
import net.sourceforge.pmd.lang.vf.ast.VfTypedNode;

/**
 * Helps detect visualforce encoding in EL Expressions
 * (porting over code previously living in VfUnescapeElRule for reusability)
 */

public final class ElEscapeDetector {

    private static final Set<String> SAFE_PROPERTIES = new HashSet<>(Arrays.asList("id", "size", "caseNumber"));
    private static final Set<String> SAFE_BUILTIN_FUNCTIONS = new HashSet<>(Arrays.asList(
            // These DateTime functions accept or return dates, and therefore don't need escaping.
            "addmonths", "date", "datevalue", "datetimevalue", "day", "hour", "millisecond", "minute", "month", "now",
            "second", "timenow", "timevalue", "today", "weekday", "year",
            // These Logical functions accept or return booleans, and therefore don't need escaping.
            "and", "isblank", "isclone", "isnew", "isnull", "isnumber", "not", "or",
            // These Math functions return numbers, and therefore don't require escaping.
            "abs", "ceiling", "exp", "floor", "ln", "log", "max", "mceiling", "mfloor", "min", "mod", "round", "sqrt",
            // These Text functions are safe, either because of what they accept or what they return.
            "begins", "br", "casesafeid", "contains", "find", "getsessionid", "ispickval", "len",
            // These Advanced functions are safe because of what they accept or what they return.
            "currencyrate", "getrecordids", "ischanged", "junctionidlist", "regex", "urlfor"
    ));
    private static final Set<String> FUNCTIONS_WITH_XSSABLE_ARG0 = new HashSet<>(Arrays.asList(
            // For these methods, the first argument is a string that must be escaped.
            "left", "lower", "lpad", "mid", "right", "rpad", "upper"
    ));
    private static final Set<String> FUNCTIONS_WITH_XSSABLE_ARG2 = new HashSet<>(Arrays.asList(
            // For these methods, the third argument is a string that must be escaped.
            "lpad", "rpad"
    ));
    private static final Set<String> SAFE_GLOBAL_VARS = new HashSet<>(Arrays.asList("$action", "$page", "$site",
            "$resource", "$label", "$objecttype", "$component", "$remoteaction", "$messagechannel"));

    private ElEscapeDetector() {
        // utility class
    }

    /**
     * Given an ASTExpression node, determines whether that expression and any expressions under it are properly escaped.
     * @param expression - Represents a VF expression
     * @param escapes - The escape operations that are acceptable in this context
     * @return - True if the expression is properly escaped, otherwise false.
     */
    public static boolean expressionRecursivelyValid(final ASTExpression expression, final EnumSet<Escaping> escapes) {
        // We'll want to iterate over all of this expression's children.
        int childCount = expression.getNumChildren();
        String prevId = "";
        List<ASTExpression> relevantChildren = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            Node child = expression.getChild(i);

            if (child instanceof ASTIdentifier) {
                // How we deal with an Identifier depends on what the next node after it is.
                if (i < childCount - 1) {
                    VfNode nextNode = expression.getChild(i + 1);
                    // If the next node is Arguments or a Dot expression, that means this Identifier represents the name
                    // of a function, or some kind of object. In that case, we might be okay. So we'll store the name
                    // and keep going.
                    if (nextNode instanceof ASTArguments || nextNode instanceof ASTDotExpression) {
                        prevId = child.getImage();
                        continue;
                    }
                }
                // If there's no next node, or the next node isn't one of those types, then this Identifier is a raw variable.
                if (typedNodeIsSafe((ASTIdentifier) child)) {
                    // If the raw variable is of an inherently safe type, we can keep going.
                    continue;
                }
                // If the raw variable isn't inherently safe, then it's dangerous, and must be escaped.
                return false;
            } else if (child instanceof ASTArguments) {
                // An Arguments node means we're looking at some kind of function call.
                // If it's one of our designated escape functions, we're in the clear and we can keep going.
                // Also, some built-in functions are inherently safe, which would mean we're good to continue.
                if (functionIsEscape(prevId, escapes) || functionInherentlySafe(prevId)) {
                    continue;
                }

                // Otherwise, identify the argument expressions that must be checked, and add them to the list.
                relevantChildren.addAll(getXssableArguments(prevId, (ASTArguments) child));
            } else if (child instanceof ASTDotExpression) {
                // Dot expressions mean we're doing accessing properties of variables.
                // If the variable is one of the definitely-safe global variables, then we're in the clear.
                if (isSafeGlobal(prevId)) {
                    continue;
                }
                // If the node after this one is also a Dot expression, then this is a chained access, and we can't make
                // any final judgements.
                if (i < childCount - 1 && expression.getChild(i + 1) instanceof ASTDotExpression) {
                    continue;
                }
                // If none of those things are true, then we need to determine whether the field being accessed is
                // definitely safe.
                ASTIdentifier propId = child.getFirstChildOfType(ASTIdentifier.class);
                // If there's an identifier for a field/property, we need to check whether that property is inherently safe,
                // either because it corresponds to a safe field or because its data type is known to be safe.
                if (propId != null && !isSafeProperty(propId.getImage()) && !typedNodeIsSafe(propId)) {
                    // If the node isn't definitely safe, it ought to be escaped. Return false.
                    return false;
                }
            } else if (child instanceof ASTExpression) {
                // Expressions should always be added to the list.
                relevantChildren.add((ASTExpression) child);
            }
        }
        // Just because there's nothing immediately wrong with this node doesn't mean its children are guaranteed to be
        // fine. Iterate over all of the children and make a recursive call. If any of those calls return false, we need
        // to relay that back up the chain.
        for (ASTExpression e : relevantChildren) {
            if (!expressionRecursivelyValid(e, escapes)) {
                return false;
            }
        }
        // If we didn't find a reason to return false, we're good. Return true.
        return true;
    }

    /**
     * Indicates whether the provided function name corresponds to any of the provided escape functions.
     * @param functionName - The name of a VF function
     * @param escapes - A set of acceptable escape functions (e.g., JSENCODE, HTMLENCODE, etc)
     * @return - True if the function is a viable escape.
     */
    private static boolean functionIsEscape(String functionName, EnumSet<Escaping> escapes) {
        // If one of the escapes we were passed is ANY, then we should replace the provided set with one that contains
        // all possible escapes.
        EnumSet<Escaping> handledEscapes = escapes.contains(Escaping.ANY) ? EnumSet.allOf(Escaping.class) : escapes;
        for (Escaping e : handledEscapes) {
            if (functionName.equalsIgnoreCase(e.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Certain built-in functions are inherently safe and don't require any escaping. This method determines whether the
     * function name provided corresponds to one of those methods.
     * @param functionName - The name of a VF function
     * @return - True if the function is an inherently safe built-in function
     */
    private static boolean functionInherentlySafe(String functionName) {
        String lowerCaseName = functionName.toLowerCase(Locale.ROOT);
        return SAFE_BUILTIN_FUNCTIONS.contains(lowerCaseName);
    }

    /**
     * Given a function name and a node containing its arguments, returns the ASTExpression nodes corresponding to arguments
     * that require escaping. Frequently, this will be all arguments, but not always.
     * @param functionName - The name of a function being called
     * @param arguments - Contains the ASTExpression nodes representing the function's arguments
     * @return - ASTExpression list containing all arguments that are vulnerable to XSS.
     */
    private static List<ASTExpression> getXssableArguments(String functionName, ASTArguments arguments) {
        List<ASTExpression> exprs = new ArrayList<>();
        int argCount = arguments.getNumChildren();
        if (argCount != 0) {
            String lowerCaseName = functionName.toLowerCase(Locale.ROOT);
            List<Integer> indicesToCheck = new ArrayList<>();

            // See if the function name corresponds to one of the built-in functions that don't require us to examine
            // every argument.
            if ("case".equals(lowerCaseName)) {
                // CASE accepts (exx, val1, res1, val2, res2, ...else_res). We want resX and else_res.
                for (int i = 2; i < argCount; i += 2) {
                    indicesToCheck.add(i);
                }
                indicesToCheck.add(argCount - 1);
            } else if ("if".equals(lowerCaseName)) {
                // IF accepts (test, if_true, if_false). We care about if_true and if_false.
                indicesToCheck.add(1);
                indicesToCheck.add(2);
            } else {
                boolean checkAllArgs = true;
                // If this is a function with an XSSable first arg, add 0 to the array.
                if (FUNCTIONS_WITH_XSSABLE_ARG0.contains(lowerCaseName)) {
                    checkAllArgs = false;
                    indicesToCheck.add(0);
                }
                // If this is a function that has at least 3 args, and the third arg is XSSable, add 2 to the array.
                if (argCount > 2 && FUNCTIONS_WITH_XSSABLE_ARG2.contains(lowerCaseName)) {
                    checkAllArgs = false;
                    indicesToCheck.add(2);
                }
                // If the function has no known pattern for argument checking, all arguments must be checked.
                if (checkAllArgs) {
                    for (int i = 0; i < argCount; i++) {
                        indicesToCheck.add(i);
                    }
                }
            }

            // Add each of the targeted arguments to the return array if they represent an Expression node. (They always
            // should, but better safe than sorry.)
            for (int i : indicesToCheck) {
                VfNode ithArg = arguments.getChild(i);
                if (ithArg instanceof ASTExpression) {
                    exprs.add((ASTExpression) ithArg);
                }
            }
        }
        return exprs;
    }

    /**
     * VF has global variables prefixed with a '$'. Some of those are inherently safe to access, and this method determines
     * whether the provided ID corresponds to one of those globals.
     * @param id - Identifier of some variable.
     * @return - True if the global is inherently safe.
     */
    private static boolean isSafeGlobal(String id) {
        String lowerCaseId = id.toLowerCase(Locale.ROOT);
        return SAFE_GLOBAL_VARS.contains(lowerCaseId);
    }

    /**
     * Determines whether the property being referenced is inherently safe, or if it requires XSS escaping.
     * @param propertyName - The name of a field or property being referenced.
     * @return - True if that field/property is inherently safe
     */
    private static boolean isSafeProperty(String propertyName) {
        String lowerCaseName = propertyName.toLowerCase(Locale.ROOT);
        return SAFE_PROPERTIES.contains(lowerCaseName);
    }

    private static boolean innerContainsSafeFields(final VfNode expression) {
        for (VfNode child : expression.children()) {

            if (child instanceof ASTIdentifier && isSafeProperty(child.getImage())) {
                return true;
            }

            if (child instanceof ASTArguments) {
                if (containsSafeFields((ASTArguments) child)) {
                    return true;
                }
            }

            if (child instanceof ASTDotExpression) {
                if (innerContainsSafeFields((ASTDotExpression) child)) {
                    return true;
                }
            }

        }

        return false;
    }

    public static boolean containsSafeFields(final VfNode expression) {
        final ASTExpression ex = expression.getFirstChildOfType(ASTExpression.class);

        return ex != null && innerContainsSafeFields(ex);
    }

    public static boolean startsWithSafeResource(final ASTElExpression el) {
        final ASTExpression expression = el.getFirstChildOfType(ASTExpression.class);
        if (expression != null) {
            final ASTNegationExpression negation = expression.getFirstChildOfType(ASTNegationExpression.class);
            if (negation != null) {
                return true;
            }

            final ASTIdentifier id = expression.getFirstChildOfType(ASTIdentifier.class);
            if (id != null) {
                List<ASTArguments> args = expression.findChildrenOfType(ASTArguments.class);

                if (!args.isEmpty()) {
                    return functionInherentlySafe(id.getImage());
                } else {
                    return isSafeGlobal(id.getImage());
                }
            }
        }
        return false;
    }

    public static boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression, Escaping escape) {
        return doesElContainAnyUnescapedIdentifiers(elExpression, EnumSet.of(escape));
    }

    public static boolean doesElContainAnyUnescapedIdentifiers(final ASTElExpression elExpression,
                                                         EnumSet<Escaping> escapes) {
        if (elExpression == null) {
            return false;
        }

        final Set<ASTIdentifier> nonEscapedIds = new HashSet<>();

        final List<ASTExpression> exprs = elExpression.findChildrenOfType(ASTExpression.class);
        for (final ASTExpression expr : exprs) {

            if (innerContainsSafeFields(expr)) {
                continue;
            }

            if (expressionContainsSafeDataNodes(expr)) {
                continue;
            }

            final List<ASTIdentifier> ids = expr.findChildrenOfType(ASTIdentifier.class);
            for (final ASTIdentifier id : ids) {
                boolean isEscaped = false;

                for (Escaping e : escapes) {

                    if (id.getImage().equalsIgnoreCase(e.toString())) {
                        isEscaped = true;
                        break;
                    }

                    if (e.equals(Escaping.ANY)) {
                        for (Escaping esc : Escaping.values()) {
                            if (id.getImage().equalsIgnoreCase(esc.toString())) {
                                isEscaped = true;
                                break;
                            }
                        }
                    }

                }

                if (!isEscaped) {
                    nonEscapedIds.add(id);
                }
            }

        }

        return !nonEscapedIds.isEmpty();
    }

    /**
     * Return true if the type of all data nodes can be determined and none of them require escaping
     * @param expression
     */
    private static boolean expressionContainsSafeDataNodes(ASTExpression expression) {
        try {
            for (VfTypedNode node : expression.getDataNodes().keySet()) {
                if (!typedNodeIsSafe(node)) {
                    return false;
                }
            }

            return true;
        } catch (ASTExpression.DataNodeStateException e) {
            return false;
        }
    }

    private static boolean typedNodeIsSafe(VfTypedNode node) {
        DataType dataType = node.getDataType();
        return dataType != null && !dataType.requiresEscaping;
    }

    public enum Escaping {
        HTMLENCODE("HTMLENCODE"),
        URLENCODE("URLENCODE"),
        JSINHTMLENCODE("JSINHTMLENCODE"),
        JSENCODE("JSENCODE"),
        ANY("ANY");

        private final String text;

        Escaping(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.util;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtAssignableExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtAssignment;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtDirectlyAssignableExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionBody;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionValueParameters;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaLiteral;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtLambdaParameters;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPrimaryExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtSimpleIdentifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtVariableDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;

/**
 * Static utility methods for navigating the PMD 7 ANTLR-based Kotlin AST.
 *
 * <p>Adapted from the jPinpoint project (com.jpinpoint.perf.lang.kotlin.util.KotlinAstUtil).</p>
 */
public final class KotlinAstUtil {

    private KotlinAstUtil() { /* utility class */ }

    /**
     * Returns the text of the first terminal-node child of a {@link KtSimpleIdentifier},
     * or {@code null} if the node itself is {@code null} or has no terminal children.
     */
    public static String textOf(KtSimpleIdentifier simpleId) {
        if (simpleId == null) {
            return null;
        }
        KotlinTerminalNode token = simpleId.children(KotlinTerminalNode.class).first();
        return token != null ? token.getText() : null;
    }

    /**
     * Returns the text of the SimpleIdentifier child of a {@link KtPrimaryExpression},
     * or {@code null} if none is present.
     */
    public static String textOf(KtPrimaryExpression pe) {
        if (pe == null) {
            return null;
        }
        return textOf(pe.simpleIdentifier());
    }

    /**
     * Returns {@code true} if the nearest enclosing ancestor of type {@code T}
     * for {@code node} is exactly {@code ancestor}. Used to scope descendant searches
     * to a single enclosing node without crossing into nested scopes of the same type.
     */
    public static <T extends Node> boolean isWithin(Node node, Class<T> type, T ancestor) {
        return node.ancestors(type).first() == ancestor;
    }

    /**
     * Extracts the simple variable name from the left-hand side of an assignment.
     * Handles both plain assignment ({@code x = ...}) and compound assignment ({@code x += ...}).
     * Returns {@code null} if the LHS is not a simple identifier (e.g. property access, index).
     */
    public static String getLhsVarName(KtAssignment assignment) {
        KtDirectlyAssignableExpression dae = assignment.directlyAssignableExpression();
        if (dae != null) {
            String name = textOf(dae.simpleIdentifier());
            if (name != null) {
                return name;
            }
        }
        KtAssignableExpression ae = assignment.assignableExpression();
        if (ae != null) {
            KotlinTerminalNode token = ae.descendants(KotlinTerminalNode.class).first();
            if (token != null) {
                return token.getText();
            }
        }
        return null;
    }

    /**
     * Returns the names of all parameters in the function declaration (regardless of type).
     */
    public static Set<String> collectParamNames(KtFunctionDeclaration funcDecl) {
        Set<String> result = new HashSet<>();
        if (funcDecl == null) {
            return result;
        }
        KtFunctionValueParameters params = funcDecl.functionValueParameters();
        if (params == null) {
            return result;
        }
        for (KtFunctionValueParameter param : params.functionValueParameter()) {
            KtParameter p = param.parameter();
            if (p != null) {
                String name = textOf(p.simpleIdentifier());
                if (name != null) {
                    result.add(name);
                }
            }
        }
        return result;
    }

    /**
     * Returns the explicit parameter names of a lambda literal.
     * Returns an empty set for lambdas that use the implicit {@code it} parameter.
     */
    public static Set<String> collectLambdaParamNames(KtLambdaLiteral lambda) {
        Set<String> result = new HashSet<>();
        KtLambdaParameters params = lambda.lambdaParameters();
        if (params == null) {
            return result;
        }
        for (KtLambdaParameter param : params.lambdaParameter()) {
            KtVariableDeclaration varDecl = param.variableDeclaration();
            if (varDecl != null) {
                String name = textOf(varDecl.simpleIdentifier());
                if (name != null) {
                    result.add(name);
                }
            }
        }
        return result;
    }

    /**
     * Returns the names of all local variables ({@code PropertyDeclaration}s) declared anywhere
     * within {@code functionBody}, including inside nested lambdas.
     */
    public static Set<String> collectLocalVarNames(KtFunctionBody functionBody) {
        Set<String> result = new HashSet<>();
        if (functionBody == null) {
            return result;
        }
        for (KtPropertyDeclaration propDecl
                : functionBody.descendants(KtPropertyDeclaration.class).toList()) {
            KtVariableDeclaration varDecl = propDecl.variableDeclaration();
            if (varDecl != null) {
                String name = textOf(varDecl.simpleIdentifier());
                if (name != null) {
                    result.add(name);
                }
            }
        }
        return result;
    }

    /**
     * Returns the names of all mutable ({@code var}) class fields declared in the class body
     * that encloses {@code node}.
     */
    public static Set<String> collectClassVarFieldNames(Node node) {
        Set<String> result = new HashSet<>();
        KtClassDeclaration classDecl =
                node.ancestors(KtClassDeclaration.class).first();
        if (classDecl == null) {
            return result;
        }
        for (KtPropertyDeclaration propDecl
                : classDecl.descendants(KtPropertyDeclaration.class).toList()) {
            if (propDecl.VAR() != null) {
                KtVariableDeclaration varDecl = propDecl.variableDeclaration();
                if (varDecl != null) {
                    String name = textOf(varDecl.simpleIdentifier());
                    if (name != null) {
                        result.add(name);
                    }
                }
            }
        }
        return result;
    }
}

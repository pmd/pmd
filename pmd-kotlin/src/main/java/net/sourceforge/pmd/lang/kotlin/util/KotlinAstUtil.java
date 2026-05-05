/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.util;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;

/**
 * Static utility methods for navigating the PMD 7 ANTLR-based Kotlin AST.
 *
 * <p><b>Key gotcha:</b> {@code BaseAntlrTerminalNode.getTokenKind()} returns the token-stream
 * index, not the token-type constant. Generated accessor methods such as {@code ADD()} always
 * return {@code null}. Always use text comparison instead: {@code t.getText().equals("+=")} etc.</p>
 *
 * <p>Adapted from the jPinpoint project (com.jpinpoint.perf.lang.kotlin.util.KotlinAstUtil).</p>
 */
public final class KotlinAstUtil {

    private KotlinAstUtil() { /* utility class */ }

    /**
     * Returns the text of the first terminal-node child of a {@link KotlinParser.KtSimpleIdentifier},
     * or {@code null} if the node itself is {@code null} or has no terminal children.
     */
    public static String getIdentifierText(KotlinParser.KtSimpleIdentifier simpleId) {
        if (simpleId == null) {
            return null;
        }
        KotlinTerminalNode token = simpleId.children(KotlinTerminalNode.class).first();
        return token != null ? token.getText() : null;
    }

    /**
     * Returns the text of the SimpleIdentifier child of a {@link KotlinParser.KtPrimaryExpression},
     * or {@code null} if none is present.
     */
    public static String getPrimaryExpressionText(KotlinParser.KtPrimaryExpression pe) {
        if (pe == null) {
            return null;
        }
        return getIdentifierText(pe.simpleIdentifier());
    }

    /**
     * Returns {@code true} if any terminal-node descendant of {@code type} has text equal to
     * {@code typeName}. Useful for checking type annotations like {@code var x: String}.
     */
    public static boolean typeContainsName(KotlinParser.KtType type, String typeName) {
        if (type == null || typeName == null) {
            return false;
        }
        return type.descendants(KotlinTerminalNode.class).any(t -> typeName.equals(t.getText()));
    }

    /**
     * Returns {@code true} if the nearest enclosing {@link KotlinParser.KtFunctionBody} ancestor
     * of {@code node} is exactly {@code body}. Used to restrict descendant searches to a single
     * function body without crossing into nested lambdas or local functions.
     */
    public static boolean isDirectChildOfFunctionBody(Node node, KotlinParser.KtFunctionBody body) {
        return node.ancestors(KotlinParser.KtFunctionBody.class).first() == body;
    }

    /**
     * Returns {@code true} if the nearest enclosing {@link KotlinParser.KtFunctionDeclaration}
     * ancestor of {@code node} is exactly {@code funcDecl}. Used to scope AST searches to a
     * single function declaration without crossing into nested local functions.
     * Note: lambdas ({@code KtFunctionLiteral}) are not {@code KtFunctionDeclaration},
     * so they are transparent to this check.
     */
    public static boolean isDirectDescendantOf(Node node,
                                               KotlinParser.KtFunctionDeclaration funcDecl) {
        return node.ancestors(KotlinParser.KtFunctionDeclaration.class).first() == funcDecl;
    }

    /**
     * Extracts the simple variable name from the left-hand side of an assignment.
     * Handles both plain assignment ({@code x = ...}) and compound assignment ({@code x += ...}).
     * Returns {@code null} if the LHS is not a simple identifier (e.g. property access, index).
     */
    public static String getLhsVarName(KotlinParser.KtAssignment assignment) {
        KotlinParser.KtDirectlyAssignableExpression dae = assignment.directlyAssignableExpression();
        if (dae != null && dae.simpleIdentifier() != null) {
            KotlinTerminalNode token = dae.simpleIdentifier().children(KotlinTerminalNode.class).first();
            if (token != null) {
                return token.getText();
            }
        }
        KotlinParser.KtAssignableExpression ae = assignment.assignableExpression();
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
    public static Set<String> collectAllParamNames(KotlinParser.KtFunctionDeclaration funcDecl) {
        Set<String> result = new HashSet<>();
        if (funcDecl == null) {
            return result;
        }
        KotlinParser.KtFunctionValueParameters params = funcDecl.functionValueParameters();
        if (params == null) {
            return result;
        }
        for (KotlinParser.KtFunctionValueParameter param : params.functionValueParameter()) {
            KotlinParser.KtParameter p = param.parameter();
            if (p != null) {
                String name = getIdentifierText(p.simpleIdentifier());
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
    public static Set<String> collectLocalVarNames(KotlinParser.KtFunctionBody functionBody) {
        Set<String> result = new HashSet<>();
        if (functionBody == null) {
            return result;
        }
        for (KotlinParser.KtPropertyDeclaration propDecl :
                functionBody.descendants(KotlinParser.KtPropertyDeclaration.class).toList()) {
            KotlinParser.KtVariableDeclaration varDecl = propDecl.variableDeclaration();
            if (varDecl != null) {
                String name = getIdentifierText(varDecl.simpleIdentifier());
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
        KotlinParser.KtClassDeclaration classDecl =
                node.ancestors(KotlinParser.KtClassDeclaration.class).first();
        if (classDecl == null) {
            return result;
        }
        for (KotlinParser.KtPropertyDeclaration propDecl :
                classDecl.descendants(KotlinParser.KtPropertyDeclaration.class).toList()) {
            if (propDecl.children(KotlinTerminalNode.class).any(t -> "var".equals(t.getText()))) {
                KotlinParser.KtVariableDeclaration varDecl = propDecl.variableDeclaration();
                if (varDecl != null) {
                    String name = getIdentifierText(varDecl.simpleIdentifier());
                    if (name != null) {
                        result.add(name);
                    }
                }
            }
        }
        return result;
    }
}

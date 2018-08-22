package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.Matcher
import io.kotlintest.Result
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.NWrapper
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.java.ParserTstUtil


const val defaultJavaVersion = "11"


fun <N : Node> Node.getFirstDescendantOrSelfOfType(klass: Class<N>): N? {
    return when (this) {
        klass::isInstance -> {
            @SuppressWarnings("UNCHECKED_CAST")
            val n = this as N
            n
        }
        else -> getFirstDescendantOfType(klass)
    }
}


/**
 * Parse the string in an expression context, and finds the first descendant of type [N].
 * This is only suitable if you've ascertained that the node to find is the first such
 * descendant.
 *
 * @param expr The expression to parse
 * @param javaVersion The java parser version to use
 * @param N type of node to find
 *
 * @return The first descendant of type [N] found in the parsed expression
 *
 * @throws NoSuchElementException If no node of type [N] is found in the expression
 * @throws ParseException If the expression is no valid expression for the given language version
 */
inline fun <reified N : Node> parseExpression(expr: String, javaVersion: String = defaultJavaVersion): N =
        parseAstExpression(expr, javaVersion).getFirstDescendantOrSelfOfType(N::class.java)
        ?: throw NoSuchElementException("No node of type ${N::class.java.simpleName} in the given expression:\n\t$expr")


/**
 * Parse the string in an expression context. The parsed expression is the child of the
 * returned [ASTExpression]. Sometimes there may be many layers between the returned node
 * and the node of interest, e.g. when the expression is contained in a PrimaryExpression/PrimaryPrefix
 * construct, in which case [parseExpression] saves some keystrokes.
 *
 * @param expr The expression to parse
 * @param javaVersion The java parser version to use
 *
 * @return An [ASTExpression] whose child is the given expression
 *
 * @throws ParseException If the argument is no valid expression for the given language version
 */
fun parseAstExpression(expr: String, javaVersion: String = defaultJavaVersion): ASTExpression {

    val source = """
        class Foo {
            {
              Object o = $expr;
            }
        }
    """.trimIndent()

    val acu = ParserTstUtil.parseAndTypeResolveJava(javaVersion, source)

    return acu.getFirstDescendantOfType(ASTVariableInitializer::class.java).jjtGetChild(0) as ASTExpression
}


/**
 * Parse the string in a statement context, and finds the first descendant of type [N].
 * This is only sound if you've ascertained that the node you're interested in exists
 * and is the first such descendant.
 *
 * @param stmt The statement to parse
 * @param javaVersion The java parser version to use
 * @param N The type of node to find
 *
 * @return The first descendant of type [N] found in the parsed expression
 *
 * @throws NoSuchElementException If no node of type [N] is found in the statement
 * @throws ParseException If the argument is no valid statement for the given language version.
 *                        Don't forget the semicolon!
 */
inline fun <reified N : Node> parseStatement(stmt: String, javaVersion: String = defaultJavaVersion): N =
        parseAstStatement(stmt, javaVersion).getFirstDescendantOrSelfOfType(N::class.java)
        ?: throw NoSuchElementException("No node of type ${N::class.java.simpleName} in the given statement:\n\t$stmt")

/**
 * Parse the string in a statement context. The parsed statement is the child of the
 * returned [ASTBlockStatement]. Note that [parseStatement] can save you some keystrokes
 * because it finds a descendant of the wanted type.
 *
 * @param statement The statement to parse
 * @param javaVersion The java parser version to use
 *
 * @return An [ASTBlockStatement] whose child is the given statement
 *
 * @throws ParseException If the argument is no valid statement for the given language version.
 *                        Don't forget the semicolon!
 */
fun parseAstStatement(statement: String, javaVersion: String = defaultJavaVersion): ASTBlockStatement {

    // place the param in a statement parsing context
    val source = """
            class Foo {
               {
                 $statement
               }
            }
        """.trimIndent()

    val root = ParserTstUtil.parseAndTypeResolveJava(javaVersion, source)

    return root.getFirstDescendantOfType(ASTBlockStatement::class.java)
}


inline fun <reified N : Node> matchExpr(ignoreChildren: Boolean = false,
                                        javaVersion: String = defaultJavaVersion,
                                        matchFirstDescendantOrSelf: Boolean = true,
                                        noinline nodeSpec: NWrapper<N>.() -> Unit): Matcher<String> = object : Matcher<String> {

    override fun test(value: String): Result =
            when (matchFirstDescendantOrSelf) {
                true -> parseExpression<N>(value, javaVersion)
                false -> parseAstExpression(value, javaVersion)
            }.let {
                matchNode(ignoreChildren, nodeSpec).test(it)

            }

}

inline fun <reified N : Node> matchStmt(ignoreChildren: Boolean = false,
                                        javaVersion: String = defaultJavaVersion,
                                        matchFirstDescendantOrSelf: Boolean = true,
                                        noinline nodeSpec: NWrapper<N>.() -> Unit): Matcher<String> = object : Matcher<String> {

    override fun test(value: String): Result =
            when (matchFirstDescendantOrSelf) {
                true -> parseStatement<N>(value, javaVersion)
                false -> parseAstStatement(value, javaVersion)
            }.let {
                matchNode(ignoreChildren, nodeSpec).test(it)

            }

}

// also need e.g. parseDeclaration

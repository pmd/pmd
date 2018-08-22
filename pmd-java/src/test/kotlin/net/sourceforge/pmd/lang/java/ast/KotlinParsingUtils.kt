package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.specs.AbstractFunSpec
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.NWrapper
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.java.ParserTstUtil


val defaultJavaVersion = JavaVersion.J11

/**
 * Finds the first descendant of type [N] of [this] node which is
 * accessible in a straight line. The descendant must be accessible
 * from the [this] on a path where each node has a single child.
 *
 * If one node has another child, the search is aborted and the method
 * returns null.
 */
fun <N : Node> Node.findFirstNodeOnStraightLine(klass: Class<N>): N? {
    return when {
        klass.isInstance(this) -> {
            @SuppressWarnings("UNCHECKED_CAST")
            val n = this as N
            n
        }
        else -> if (this.jjtGetNumChildren() == 1) jjtGetChild(0).findFirstNodeOnStraightLine(klass) else null
    }
}


/**
 * Parse the string in an expression context, and finds the first descendant of type [N].
 * The descendant is searched for by [findFirstNodeOnStraightLine], to prevent accidental
 * mis-selection of a node. In such a case, a [NoSuchElementException] is thrown, and you
 * should fix your test case.
 *
 * @param expr The expression to parse
 * @param javaVersion The java parser version to use
 * @param N type of node to find
 *
 * @return The first descendant of type [N] found in the parsed expression
 *
 * @throws NoSuchElementException If no node of type [N] is found by [findFirstNodeOnStraightLine]
 * @throws ParseException If the expression is no valid expression for the given language version
 */
inline fun <reified N : Node> parseExpression(expr: String, javaVersion: JavaVersion = defaultJavaVersion): N =
        parseAstExpression(expr, javaVersion).findFirstNodeOnStraightLine(N::class.java)
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
fun parseAstExpression(expr: String, javaVersion: JavaVersion = defaultJavaVersion): ASTExpression {

    val source = """
        class Foo {
            {
              Object o = $expr;
            }
        }
    """.trimIndent()

    val acu = ParserTstUtil.parseAndTypeResolveJava(javaVersion.pmdName, source)

    return acu.getFirstDescendantOfType(ASTVariableInitializer::class.java).jjtGetChild(0) as ASTExpression
}


/**
 * Parse the string in a statement context, and finds the first descendant of type [N].
 * The descendant is searched for by [findFirstNodeOnStraightLine], to prevent accidental
 * mis-selection of a node. In such a case, a [NoSuchElementException] is thrown, and you
 * should fix your test case.
 *
 * @param stmt The statement to parse
 * @param javaVersion The java parser version to use
 * @param N The type of node to find
 *
 * @return The first descendant of type [N] found in the parsed expression
 *
 * @throws NoSuchElementException If no node of type [N] is found by [findFirstNodeOnStraightLine]
 * @throws ParseException If the argument is no valid statement for the given language version.
 *                        Don't forget the semicolon!
 */
inline fun <reified N : Node> parseStatement(stmt: String, javaVersion: JavaVersion = defaultJavaVersion): N =
        parseAstStatement(stmt, javaVersion).findFirstNodeOnStraightLine(N::class.java)
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
fun parseAstStatement(statement: String, javaVersion: JavaVersion = defaultJavaVersion): ASTBlockStatement {

    // place the param in a statement parsing context
    val source = """
            class Foo {
               {
                 $statement
               }
            }
        """.trimIndent()

    val root = ParserTstUtil.parseAndTypeResolveJava(javaVersion.pmdName, source)

    return root.getFirstDescendantOfType(ASTBlockStatement::class.java)
}


inline fun <reified N : Node> matchExpr(ignoreChildren: Boolean = false,
                                        javaVersion: JavaVersion = defaultJavaVersion,
                                        noinline nodeSpec: NWrapper<N>.() -> Unit): Matcher<String> = object : Matcher<String> {

    override fun test(value: String): Result =
            matchNode(ignoreChildren, nodeSpec).test(parseExpression<N>(value, javaVersion))

}

inline fun <reified N : Node> matchStmt(ignoreChildren: Boolean = false,
                                        javaVersion: JavaVersion = defaultJavaVersion,
                                        noinline nodeSpec: NWrapper<N>.() -> Unit): Matcher<String> = object : Matcher<String> {

    override fun test(value: String): Result =
            matchNode(ignoreChildren, nodeSpec).test(parseStatement<N>(value, javaVersion))

}


enum class JavaVersion {
    J1_3, J1_4, J1_5, J1_6, J1_7, J1_8, J9, J10, J11;

    val pmdName: String = name.removePrefix("J").replace('_', '.')
}





fun AbstractFunSpec.parserTest(name: String,
                               javaVersion: JavaVersion = defaultJavaVersion,
                               assertions: ParsingCtx.() -> Unit) {
    test(name) {

        val ctx = ParsingCtx(javaVersion)

        ctx.assertions()
    }
}


data class ParsingCtx constructor(val javaVersion: JavaVersion) {


    inline fun <reified N : Node> matchExpr(ignoreChildren: Boolean = false,
                                            noinline nodeSpec: NWrapper<N>.() -> Unit): Matcher<String> =
            matchExpr(ignoreChildren, javaVersion, nodeSpec)

    inline fun <reified N : Node> matchStmt(ignoreChildren: Boolean = false,
                                            noinline nodeSpec: NWrapper<N>.() -> Unit): Matcher<String> =
            matchStmt(ignoreChildren, javaVersion, nodeSpec)
}








// also need e.g. parseDeclaration

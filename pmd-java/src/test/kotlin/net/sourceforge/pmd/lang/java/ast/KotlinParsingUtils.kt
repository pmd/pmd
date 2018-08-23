package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AbstractFunSpec
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.NWrapper
import net.sourceforge.pmd.lang.ast.test.matchNode
import net.sourceforge.pmd.lang.java.ParserTstUtil


/**
 * Represents the different Java language versions.
 */
enum class JavaVersion : Comparable<JavaVersion> {
    J1_3, J1_4, J1_5, J1_6, J1_7, J1_8, J9, J10, J11;

    /** Name suitable for use with e.g. [ParserTstUtil.parseAndTypeResolveJava] */
    val pmdName: String = name.removePrefix("J").replace('_', '.')

    /**
     * Overloads the range operator, e.g. (`J9..J11`).
     * If both operands are the same, a singleton list is returned.
     */
    operator fun rangeTo(last: JavaVersion): List<JavaVersion> =
            when {
                last == this -> listOf(this)
                last.ordinal > this.ordinal -> values().filter { ver -> ver >= this && ver <= last }
                else -> values().filter { ver -> ver <= this && ver >= last }
            }

    companion object {
        val Latest = values().last()
        val Earliest = values().first()
    }
}


/**
 * Specify several tests at once for different java versions.
 * One test will be generated per version in [javaVersions].
 * Use [focusOn] to execute one test in isolation.
 *
 * @param name Name of the test. Will be postfixed by the specific
 *             java version used to run it
 * @param javaVersions Language versions for which to generate tests
 * @param focusOn Sets the java version of the test to isolate
 * @param assertions Assertions and further configuration
 *                   to perform with the parsing context
 */
fun AbstractFunSpec.parserTest(name: String,
                               javaVersions: List<JavaVersion>,
                               focusOn: JavaVersion? = null,
                               assertions: ParsingTestCtx.() -> Unit) {

    javaVersions.forEach {

        val focus = if (focusOn != null && focusOn == it) "f:" else ""

        test("$focus$name (Java ${it.pmdName})") {
            ParsingTestCtx(it).assertions()
        }
    }
}

/**
 * Specify a new test for a single java version. To execute the test in isolation,
 * prefix the name with `"f:"`.
 *
 * @param name Name of the test. Will be postfixed by the [javaVersion]
 * @param javaVersion Language version to use when parsing
 * @param assertions Assertions and further configuration
 *                   to perform with the parsing context
 */
fun AbstractFunSpec.parserTest(name: String,
                               javaVersion: JavaVersion = JavaVersion.Latest,
                               assertions: ParsingTestCtx.() -> Unit) {
    parserTest(name, listOf(javaVersion), null, assertions)
}


/**
 * Extensible environment to describe parse/match testing workflows in a concise way.
 * Can be used inside of a [io.kotlintest.specs.FunSpec] with [parserTest].
 *
 * Parsing contexts allow to parse a string containing only the node you're interested
 * in instead of writing up a full class that the parser can handle. See [parseAstExpression],
 * [parseAstStatement].
 *
 * The methods [parseExpression] and [parseStatement] add some sugar to those by skipping
 * some nodes we're not interested in to find the node of interest using their reified type
 * parameter.
 *
 * These are implicitly used by [matchExpr] and [matchStmt], which specify a matcher directly
 * on the strings, using their type parameter and the info in this test context to parse, find
 * the node, and execute the matcher in a single call. These may be used by [io.kotlintest.should],
 * e.g.
 *
 *      parserTest("Test ShiftExpression operator") {
 *          "1 >> 2" should matchExpr<ASTShiftExpression>(ignoreChildren = true) {
 *              it.operator shouldBe ">>"
 *          }
 *      }
 *
 *
 * Import statements in the parsing contexts can be configured by adding types to [importedTypes],
 * or strings to [otherImports].
 *
 * Technically the utilities provided by this class may be used outside of [io.kotlintest.specs.FunSpec]s,
 * e.g. in regular JUnit tests, but I think we should strive to uniformize our testing style,
 * especially since KotlinTest defines so many.
 *
 * TODO allow to reference an existing type as the parsing context, for full type resolution
 *
 * @property javaVersion The java version that will be used for parsing.
 * @property importedTypes Types to import at the beginning of parsing contexts
 * @property otherImports Other imports, without the `import` and semicolon
 */
data class ParsingTestCtx(val javaVersion: JavaVersion = JavaVersion.Latest,
                          val importedTypes: MutableList<Class<*>> = mutableListOf(),
                          val otherImports: MutableList<String> = mutableListOf()) {

    /** Imports to add to the top of the parsing contexts. */
    private val imports: List<String>
        get() {
            val types = importedTypes.mapNotNull { it.canonicalName }.map { "import $it;" }
            return types + otherImports.map { "import $it;" }
        }

    /**
     * Returns a String matcher that parses the node using [parseExpression] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     *
     */
    inline fun <reified N : Node> matchExpr(ignoreChildren: Boolean = false,
                                            noinline nodeSpec: NWrapper<N>.() -> Unit): Matcher<String> =
            object : Matcher<String> {
                override fun test(value: String): Result =
                        matchNode(ignoreChildren, nodeSpec).test(parseExpression<N>(value))

            }

    /**
     * Returns a String matcher that parses the node using [parseStatement] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     */
    inline fun <reified N : Node> matchStmt(ignoreChildren: Boolean = false,
                                            noinline nodeSpec: NWrapper<N>.() -> Unit) =
            object : Matcher<String> {

                override fun test(value: String): Result =
                        matchNode(ignoreChildren, nodeSpec).test(parseStatement<N>(value))

            }


    /**
     * Expect a parse exception to be thrown by [block].
     * The message is asserted to contain [messageContains].
     */
    fun expectParseException(messageContains: String, block: () -> Unit) {

        val thrown = shouldThrow<ParseException>(block)

        thrown.message.shouldContain(messageContains)

    }


    /**
     * Parse the string in an expression context. The parsed expression is the child of the
     * returned [ASTExpression]. Sometimes there may be many layers between the returned node
     * and the node of interest, e.g. when the expression is contained in a PrimaryExpression/PrimaryPrefix
     * construct, in which case [parseExpression] saves some keystrokes.
     *
     * @param expr The expression to parse
     *
     * @return An [ASTExpression] whose child is the given expression
     *
     * @throws ParseException If the argument is no valid expression for the given language version
     */
    fun parseAstExpression(expr: String): ASTExpression {

        val source = """
        ${imports.joinToString(separator = "\n")}
        class Foo {
            {
              Object o = $expr;
            }
        }
        """.trimIndent()

        val acu = ParserTstUtil.parseAndTypeResolveJava(javaVersion.pmdName, source)

        return acu.getFirstDescendantOfType(ASTVariableInitializer::class.java).getChild(0) as ASTExpression
    }


    /**
     * Parse the string in a statement context. The parsed statement is the child of the
     * returned [ASTBlockStatement]. Note that [parseStatement] can save you some keystrokes
     * because it finds a descendant of the wanted type.
     *
     * @param statement The statement to parse
     *
     * @return An [ASTBlockStatement] whose child is the given statement
     *
     * @throws ParseException If the argument is no valid statement for the given language version.
     *                        Don't forget the semicolon!
     */
    fun parseAstStatement(statement: String): ASTBlockStatement {

        // place the param in a statement parsing context
        val source = """
            ${imports.joinToString(separator = "\n")}
            class Foo {
               {
                 $statement
               }
            }
        """.trimIndent()

        val root = ParserTstUtil.parseAndTypeResolveJava(javaVersion.pmdName, source)

        return root.getFirstDescendantOfType(ASTBlockStatement::class.java)
    }


    /**
     * Parse the string in an expression context, and finds the first descendant of type [N].
     * The descendant is searched for by [findFirstNodeOnStraightLine], to prevent accidental
     * mis-selection of a node. In such a case, a [NoSuchElementException] is thrown, and you
     * should fix your test case.
     *
     * @param expr The expression to parse
     * @param N type of node to find
     *
     * @return The first descendant of type [N] found in the parsed expression
     *
     * @throws NoSuchElementException If no node of type [N] is found by [findFirstNodeOnStraightLine]
     * @throws ParseException If the expression is no valid expression for the given language version
     */
    inline fun <reified N : Node> parseExpression(expr: String): N =
            parseAstExpression(expr).findFirstNodeOnStraightLine(N::class.java)
            ?: throw NoSuchElementException("No node of type ${N::class.java.simpleName} in the given expression:\n\t$expr")


    /**
     * Parse the string in a statement context, and finds the first descendant of type [N].
     * The descendant is searched for by [findFirstNodeOnStraightLine], to prevent accidental
     * mis-selection of a node. In such a case, a [NoSuchElementException] is thrown, and you
     * should fix your test case.
     *
     * @param stmt The statement to parse
     * @param N The type of node to find
     *
     * @return The first descendant of type [N] found in the parsed expression
     *
     * @throws NoSuchElementException If no node of type [N] is found by [findFirstNodeOnStraightLine]
     * @throws ParseException If the argument is no valid statement for the given language version.
     *                        Don't forget the semicolon!
     */
    inline fun <reified N : Node> parseStatement(stmt: String): N =
            parseAstStatement(stmt).findFirstNodeOnStraightLine(N::class.java)
            ?: throw NoSuchElementException("No node of type ${N::class.java.simpleName} in the given statement:\n\t$stmt")


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
                @Suppress("UNCHECKED_CAST")
                val n = this as N
                n
            }
            this.numChildren == 1 -> getChild(0).findFirstNodeOnStraightLine(klass)
            else -> null
        }
    }
}


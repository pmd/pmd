package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.baseShouldMatchSubtree
import com.github.oowekyala.treeutils.printers.KotlintestBeanTreePrinter
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldThrow
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.*
import net.sourceforge.pmd.lang.java.ParserTstUtil
import java.beans.PropertyDescriptor
import io.kotlintest.should as kotlintestShould

/**
 * Represents the different Java language versions.
 */
enum class JavaVersion : Comparable<JavaVersion> {
    J1_3, J1_4, J1_5, J1_6, J1_7, J1_8, J9, J10, J11, J12;

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


object CustomTreePrinter : KotlintestBeanTreePrinter<Node>(NodeTreeLikeAdapter) {

    override fun takePropertyDescriptorIf(node: Node, prop: PropertyDescriptor): Boolean =
            when {
                prop.readMethod?.declaringClass !== node.javaClass -> false
                // avoid outputting too much, it's bad for readability
                node is ASTNumericLiteral -> when {
                    node.isIntLiteral || node.isLongLiteral -> prop.name == "valueAsInt"
                    else -> prop.name == "valueAsDouble"
                }
                else -> true
            }

    // dump the 'it::getName' instead of 'it.name' syntax

    override fun formatPropertyAssertion(expected: Any?, actualPropertyAccess: String): String? {
        val javaGetterName = convertKtPropAccessToGetterAccess(actualPropertyAccess)
        return super.formatPropertyAssertion(expected, "it::$javaGetterName")
    }

    override fun getContextAroundChildAssertion(node: Node, childIndex: Int, actualPropertyAccess: String): Pair<String, String> {
        val javaGetterName = convertKtPropAccessToGetterAccess(actualPropertyAccess)
        return super.getContextAroundChildAssertion(node, childIndex, "it::$javaGetterName")
    }

    private fun convertKtPropAccessToGetterAccess(ktPropAccess: String): String {
        val ktPropName = ktPropAccess.split('.')[1]

        return when {
            // boolean getter
            ktPropName matches Regex("is[A-Z].*") -> ktPropName
            else -> "get" + ktPropName.capitalize()
        }
    }

}


val JavaMatchingConfig = DefaultMatchingConfig.copy(
        errorPrinter = CustomTreePrinter
)

/** Java-specific matching method. */
inline fun <reified N : Node> JavaNode?.shouldMatchNode(ignoreChildren: Boolean = false, noinline nodeSpec: NodeSpec<N>) {
    this.baseShouldMatchSubtree(JavaMatchingConfig, ignoreChildren, nodeSpec)
}

/**
 * Extensible environment to describe parse/match testing workflows in a concise way.
 * Can be used inside of a [ParserTestSpec] with [ParserTestSpec.parserTest].
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
 * @property genClassHeader Header of the enclosing class used in parsing contexts like parseExpression, etc. E.g. "class Foo"
 */
open class ParserTestCtx(val javaVersion: JavaVersion = JavaVersion.Latest,
                         val importedTypes: MutableList<Class<*>> = mutableListOf(),
                         val otherImports: MutableList<String> = mutableListOf(),
                         var genClassHeader: String = "class Foo") {

    /** Imports to add to the top of the parsing contexts. */
    internal val imports: List<String>
        get() {
            val types = importedTypes.mapNotNull { it.canonicalName }.map { "import $it;" }
            return types + otherImports.map { "import $it;" }
        }

    inline fun <reified N : JavaNode> makeMatcher(nodeParsingCtx: NodeParsingCtx<*>, ignoreChildren: Boolean, noinline nodeSpec: NodeSpec<N>)
            : Assertions<String> = { nodeParsingCtx.parseAndFind<N>(it, this).shouldMatchNode(ignoreChildren, nodeSpec) }

    /**
     * Returns a String matcher that parses the node using [parseExpression] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     *
     */
    inline fun <reified N : ASTExpression> matchExpr(ignoreChildren: Boolean = false,
                                            noinline nodeSpec: NodeSpec<N>) =
            makeMatcher(ExpressionParsingCtx, ignoreChildren, nodeSpec)

    /**
     * Returns a String matcher that parses the node using [parseStatement] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     */
    inline fun <reified N : JavaNode> matchStmt(ignoreChildren: Boolean = false,
                                                noinline nodeSpec: NodeSpec<N>) =
            makeMatcher(StatementParsingCtx, ignoreChildren, nodeSpec)


    /**
     * Returns a String matcher that parses the node using [parseType] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     */
    inline fun <reified N : ASTType> matchType(ignoreChildren: Boolean = false,
                                            noinline nodeSpec: NodeSpec<N>) =
            makeMatcher(TypeParsingCtx, ignoreChildren, nodeSpec)

    /**
     * Returns a String matcher that parses the node using [parseToplevelDeclaration] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     */
    inline fun <reified N : ASTAnyTypeDeclaration> matchToplevelType(ignoreChildren: Boolean = false,
                                                                     noinline nodeSpec: NodeSpec<N>) =
            makeMatcher(TopLevelTypeDeclarationParsingCtx, ignoreChildren, nodeSpec)

    /**
     * Returns a String matcher that parses the node using [parseBodyDeclaration] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     *
     * Note that the enclosing type declaration can be customized by changing [genClassHeader].
     */
    inline fun <reified N : JavaNode> matchDeclaration(
            ignoreChildren: Boolean = false,
            noinline nodeSpec: NodeSpec<N>) = makeMatcher(EnclosedDeclarationParsingCtx, ignoreChildren, nodeSpec)

    fun notParseIn(nodeParsingCtx:NodeParsingCtx<*>): Assertions<String> = {
        shouldThrow<ParseException> {
            nodeParsingCtx.parseNode(it, this)
        }
    }

    /**
     * Expect a parse exception to be thrown by [block].
     * The message is asserted to contain [messageContains].
     */
    fun expectParseException(messageContains: String, block: () -> Unit) {

        val thrown = shouldThrow<ParseException>(block)

        thrown.message.shouldContain(messageContains)

    }


    fun parseAstExpression(expr: String): ASTExpression = ExpressionParsingCtx.parseNode(expr, this)

    fun parseAstStatement(statement: String): ASTBlockStatement = StatementParsingCtx.parseNode(statement, this)

    fun parseAstType(type: String): ASTType = TypeParsingCtx.parseNode(type, this)

    fun parseToplevelAnyTypeDeclaration(type: String): ASTAnyTypeDeclaration = TopLevelTypeDeclarationParsingCtx.parseNode(type, this)

    fun parseBodyDeclaration(type: String): ASTAnyTypeBodyDeclaration = EnclosedDeclarationParsingCtx.parseNode(type, this)

    // reified shorthands, fetching the node

    inline fun <reified N : Node> parseExpression(expr: String): N =
            ExpressionParsingCtx.parseAndFind(expr, this)

    // don't forget the semicolon
    inline fun <reified N : Node> parseStatement(stmt: String): N =
            StatementParsingCtx.parseAndFind(stmt, this)

    inline fun <reified N : Node> parseType(type: String): N =
            TypeParsingCtx.parseAndFind(type, this)

    inline fun <reified N : Node> parseToplevelDeclaration(decl: String): N =
            TopLevelTypeDeclarationParsingCtx.parseAndFind(decl, this)

    inline fun <reified N : Node> parseDeclaration(decl: String): N =
            EnclosedDeclarationParsingCtx.parseAndFind(decl, this)

    companion object {


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
                klass.isInstance(this) -> klass.cast(this)
                this.numChildren == 1 -> getChild(0).findFirstNodeOnStraightLine(klass)
                else -> null
            }
        }

        /**
         * Describes a kind of node that can be found commonly in the same contexts.
         * This type defines some machinery to parse a string to this kind of node
         * without much ado by placing it in a specific parsing context.
         */
        abstract class NodeParsingCtx<T : Node>(val constructName: String) {

            abstract fun getTemplate(construct: String, ctx: ParserTestCtx): String

            abstract fun retrieveNode(acu: ASTCompilationUnit): T

            /**
             * Parse the string in the context described by this object. The parsed node is usually
             * the child of the returned [T] node. Note that [parseAndFind] can save you some keystrokes
             * because it finds a descendant of the wanted type.
             *
             * @param construct The construct to parse
             *
             * @return A [T] whose child is the given statement
             *
             * @throws ParseException If the argument is no valid construct of this kind (mind the language version)
             */
            fun parseNode(construct: String, ctx: ParserTestCtx): T {
                val root = ParserTstUtil.parseAndTypeResolveJava(ctx.javaVersion.pmdName, getTemplate(construct, ctx))

                return retrieveNode(root)
            }

            /**
             * Parse the string the context described by this object, and finds the first descendant of type [N].
             * The descendant is searched for by [findFirstNodeOnStraightLine], to prevent accidental
             * mis-selection of a node. In such a case, a [NoSuchElementException] is thrown, and you
             * should fix your test case.
             *
             * @param construct The construct to parse
             * @param N The type of node to find
             *
             * @return The first descendant of type [N] found in the parsed expression
             *
             * @throws NoSuchElementException If no node of type [N] is found by [findFirstNodeOnStraightLine]
             * @throws ParseException If the argument is no valid construct of this kind
             *
             */
            inline fun <reified N : Node> parseAndFind(construct: String, ctx: ParserTestCtx): N =
                    parseNode(construct, ctx).findFirstNodeOnStraightLine(N::class.java)
                    ?: throw NoSuchElementException("No node of type ${N::class.java.simpleName} in the given $constructName:\n\t$construct")

        }


        object ExpressionParsingCtx : NodeParsingCtx<ASTExpression>("expression") {

            override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
                """
                ${ctx.imports.joinToString(separator = "\n")}
                ${ctx.genClassHeader} {
                    {
                        Object o = $construct;
                    }
                }
                """.trimIndent()


            override fun retrieveNode(acu: ASTCompilationUnit): ASTExpression = acu.getFirstDescendantOfType(ASTExpression::class.java)!!
        }

        object StatementParsingCtx : NodeParsingCtx<ASTBlockStatement>("statement") {

            override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
                """
                ${ctx.imports.joinToString(separator = "\n")}
                ${ctx.genClassHeader} {
                    {
                        $construct
                    }
                }
                """.trimIndent()


            override fun retrieveNode(acu: ASTCompilationUnit): ASTBlockStatement = acu.getFirstDescendantOfType(ASTBlockStatement::class.java)
        }

        object EnclosedDeclarationParsingCtx : NodeParsingCtx<ASTAnyTypeBodyDeclaration>("enclosed declaration") {

            override fun getTemplate(construct: String, ctx: ParserTestCtx): String = """
                ${ctx.imports.joinToString(separator = "\n")}
                ${ctx.genClassHeader} {
                    $construct
                }
                """.trimIndent()

            override fun retrieveNode(acu: ASTCompilationUnit): ASTAnyTypeBodyDeclaration =
                    acu.getFirstDescendantOfType(ASTAnyTypeBodyDeclaration::class.java)!!
        }

        object TopLevelTypeDeclarationParsingCtx : NodeParsingCtx<ASTAnyTypeDeclaration>("top-level declaration") {

            override fun getTemplate(construct: String, ctx: ParserTestCtx): String = """
            ${ctx.imports.joinToString(separator = "\n")}
            $construct
            """.trimIndent()

            override fun retrieveNode(acu: ASTCompilationUnit): ASTAnyTypeDeclaration = acu.getFirstDescendantOfType(ASTAnyTypeDeclaration::class.java)!!
        }

        object TypeParsingCtx : NodeParsingCtx<ASTType>("type") {
            override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
                """
                ${ctx.imports.joinToString(separator = "\n")}
                ${ctx.genClassHeader} {
                    $construct foo;
                }
                """.trimIndent()

            override fun retrieveNode(acu: ASTCompilationUnit): ASTType = acu.getFirstDescendantOfType(ASTType::class.java)
        }

    }
}


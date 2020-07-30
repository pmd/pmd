package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.baseShouldMatchSubtree
import com.github.oowekyala.treeutils.printers.KotlintestBeanTreePrinter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.string.shouldContain
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.ParseException
import net.sourceforge.pmd.lang.ast.test.*
import net.sourceforge.pmd.lang.ast.test.shouldMatchNode
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import java.beans.PropertyDescriptor

/**
 * Represents the different Java language versions.
 */
enum class JavaVersion : Comparable<JavaVersion> {
    J1_3, J1_4, J1_5, J1_6, J1_7, J1_8, J9, J10, J11,
    J12,
    J13, J13__PREVIEW,
    J14, J14__PREVIEW;

    /** Name suitable for use with e.g. [JavaParsingHelper.parse] */
    val pmdName: String = name.removePrefix("J").replaceFirst("__", "-").replace('_', '.').toLowerCase()

    val parser: JavaParsingHelper = JavaParsingHelper.WITH_PROCESSING.withDefaultVersion(pmdName)

    operator fun not(): List<JavaVersion> = values().toList() - this

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

// invariants that should be preserved always
private val javaImplicitAssertions: Assertions<Node> = {
    DefaultMatchingConfig.implicitAssertions(it)

}


val JavaMatchingConfig = DefaultMatchingConfig.copy(
        errorPrinter = CustomTreePrinter,
        implicitAssertions = javaImplicitAssertions
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
 * in instead of writing up a full class that the parser can handle. See [parseExpression],
 * [parseStatement].
 *
 * These are implicitly used by [matchExpr] and [matchStmt], which specify a matcher directly
 * on the strings, using their type parameter and the info in this test context to parse, find
 * the node, and execute the matcher in a single call. These may be used by [io.kotest.matchers.should],
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
 * Technically the utilities provided by this class may be used outside of [io.kotest.specs.FunSpec]s,
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
                         var packageName: String = "",
                         var genClassHeader: String = "class Foo") {

    var fullSource: String? = null

    /** Imports to add to the top of the parsing contexts. */
    internal val imports: List<String>
        get() {
            val types = importedTypes.mapNotNull { it.canonicalName }.map { "import $it;" }
            return types + otherImports.map { "import $it;" }
        }

    internal val packageDecl: String get() = if (packageName.isEmpty()) "" else "package $packageName;"

    /**
     * Places all node parsing contexts inside the declaration of the given class
     * of the given class.
     * It's like you were writing eg expressions inside the class, with the method
     * declarations around it and all.
     *
     * LIMITATIONS:
     * - does not work for [TopLevelTypeDeclarationParsingCtx]
     * - [klass] must be a toplevel class (not an enum, not an interface, not nested/local/anonymous)
     */
    fun asIfIn(klass: Class<*>) {
        assert(!klass.isArray && !klass.isPrimitive) {
            "$klass has no class name"
        }

        assert(!klass.isLocalClass
                && !klass.isAnonymousClass
                && klass.enclosingClass == null
                && !klass.isEnum
                && !klass.isInterface) {
            "Unsupported class $klass"
        }

        fullSource = javaVersion.parser.readClassSource(klass)
    }

    inline fun <reified N : JavaNode> makeMatcher(nodeParsingCtx: NodeParsingCtx<*>, ignoreChildren: Boolean, noinline nodeSpec: NodeSpec<N>)
            : Assertions<String> = { nodeParsingCtx.parseAndFind<N>(it, this).shouldMatchNode(ignoreChildren, nodeSpec) }

    /**
     * Returns a String matcher that parses the node using [parseExpression] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     *
     */
    inline fun <reified N : JavaNode> matchExpr(ignoreChildren: Boolean = false,
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
    inline fun <reified N : JavaNode> matchType(ignoreChildren: Boolean = false,
                                               noinline nodeSpec: NodeSpec<N>) =
            makeMatcher(TypeParsingCtx, ignoreChildren, nodeSpec)

    /**
     * Returns a String matcher that parses the node using [parseTypeParameters]
     * then matches it against the [nodeSpec] using [matchNode].
     */
    fun matchTypeParameters(ignoreChildren: Boolean = false,
                            nodeSpec: NodeSpec<ASTTypeParameters>) =
            makeMatcher(TypeParametersParsingCtx, ignoreChildren, nodeSpec)

    /**
     * Returns a String matcher that parses the node using [parseToplevelDeclaration] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     */
    inline fun <reified N : ASTAnyTypeDeclaration> matchToplevelType(ignoreChildren: Boolean = false,
                                                                     noinline nodeSpec: NodeSpec<N>) =
            makeMatcher(TopLevelTypeDeclarationParsingCtx, ignoreChildren, nodeSpec)

    /**
     * Returns a String matcher that parses the node using [parseDeclaration] with
     * type param [N], then matches it against the [nodeSpec] using [matchNode].
     *
     * Note that the enclosing type declaration can be customized by changing [genClassHeader].
     */
    inline fun <reified N : JavaNode> matchDeclaration(
            ignoreChildren: Boolean = false,
            noinline nodeSpec: NodeSpec<N>) = makeMatcher(EnclosedDeclarationParsingCtx, ignoreChildren, nodeSpec)

    fun notParseIn(nodeParsingCtx: NodeParsingCtx<*>, expected: (ParseException) -> Unit = {}): Assertions<String> = {
        val e = shouldThrow<ParseException> {
            nodeParsingCtx.parseNode(it, this)
        }
        expected(e)
    }

    fun parseIn(nodeParsingCtx: NodeParsingCtx<*>) = object : Matcher<String> {

        override fun test(value: String): Result {
            val (pass, e) = try {
                nodeParsingCtx.parseNode(value, this@ParserTestCtx)
                Pair(true, null)
            } catch (e: ParseException) {
                Pair(false, e)
            }

            return Result(pass,
                    "Expected '$value' to parse in $nodeParsingCtx, got $e",
                    "Expected '$value' not to parse in $nodeParsingCtx"
            )

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

    inline fun <reified N : ASTExpression> parseExpression(expr: String): N =
            ExpressionParsingCtx.parseAndFind(expr, this)

    // don't forget the semicolon
    inline fun <reified N : ASTStatement> parseStatement(stmt: String): N =
            StatementParsingCtx.parseAndFind(stmt, this)

    inline fun <reified N : ASTType> parseType(type: String): N =
            TypeParsingCtx.parseAndFind(type, this)

    inline fun parseTypeParameters(typeParams: String): ASTTypeParameters =
            TypeParametersParsingCtx.parseAndFind(typeParams, this)

    inline fun <reified N : Node> parseToplevelDeclaration(decl: String): N =
            TopLevelTypeDeclarationParsingCtx.parseAndFind(decl, this)

    inline fun <reified N : Node> parseDeclaration(decl: String): N =
            EnclosedDeclarationParsingCtx.parseNode(decl, this) as N

}


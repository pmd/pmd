/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.baseShouldMatchSubtree
import com.github.oowekyala.treeutils.printers.KotlintestBeanTreePrinter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestScope
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.collections.shouldContainAll
import net.sourceforge.pmd.lang.ast.*
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.JavaParsingHelper.*
import net.sourceforge.pmd.lang.test.ast.*
import java.beans.PropertyDescriptor
import java.io.PrintStream
import java.util.*

/**
 * Represents the different Java language versions.
 */
enum class JavaVersion : Comparable<JavaVersion> {
    J1_3, J1_4, J1_5, J1_6, J1_7, J1_8, J9, J10, J11,
    J12,
    J13,
    J14,
    J15,
    J16,
    J17,
    J18,
    J19,
    J20,
    J21, J21__PREVIEW,
    J22, J22__PREVIEW,
    J23, J23__PREVIEW;

    /** Name suitable for use with e.g. [JavaParsingHelper.parse] */
    val pmdName: String = name.removePrefix("J").replaceFirst("__", "-").replace('_', '.').lowercase()

    val parser: JavaParsingHelper = DEFAULT.withDefaultVersion(pmdName)

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

        fun since(v: JavaVersion) = v.rangeTo(Latest)

        fun except(v1: JavaVersion, vararg versions: JavaVersion) =
                values().toList() - v1 - versions.toSet()

        fun except(versions: List<JavaVersion>) = values().toList() - versions.toSet()
    }
}


object CustomTreePrinter : KotlintestBeanTreePrinter<Node>(NodeTreeLikeAdapter) {

    private val ignoredProps = setOf("scope")

    override fun takePropertyDescriptorIf(node: Node, prop: PropertyDescriptor): Boolean =
            when {
                prop.name in ignoredProps                          -> false
                prop.readMethod?.declaringClass !== node.javaClass -> false
                // avoid outputting too much, it's bad for readability
                node is ASTNumericLiteral                          -> when {
                    node.isIntegral -> prop.name == "valueAsInt"
                    else            -> prop.name == "valueAsDouble"
                }

                else                                               -> true
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
            else -> "get" + ktPropName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

}

// invariants that should be preserved always
private val javaImplicitAssertions: Assertions<Node> = {
    DefaultMatchingConfig.implicitAssertions(it)

    if (it is ASTExpression) run {
        it::isParenthesized shouldBe (it.parenthesisDepth > 0)
    }

    if (it is InternalInterfaces.AtLeastOneChild) {
        assert(it.numChildren > 0) {
            "Expected at least one child for $it"
        }
    }

    if (it is ModifierOwner) run {
        it.modifiers.effectiveModifiers.shouldContainAll(it.modifiers.explicitModifiers)
        it.modifiers.effectiveModifiers.shouldContainAtMostOneOf(JModifier.PUBLIC, JModifier.PRIVATE, JModifier.PROTECTED)
        it.modifiers.effectiveModifiers.shouldContainAtMostOneOf(JModifier.FINAL, JModifier.ABSTRACT)
        it.modifiers.effectiveModifiers.shouldContainAtMostOneOf(JModifier.DEFAULT, JModifier.ABSTRACT)
    }

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
 * in instead of writing up a full class that the parser can handle. See [ExpressionParsingCtx],
 * [StatementParsingCtx].
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
 * Technically the utilities provided by this class may be used outside of [io.kotest.core.spec.Spec]s,
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
open class ParserTestCtx(testScope: TestScope,
                         val javaVersion: JavaVersion = JavaVersion.Latest,
                         val importedTypes: MutableList<Class<*>> = mutableListOf(),
                         val otherImports: MutableList<String> = mutableListOf(),
                         var packageName: String = "",
                         var genClassHeader: String = "class Foo",
                         private var registeredTestCases: Int = 0): AbstractContainerScope(testScope) {

    var parser: JavaParsingHelper = javaVersion.parser.withProcessing(false)
        private set

    fun enableProcessing(logToConsole: Boolean = false): TestCheckLogger {
        val logger = TestCheckLogger(logToConsole)
        parser = parser.withProcessing(true).withLogger(logger)
        return logger
    }

    /**
     * Will throw on the first semantic error or warning.
     * Useful because it produces a stack trace for that warning/error.
     */
    fun assertNoSemanticErrorsOrWarnings() {
        parser = parser.withProcessing(true).withLogger(UnforgivingSemanticLogger.INSTANCE)
    }

    /** Returns a function that can retrieve the log*/
    fun logTypeInference(verbose: Boolean = false, to: PrintStream = System.err) {
        parser = parser.withProcessing(true).logTypeInference(verbose, to)
    }

    /** Populated after an [asIfIn] call, used by [TypeBodyParsingCtx]. */
    internal var fullSource: String? = null

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
     * It's like you were writing e.g. expressions inside the class, with the method
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

        fullSource = javaVersion.parser.withResourceContext(javaClass).readClassSource(klass)
    }


    fun notParseIn(nodeParsingCtx: NodeParsingCtx<*>, expected: (ParseException) -> Unit = {}): Assertions<String> = {
        val e = shouldThrow<ParseException> {
            nodeParsingCtx.parseNode(it, this)
        }
        expected(e)
    }

    fun parseIn(nodeParsingCtx: NodeParsingCtx<*>) = object : Matcher<String> {

        override fun test(value: String): MatcherResult {
            val (pass, e) = try {
                nodeParsingCtx.parseNode(value, this@ParserTestCtx)
                Pair(true, null)
            } catch (e: ParseException) {
                Pair(false, e)
            } catch (e: LexException) {
                Pair(false, e)
            }

            return MatcherResult(
                pass,
                { "Expected '$value' to parse in $nodeParsingCtx, got $e" },
                {
                    "Expected '$value' not to parse in ${nodeParsingCtx.toString().addArticle()}"
                }
            )
        }
    }

    override suspend fun registerTestCase(nested: NestedTest) {
        registeredTestCases++
        super.registerTestCase(nested)
    }

    fun hasMoreThanOneChild() : Boolean = registeredTestCases > 1
}

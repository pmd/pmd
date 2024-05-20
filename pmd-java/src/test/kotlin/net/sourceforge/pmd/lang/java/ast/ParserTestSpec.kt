/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.AbstractContainerScope
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addContainer
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.matchers.Matcher
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.ParseException
import net.sourceforge.pmd.lang.test.ast.Assertions
import net.sourceforge.pmd.lang.test.ast.IntelliMarker
import net.sourceforge.pmd.lang.test.ast.ValuedNodeSpec
import net.sourceforge.pmd.lang.test.ast.shouldMatchN
import net.sourceforge.pmd.lang.java.types.JTypeMirror
import net.sourceforge.pmd.lang.java.types.TypeDslMixin
import net.sourceforge.pmd.lang.java.types.TypeDslOf
import net.sourceforge.pmd.lang.java.types.shouldHaveType
import io.kotest.matchers.should as kotlintestShould

/**
 * Base class for grammar tests that use the DSL. Tests are layered into
 * containers that make it easier to browse in the IDE. Layout is group name,
 * then java version, then test case. Test cases are "should" assertions matching
 * a string against a matcher defined in [ParserTestCtx] or explicitly defined
 * test cases with "doTest".
 *
 * <p>If only one explicit test is written, then use "parserTest" as the grouping
 * and just put in assertions. A single test case will be defined implicitly then.
 *
 * <pre>{@code
 * class MyTest : ParserTestSpec({
 *     parserTestContainer("Simple additive expression should be flat") {
 *         inContext(ExpressionParsingCtx) {
 *             "1 + 2 + 3" should parseAs {
 *                 infixExpr(ADD) {
 *                     infixExpr(ADD) {
 *                         int(1)
 *                         int(2)
 *                     }
 *                     int(3)
 *                 }
 *             }
 *         }
 *         doTest("another test case") {
 *           //...
 *         }
 *     }
 *
 *    parserTest("Test annotations on module", javaVersions = since(J9)) {
 *         val root: ASTCompilationUnit = parser.withProcessing(true).parse("@A @a.B module foo { } ")
 *         root.moduleDeclaration.shouldMatchNode<ASTModuleDeclaration> {
 *             it.getAnnotation("A") shouldBe annotation("A")
 *             it.getAnnotation("a.B") shouldBe annotation("B")
 *             modName("foo")
 *         }
 *     }
 * }
 * }</pre>
 *
 * @author Clément Fournier
 */
abstract class ParserTestSpec(body: ParserTestSpec.() -> Unit) : DslDrivenSpec(), RootScope, IntelliMarker {

    init {
        body()
    }

    fun test(name: String, disabled: Boolean = false, test: suspend TestScope.() -> Unit) =
            addTest(
                testName = TestName(name),
                disabled = disabled,
                config = null,
                type = TestType.Test,
                test = test
            )

    /**
     * Defines a group of tests that should be named similarly,
     * with separate tests for separate versions.
     *
     * Calls to "should" in the block are intercepted to create
     * a new test, with the given [name] as a common prefix.
     *
     * This is useful to make a batch of grammar specs for grammar
     * regression tests without bothering to find a name.
     *
     * @param name Name of the container test
     * @param spec Assertions. Each call to [io.kotest.matchers.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     *
     */
    private fun parserTestGroup(name: String,
                        disabled: Boolean = false,
                        spec: suspend GroupTestCtx.() -> Unit) =
            addContainer(
                    testName = TestName(name),
                    test = { GroupTestCtx(this).spec() },
                    disabled = disabled,
                    config = null
            )

    fun parserTest(name: String,
                   javaVersion: JavaVersion = JavaVersion.Latest,
                   spec: suspend GroupTestCtx.VersionedTestCtx.() -> Unit) =
        parserTest(name, listOf(javaVersion), spec)

    /**
     * Defines a single test case, that is executed on several java versions
     * and grouped by the given [name].
     *
     * @param name Name of the container test
     * @param javaVersions Language versions fo which to generate tests
     * @param spec Assertions.
     */
    fun parserTest(name: String,
                   javaVersions: List<JavaVersion>,
                   spec: suspend GroupTestCtx.VersionedTestCtx.() -> Unit) =
        parserTestGroup(name) {
            onVersions(javaVersions) {
                doTest(name) {
                    spec()

                    if (this@onVersions.hasMoreThanOneChild()) {
                        throw IllegalStateException("Expected no child test cases, but parserTest '$name' has children. Use 'parserTestContainer' instead.")
                    }
                }
            }
        }

    fun parserTestContainer(name: String,
                            javaVersion: JavaVersion = JavaVersion.Latest,
                            spec: suspend GroupTestCtx.VersionedTestCtx.() -> Unit) =
        parserTestContainer(name, listOf(javaVersion), spec)

    /**
     * Defines a group of tests that should be named similarly,
     * executed on several java versions.
     * Calls to "should" in the block are intercepted to create a
     * new test, with the given [name] as a common prefix.
     *
     * Alternatively you can use "doTest" to define a new test case
     * explicitly.
     *
     * This is useful to make a batch of grammar specs for grammar
     * regression tests without bothering to find a name.
     *
     * @param name Name of the container test
     * @param javaVersions Language versions for which to generate tests
     * @param spec Assertions. Each call to [io.kotest.matchers.should] on a string
     *             receiver is replaced by a [GroupTestCtx.VersionedTestCtx.should], which creates a
     *             new parser test case. Alternatively use [GroupTestCtx.VersionedTestCtx.doTest] to create
     *             a new parser test case explicitly.
     */
    fun parserTestContainer(name: String,
                   javaVersions: List<JavaVersion>,
                   spec: suspend GroupTestCtx.VersionedTestCtx.() -> Unit) =
            parserTestGroup(name) {
                onVersions(javaVersions) {
                    spec()

                    if (!this@onVersions.hasChildren()) {
                        throw IllegalStateException("Expected at least one child test case, but parserTestContainer '$name' has no children. Use 'parserTest' instead or use 'should'/'doTest'.")
                    }
                }
            }

    private suspend fun containedParserTestImpl(
        testScope: GroupTestCtx.VersionedTestCtx,
        name: String,
        javaVersion: JavaVersion,
        assertions: suspend ParserTestCtx.() -> Unit) {

        val nested = NestedTest(
            name = TestName(name),
            test = { ParserTestCtx(testScope, javaVersion).apply { setup() }.assertions() },
            config = null,
            type = TestType.Test,
            disabled = false,
            source = sourceRef()
        )
        testScope.registerTestCase(nested)
    }

    /**
     * Setup to apply to spawned [ParserTestCtx]. By default, AST
     * processing is disabled beyond the parser.
     */
    protected open fun ParserTestCtx.setup() {

    }

    inner class GroupTestCtx(testScope: TestScope) : AbstractContainerScope(testScope) {

        suspend fun onVersions(javaVersions: List<JavaVersion>, spec: suspend VersionedTestCtx.() -> Unit) {
            javaVersions.forEach { javaVersion ->

                val nested = NestedTest(
                    name = TestName("Java ${javaVersion.pmdName}"),
                    test = { this@GroupTestCtx.VersionedTestCtx(this, javaVersion).apply { setup() }.spec() },
                    config = null,
                    type = TestType.Container,
                    disabled = false,
                    source = sourceRef()
                )
                this.registerTestCase(nested)
            }
        }

        inner class VersionedTestCtx(testScope: TestScope, javaVersion: JavaVersion) : ParserTestCtx(testScope, javaVersion) {

            suspend fun doTest(name : String = "[unnamed test case]", assertions: suspend VersionedTestCtx.() -> Unit) {
                containedParserTestImpl(this@VersionedTestCtx, name, javaVersion = javaVersion) {
                    this@VersionedTestCtx.assertions()
                }
            }

            suspend infix fun String.should(matcher: Assertions<String>) {
                containedParserTestImpl(this@VersionedTestCtx, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }

            suspend infix fun String.should(matcher: Matcher<String>) {
                containedParserTestImpl(this@VersionedTestCtx, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }

            suspend infix fun String.shouldNot(matcher: Matcher<String>) =
                    should(matcher.invert())

            suspend fun <T : Node> inContext(nodeParsingCtx: NodeParsingCtx<T>, assertions: suspend ImplicitNodeParsingCtx<T>.() -> Unit) {
                ImplicitNodeParsingCtx(nodeParsingCtx).assertions()
            }

            inner class ImplicitNodeParsingCtx<T : Node>(private val nodeParsingCtx: NodeParsingCtx<T>) {

                fun haveType(type: TypeDslMixin.() -> JTypeMirror): Assertions<String> = {

                    val node = doParse(it)
                    if (node is TypeNode) node shouldHaveType TypeDslOf(node.typeSystem).type()
                    else throw AssertionError("Not a TypeNode: $node")

                }


                fun doParse(s: String): T =
                        nodeParsingCtx.parseNode(s, this@VersionedTestCtx)

                /**
                 * A matcher that succeeds if the string parses correctly.
                 */
                fun parse(): Matcher<String> = this@VersionedTestCtx.parseIn(nodeParsingCtx)

                /**
                 * A matcher that succeeds if parsing throws a ParseException.
                 */
                fun throwParseException(expected: (ParseException) -> Unit = {}): Assertions<String> =
                        this@VersionedTestCtx.notParseIn(nodeParsingCtx, expected)


                fun parseAs(matcher: ValuedNodeSpec<Node, out Any>): Assertions<String> = { str ->
                    nodeParsingCtx.parseNode(str, this@VersionedTestCtx)
                            .shouldMatchN(matcher)
                }
            }
        }
    }
}

/**
 * A spec for which AST processing beyond the parser is enabled.
 */
abstract class ProcessorTestSpec(body: ParserTestSpec.() -> Unit) : ParserTestSpec(body) {
    override fun ParserTestCtx.setup() {
        enableProcessing(true)
    }
}

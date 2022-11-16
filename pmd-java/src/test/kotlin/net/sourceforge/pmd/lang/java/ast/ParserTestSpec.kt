/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.DslDrivenSpec
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addContainer
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType
import io.kotest.matchers.Matcher
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.ParseException
import net.sourceforge.pmd.lang.ast.test.Assertions
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.ValuedNodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldMatchN
import net.sourceforge.pmd.lang.java.types.JTypeMirror
import net.sourceforge.pmd.lang.java.types.TypeDslMixin
import net.sourceforge.pmd.lang.java.types.TypeDslOf
import net.sourceforge.pmd.lang.java.types.shouldHaveType
import io.kotest.matchers.should as kotlintestShould

/**
 * Base class for grammar tests that use the DSL. Tests are layered into
 * containers that make it easier to browse in the IDE. Layout is group name,
 * then java version, then test case. Test cases are "should" assertions matching
 * a string against a matcher defined in [ParserTestCtx].
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
    fun parserTestGroup(name: String,
                        disabled: Boolean = false,
                        spec: suspend GroupTestCtx.() -> Unit) =
            addContainer(
                    testName = TestName(name),
                    test = { GroupTestCtx(this).spec() },
                    disabled = disabled,
                    config = null
            )

    /**
     * Defines a group of tests that should be named similarly.
     * Calls to "should" in the block are intercepted to create
     * a new test, with the given [name] as a common prefix.
     *
     * This is useful to make a batch of grammar specs for grammar
     * regression tests without bothering to find a name.
     *
     * @param name Name of the container test
     * @param javaVersion Language versions to use when parsing
     * @param spec Assertions. Each call to [io.kotest.matchers.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     *
     */
    fun parserTest(name: String,
                   javaVersion: JavaVersion = JavaVersion.Latest,
                   spec: suspend GroupTestCtx.VersionedTestCtx.() -> Unit) =
            parserTest(name, listOf(javaVersion), spec)

    /**
     * Defines a group of tests that should be named similarly,
     * executed on several java versions. Calls to "should" in
     * the block are intercepted to create a new test, with the
     * given [name] as a common prefix.
     *
     * This is useful to make a batch of grammar specs for grammar
     * regression tests without bothering to find a name.
     *
     * @param name Name of the container test
     * @param javaVersions Language versions for which to generate tests
     * @param spec Assertions. Each call to [io.kotest.matchers.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     */
    fun parserTest(name: String,
                   javaVersions: List<JavaVersion>,
                   spec: suspend GroupTestCtx.VersionedTestCtx.() -> Unit) =
            parserTestGroup(name) {
                onVersions(javaVersions) {
                    spec()
                }
            }

    private suspend fun containedParserTestImpl(
            scope: TestScope,
            name: String,
            javaVersion: JavaVersion,
            assertions: suspend ParserTestCtx.() -> Unit) {

        val nested = NestedTest(
            name = TestName(name),
            test = { ParserTestCtx(javaVersion).apply { setup() }.assertions() },
            config = null,
            type = TestType.Test,
            disabled = false,
            source = sourceRef()
        )
        scope.registerTestCase(nested)
    }

    /**
     * Setup to apply to spawned [ParserTestCtx]. By default, AST
     * processing is disabled beyond the parser.
     */
    protected open fun ParserTestCtx.setup() {

    }

    inner class GroupTestCtx(private val scope: TestScope) {

        suspend fun onVersions(javaVersions: List<JavaVersion>, spec: suspend VersionedTestCtx.() -> Unit) {
            javaVersions.forEach { javaVersion ->

                val nested = NestedTest(
                    name = TestName("Java ${javaVersion.pmdName}"),
                    test = { VersionedTestCtx(this, javaVersion).apply { setup() }.spec() },
                    config = null,
                    type = TestType.Container,
                    disabled = false,
                    source = sourceRef()
                )
                scope.registerTestCase(nested)
            }
        }

        inner class VersionedTestCtx(private val scope: TestScope, javaVersion: JavaVersion) : ParserTestCtx(javaVersion) {

            suspend fun doTest(name: String, assertions: suspend VersionedTestCtx.() -> Unit) {
                containedParserTestImpl(scope, name, javaVersion = javaVersion) {
                    assertions()
                }
            }

            suspend infix fun String.should(matcher: Assertions<String>) {
                containedParserTestImpl(scope, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }

            suspend infix fun String.should(matcher: Matcher<String>) {
                containedParserTestImpl(scope, "'$this'", javaVersion = javaVersion) {
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

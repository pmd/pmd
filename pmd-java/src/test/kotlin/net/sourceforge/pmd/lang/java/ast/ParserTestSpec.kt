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
import net.sourceforge.pmd.lang.ast.test.Assertions
import net.sourceforge.pmd.lang.ast.test.IntelliMarker
import io.kotest.matchers.should as kotlintestShould

/**
 * Base class for grammar tests that use the DSL. Tests are layered into
 * containers that make it easier to browse in the IDE. Layout is group name,
 * then java version, then test case. Test cases are "should" assertions matching
 * a string against a matcher defined in [ParserTestCtx], e.g. [ParserTestCtx.matchExpr].
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
     * a new test.
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
            assertions: ParserTestCtx.() -> Unit) {

        val nested = NestedTest(
            name = TestName(name),
            test = { ParserTestCtx(javaVersion).assertions() },
            config = null,
            type = TestType.Test,
            disabled = false,
            source = sourceRef()
        )
        scope.registerTestCase(nested)
    }

    inner class GroupTestCtx(private val scope: TestScope) {

        suspend fun onVersions(javaVersions: List<JavaVersion>, spec: suspend VersionedTestCtx.() -> Unit) {
            javaVersions.forEach { javaVersion ->

                val nested = NestedTest(
                    name = TestName("Java ${javaVersion.pmdName}"),
                    test = { VersionedTestCtx(this, javaVersion).spec() },
                    config = null,
                    type = TestType.Container,
                    disabled = false,
                    source = sourceRef()
                )
                scope.registerTestCase(nested)
            }
        }

        inner class VersionedTestCtx(private val scope: TestScope, javaVersion: JavaVersion) : ParserTestCtx(javaVersion) {

            suspend infix fun String.should(matcher: Assertions<String>) {
                containedParserTestImpl(scope, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }
        }
    }
}

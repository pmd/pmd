package net.sourceforge.pmd.lang.java.ast

import io.kotest.core.config.Project
import io.kotest.core.spec.style.DslDrivenSpec
import io.kotest.core.spec.style.scopes.Lifecycle
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.RootTestRegistration
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestName
import io.kotest.core.test.TestType
import io.kotest.runner.junit.platform.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.Assertions
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

    override fun lifecycle(): Lifecycle = Lifecycle.from(this)
    override fun defaultConfig(): TestCaseConfig = actualDefaultConfig()
    override fun registration(): RootTestRegistration = RootTestRegistration.from(this)

    fun test(name: String, disabled: Boolean = false, test: suspend TestContext.() -> Unit) =
            registration().addTest(
                    name = TestName(name),
                    xdisabled = disabled,
                    test = test,
                    config = actualDefaultConfig()
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
            registration().addContainerTest(
                    name = TestName(name),
                    test = { GroupTestCtx(this).spec() },
                    xdisabled = disabled
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
            context: TestContext,
            name: String,
            javaVersion: JavaVersion,
            assertions: ParserTestCtx.() -> Unit) {

        context.registerTestCase(
                name = TestName(name),
                test = { ParserTestCtx(javaVersion).assertions() },
                config = actualDefaultConfig(),
                type = TestType.Test
        )
    }

    private fun actualDefaultConfig() =
            defaultTestConfig ?: defaultTestCaseConfig()
            ?: Project.testCaseConfig()

    inner class GroupTestCtx(private val context: TestContext) {

        suspend fun onVersions(javaVersions: List<JavaVersion>, spec: suspend VersionedTestCtx.() -> Unit) {
            javaVersions.forEach { javaVersion ->

                context.registerTestCase(
                        name = TestName("Java ${javaVersion.pmdName}"),
                        test = { VersionedTestCtx(this, javaVersion).spec() },
                        config = actualDefaultConfig(),
                        type = TestType.Container
                )
            }
        }

        inner class VersionedTestCtx(private val context: TestContext, javaVersion: JavaVersion) : ParserTestCtx(javaVersion) {

            suspend infix fun String.should(matcher: Assertions<String>) {
                containedParserTestImpl(context, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }
        }
    }
}

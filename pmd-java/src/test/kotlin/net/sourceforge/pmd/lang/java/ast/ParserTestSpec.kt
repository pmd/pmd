package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.AbstractSpec
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.specs.IntelliMarker
import net.sourceforge.pmd.lang.ast.test.Assertions
import io.kotlintest.should as kotlintestShould

/**
 * Base class for grammar tests that use the DSL. Tests are layered into
 * containers that make it easier to browse in the IDE. Layout is group name,
 * then java version, then test case. Test cases are "should" assertions matching
 * a string against a matcher defined in [ParserTestCtx], e.g. [ParserTestCtx.matchExpr].
 *
 * @author Clément Fournier
 */
abstract class ParserTestSpec(body: ParserTestSpec.() -> Unit) : AbstractSpec(), IntelliMarker {

    init {
        body()
    }

    fun test(name: String, test: TestContext.() -> Unit) =
            addTestCase(name, test, defaultTestCaseConfig, TestType.Test)

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
     * @param spec Assertions. Each call to [io.kotlintest.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     *
     */
    fun parserTestGroup(name: String,
                        spec: GroupTestCtx.() -> Unit) =
            addTestCase(name, { GroupTestCtx(this).spec() }, defaultTestCaseConfig, TestType.Container)

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
     * @param spec Assertions. Each call to [io.kotlintest.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     *
     */
    fun parserTest(name: String,
                   javaVersion: JavaVersion = JavaVersion.Latest,
                   spec: GroupTestCtx.VersionedTestCtx.() -> Unit) =
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
     * @param spec Assertions. Each call to [io.kotlintest.should] on a string
     *             receiver is replaced by a [GroupTestCtx.should], which creates a
     *             new parser test.
     */
    fun parserTest(name: String,
                   javaVersions: List<JavaVersion>,
                   spec: GroupTestCtx.VersionedTestCtx.() -> Unit) =
            parserTestGroup(name) {
                onVersions(javaVersions) {
                    spec()
                }
            }

    private fun containedParserTestImpl(
            context: TestContext,
            name: String,
            javaVersion: JavaVersion,
            assertions: ParserTestCtx.() -> Unit) {

        context.registerTestCase(
                name = name,
                spec = this,
                test = { ParserTestCtx(javaVersion).assertions() },
                config = defaultTestCaseConfig,
                type = TestType.Test
        )
    }

    inner class GroupTestCtx(private val context: TestContext) {

        fun onVersions(javaVersions: List<JavaVersion>, spec: VersionedTestCtx.() -> Unit) {
            javaVersions.forEach { javaVersion ->

                context.registerTestCase(
                        name = "Java ${javaVersion.pmdName}",
                        spec = this@ParserTestSpec,
                        test = { VersionedTestCtx(this, javaVersion).spec() },
                        config = defaultTestCaseConfig,
                        type = TestType.Container
                )
            }
        }

        inner class VersionedTestCtx(private val context: TestContext, javaVersion: JavaVersion) : ParserTestCtx(javaVersion) {

            infix fun String.should(matcher: Assertions<String>) {
                containedParserTestImpl(context, "'$this'", javaVersion = javaVersion) {
                    this@should kotlintestShould matcher
                }
            }
        }
    }
}
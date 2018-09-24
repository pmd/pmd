package net.sourceforge.pmd.lang.ast.test

import io.kotlintest.matchers.string.shouldContainIgnoringCase
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AbstractFunSpec


// Improve on the KotlinTest DSL for our specific needs
// a testing DSL testing a testing DSL!


fun AbstractFunSpec.failureTest(testName: String,
                                messageContains: Set<String> = emptySet(),
                                param: io.kotlintest.TestContext.() -> kotlin.Unit) {

    this.expectFailure<AssertionError>(testName, messageContains, param)
}

inline fun <reified T : Throwable> AbstractFunSpec.expectFailure(testName: String,
                                                                 messageContains: Set<String> = emptySet(),
                                                                 noinline param: io.kotlintest.TestContext.() -> kotlin.Unit) {
    test(testName) {
        val exception = shouldThrow<T> {
            this.param() // this is the test context here
        }

        for (substr in messageContains) exception.message.shouldContainIgnoringCase(substr)

    }
}

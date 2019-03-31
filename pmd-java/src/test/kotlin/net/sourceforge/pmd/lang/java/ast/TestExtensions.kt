package net.sourceforge.pmd.lang.java.ast

import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldMatch
import java.util.*
import kotlin.reflect.KCallable

infix fun <T, U : T> Optional<T>.shouldBePresent(any: U) {
    ::isPresent shouldBe true
    ::get shouldBe any
}

fun Optional<*>.shouldBeEmpty() {
    ::isPresent shouldBe false
}

fun KCallable<Optional<*>>.shouldBeEmpty() = this shouldMatch {
    ::isPresent shouldBe false
}

infix fun <T, U : T> KCallable<Optional<T>>.shouldBePresent(any: U) = this shouldMatch {
    ::isPresent shouldBe true
    ::get shouldBe any
}

fun String.addArticle() = when (this[0].toLowerCase()) {
    'a', 'e', 'i', 'o', 'u' -> "an $this"
    else -> "a $this"
}

fun TreeNodeWrapper<Node, *>.annotation(spec: TreeNodeWrapper<Node, ASTAnnotation>.() -> Unit={}) =
        child(ignoreChildren = false, nodeSpec = spec)


fun TreeNodeWrapper<Node, *>.variableId(name: String, otherAssertions: (ASTVariableDeclaratorId) -> Unit = {}) =
        child<ASTVariableDeclaratorId>(ignoreChildren = true) {
            it::getVariableName shouldBe name
            otherAssertions(it)
        }


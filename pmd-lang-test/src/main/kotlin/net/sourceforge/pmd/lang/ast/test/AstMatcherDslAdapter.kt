/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import com.github.oowekyala.treeutils.DoublyLinkedTreeLikeAdapter
import com.github.oowekyala.treeutils.matchers.MatchingConfig
import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import com.github.oowekyala.treeutils.matchers.baseShouldMatchSubtree
import com.github.oowekyala.treeutils.printers.KotlintestBeanTreePrinter
import net.sourceforge.pmd.lang.ast.Node
import io.kotlintest.should as ktShould

/** An adapter for [baseShouldMatchSubtree]. */
object NodeTreeLikeAdapter : DoublyLinkedTreeLikeAdapter<Node> {
    override fun getChildren(node: Node): List<Node> = node.findChildrenOfType(Node::class.java)

    override fun nodeName(type: Class<out Node>): String = type.simpleName.removePrefix("AST")

    override fun getParent(node: Node): Node? = node.parent

    override fun getChild(node: Node, index: Int): Node? = node.safeGetChild(index)
}

/** A [NodeSpec] that returns a value. */
typealias ValuedNodeSpec<I, O> = TreeNodeWrapper<Node, I>.() -> O

/** A subtree matcher written in the DSL documented on [TreeNodeWrapper]. */
typealias NodeSpec<N> = ValuedNodeSpec<N, Unit>

/** A function feedable to [io.kotest.matchers.should], which fails the test if an [AssertionError] is thrown. */
typealias Assertions<M> = (M) -> Unit

fun <N : Node> ValuedNodeSpec<N, *>.ignoreResult(): NodeSpec<N> {
    val me = this
    return { this.me() }
}

val DefaultMatchingConfig = MatchingConfig(
        adapter = NodeTreeLikeAdapter,
        errorPrinter = KotlintestBeanTreePrinter(NodeTreeLikeAdapter),
        implicitAssertions = { it.assertTextRangeIsOk() }
)

/** A shorthand for [baseShouldMatchSubtree] providing the [NodeTreeLikeAdapter]. */
inline fun <reified N : Node> Node?.shouldMatchNode(ignoreChildren: Boolean = false, noinline nodeSpec: ValuedNodeSpec<N, *>) {
    this.baseShouldMatchSubtree(DefaultMatchingConfig, ignoreChildren, nodeSpec.ignoreResult())
}

/**
 * Returns [an assertion function][Assertions] asserting that its parameter conforms to the given [NodeSpec].
 *
 * Use it with [io.kotest.matchers.should], e.g. `node should matchNode<ASTExpression> {}`.
 *
 * See also the samples on [TreeNodeWrapper].
 *
 * @param N Expected type of the node
 *
 * @param ignoreChildren If true, calls to [TreeNodeWrapper.child] in the [nodeSpec] are forbidden.
 *                       The number of children of the child is not asserted.
 *
 * @param nodeSpec Sequence of assertions to carry out on the node, which can be referred to by [TreeNodeWrapper.it].
 *                 Assertions may consist of [NWrapper.child] calls, which perform the same type of node
 *                 matching on a child of the tested node.
 *
 * @return A matcher for AST nodes, suitable for use by [io.kotest.matchers.should].
 */
inline fun <reified N : Node> matchNode(ignoreChildren: Boolean = false, noinline nodeSpec: ValuedNodeSpec<N, *>)
        : Assertions<Node?> = { it.shouldMatchNode(ignoreChildren, nodeSpec) }

/**
 * The spec applies to the parent, shifted so that [this] node
 * is the first node to be queried. This allows using sweeter
 * DSL constructs like in the Java module.
 */
fun Node.shouldMatchN(matcher: ValuedNodeSpec<Node, Any>) {
    val idx = indexInParent
    parent ktShould matchNode<Node> {
        if (idx > 0) {
            unspecifiedChildren(idx)
        }
        matcher()
        val left = it.numChildren - 1 - idx
        if (left > 0) {
            unspecifiedChildren(left)
        }
    }
}

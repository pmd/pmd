/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import com.github.oowekyala.treeutils.DoublyLinkedTreeLikeAdapter
import com.github.oowekyala.treeutils.TreeLikeAdapter
import com.github.oowekyala.treeutils.matchers.MatchingConfig
import com.github.oowekyala.treeutils.matchers.TreeNodeWrapper
import com.github.oowekyala.treeutils.matchers.baseShouldMatchSubtree
import com.github.oowekyala.treeutils.printers.KotlintestBeanTreePrinter
import net.sourceforge.pmd.lang.ast.Node

/** An adapter for [baseShouldMatchSubtree]. */
object NodeTreeLikeAdapter : DoublyLinkedTreeLikeAdapter<Node> {
    override fun getChildren(node: Node): List<Node> = node.findChildrenOfType(Node::class.java)

    override fun nodeName(type: Class<out Node>): String = type.simpleName.removePrefix("AST")

    override fun getParent(node: Node): Node? = node.parent

    override fun getChild(node: Node, index: Int): Node? = node.safeGetChild(index)
}

/** A subtree matcher written in the DSL documented on [TreeNodeWrapper]. */
typealias NodeSpec<N> = TreeNodeWrapper<Node, N>.() -> Unit

/** A function feedable to [io.kotlintest.should], which fails the test if an [AssertionError] is thrown. */
typealias Assertions<M> = (M) -> Unit

/** A shorthand for [baseShouldMatchSubtree] providing the [NodeTreeLikeAdapter]. */
inline fun <reified N : Node> Node?.shouldMatchNode(ignoreChildren: Boolean = false, noinline nodeSpec: NodeSpec<N>) {
    this.baseShouldMatchSubtree(MatchingConfig(adapter = NodeTreeLikeAdapter, errorPrinter = KotlintestBeanTreePrinter(NodeTreeLikeAdapter)), ignoreChildren, nodeSpec)
}

/**
 * Returns [an assertion function][Assertions] asserting that its parameter conforms to the given [NodeSpec].
 *
 * Use it with [io.kotlintest.should], e.g. `node should matchNode<ASTExpression> {}`.
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
 * @return A matcher for AST nodes, suitable for use by [io.kotlintest.should].
 */
inline fun <reified N : Node> matchNode(ignoreChildren: Boolean = false, noinline nodeSpec: NodeSpec<N>)
        : Assertions<Node?> = { it.shouldMatchNode(ignoreChildren, nodeSpec) }

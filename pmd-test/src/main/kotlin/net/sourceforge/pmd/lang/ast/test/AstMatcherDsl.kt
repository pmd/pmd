package net.sourceforge.pmd.lang.ast.test

import arrow.legacy.disjunctionTry
import io.kotlintest.Matcher
import io.kotlintest.Result
import net.sourceforge.pmd.lang.ast.Node
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Wraps a node, providing easy access to [it]. Additional matching
 * methods are provided to match children.
 *
 * @property it Wrapped node
 * @param <N> Type of the node
 */
class NWrapper<N : Node> private constructor(val it: N, private val childMatchersAreIgnored: Boolean) {

    /** Index to which the next child matcher will apply. */
    private var nextChildMatcherIdx = 0

    private fun shiftChild(num: Int = 1): Node {

        checkChildExists(nextChildMatcherIdx)

        val ret = it.jjtGetChild(nextChildMatcherIdx)

        nextChildMatcherIdx += num
        return ret
    }


    private fun checkChildExists(childIdx: Int) =
            assertTrue("Node has fewer children than expected, child #$childIdx doesn't exist") {
                childIdx < it.numChildren
            }


    /**
     * Specify that the next [num] children will only be tested for existence,
     * but not for type, or anything else.
     */
    fun unspecifiedChildren(num: Int) {
        shiftChild(num)
        // Checks that the last child mentioned exists
        checkChildExists(nextChildMatcherIdx - 1)
    }


    /**
     * Specify that the next child will only be tested for existence,
     * but not for type, or anything else.
     */
    fun unspecifiedChild() = unspecifiedChildren(1)


    /**
     * Specify that the next child will be tested against the assertions
     * defined by the lambda.
     *
     * This method asserts that the child exists, and that it is of the
     * required type [M]. The lambda is then executed on it. Subsequent
     * calls to this method at the same tree level will test the next
     * children.
     *
     * @param ignoreChildren If true, calls to [child] in the [nodeSpec] are ignored.
     *                       The number of children of the child is not asserted either.
     * @param nodeSpec Sequence of assertions to carry out on the child node
     *
     * @param M Expected type of the child
     */
    inline fun <reified M : Node> child(ignoreChildren: Boolean = false, noinline nodeSpec: NWrapper<M>.() -> Unit) =
            childImpl(ignoreChildren, M::class.java, nodeSpec)


    @PublishedApi
    internal fun <M : Node> childImpl(ignoreChildren: Boolean, childType: Class<M>, nodeSpec: NWrapper<M>.() -> Unit) {
        if (!childMatchersAreIgnored) executeWrapper(childType, shiftChild(), ignoreChildren, nodeSpec)
    }


    override fun toString(): String {
        return "NWrapper<${it.xPathNodeName}>"
    }


    companion object {

        internal val Node.numChildren: Int
            get() = this.jjtGetNumChildren()


        private val <M : Node> Class<M>.nodeName
            get() =
                if (simpleName.startsWith("AST", ignoreCase = false))
                    simpleName.substring("AST".length)
                else simpleName

        /**
         * Execute wrapper assertions on a node.
         *
         * @param childType Expected type of [toWrap]
         * @param toWrap Node on which to execute the assertions
         * @param ignoreChildrenMatchers Ignore the children matchers in [spec]
         * @param spec Assertions to carry out on [toWrap]
         *
         * @throws AssertionError If some assertions fail
         */
        @PublishedApi
        internal fun <M : Node> executeWrapper(childType: Class<M>, toWrap: Node, ignoreChildrenMatchers: Boolean, spec: NWrapper<M>.() -> Unit) {

            assertTrue("Expected node to have type ${childType.nodeName}, actual ${toWrap.javaClass.nodeName}") {
                childType.isInstance(toWrap)
            }

            @Suppress("UNCHECKED_CAST")
            val wrapper = NWrapper(toWrap as M, ignoreChildrenMatchers)

            wrapper.spec()

            assertFalse("<${childType.nodeName}>: Wrong number of children, expected ${wrapper.nextChildMatcherIdx}, actual ${wrapper.it.numChildren}") {
                !ignoreChildrenMatchers && wrapper.nextChildMatcherIdx != wrapper.it.numChildren
            }
        }
    }
}


/**
 * Matcher for a node, using [NWrapper] to specify a subtree against which
 * the tested node will be tested.
 *
 * Use it with [io.kotlintest.should], e.g. `nodeshould matchNode<ASTExpression> {}`.
 *
 * @param N Expected type of the node
 *
 * @param ignoreChildren If true, calls to [NWrapper.child] in the [nodeSpec] are ignored.
 *                       The number of children of the child is not asserted either.
 *
 * @param nodeSpec Sequence of assertions to carry out on the node, which can be referred to by [NWrapper.it].
 *                 Assertions may onsist of [NWrapper.child] calls, which perform the same type of node
 *                 matching on a child of the tested node.
 *
 * @return A matcher for AST nodes, suitable for use by [io.kotlintest.should].
 *
 * ### Samples
 *
 *    node should matchNode<ASTStatement> {
 *
 *        // nesting matchers allow to specify a whole subtree
 *        child<ASTForStatement> {
 *
 *            // This would fail if the first child of the ForStatement wasn't a ForInit
 *            child<ASTForInit> {
 *                child<ASTLocalVariableDeclaration> {
 *
 *                    // If the parameter ignoreChildren is set to true, the number of children is not asserted
 *                    // Calls to "child" in the block are completely ignored
 *                    // The only checks carried out here are the type test and the assertions of the block
 *                    child<ASTType>(ignoreChildren = true) {
 *
 *                        // In a "child" block, the tested node can be referred to as "it"
 *                        // Here, its static type is ASTType, so we can inspect properties
 *                        // of the node and make assertions
 *
 *                        it.typeImage shouldBe "int"
 *                        it.type shouldNotBe null
 *                    }
 *
 *                    // We don't care about that node, we only care that there is "some" node
 *                    unspecifiedChild()
 *                }
 *            }
 *
 *            // The subtree is ignored, but we check a ForUpdate is present at this child position
 *            child<ASTForUpdate>(ignoreChildren = true) {}
 *
 *            // Here, ignoreChildren is not specified and takes its default value of false.
 *            // The lambda has no "child" calls and the node will be asserted to have no children
 *            child<ASTBlock> {}
 *        }
 *    }
 */
inline fun <reified N : Node> matchNode(ignoreChildren: Boolean = false, noinline nodeSpec: NWrapper<N>.() -> Unit) = object : Matcher<Node?> {
    override fun test(value: Node?): Result {
        if (value == null) {
            return Result(false, "Expecting the node not to be null", "")
        }

        val matchRes = disjunctionTry {
            NWrapper.executeWrapper(N::class.java, value, ignoreChildren, nodeSpec)
        }

        val didMatch = matchRes.isRight()


        // Output when the node should have matched and did not
        //
        val failureMessage: String = matchRes.fold({
            // Here the node failed
            it.message ?: "The node did not match the pattern (no cause specified)"
        }, {
            // The node matched, which was expected
            "SHOULD NOT BE OUTPUT"
        })

        val negatedMessage = matchRes.fold({
            // the node didn't match, which was expected
            "SHOULD NOT BE OUTPUT"
        }, {
            "The node should not have matched this pattern"
        })


        return Result(didMatch, failureMessage, negatedMessage)
    }
}

// This one preserves the stack trace
// It's still hard to read because of the inlines, and possibly only IntelliJ knows how to do that
// I'll try to get kotlintest to preserve the original stack trace

//inline fun <reified M : Node> Node.shouldMatchNode(ignoreChildren: Boolean = false, noinline nodeSpec: NWrapper<M>.() -> Unit) {
//    NWrapper.executeWrapper(M::class.java, this, ignoreChildren, nodeSpec)
//}
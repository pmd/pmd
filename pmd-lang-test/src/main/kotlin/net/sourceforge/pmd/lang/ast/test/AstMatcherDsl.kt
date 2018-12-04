package net.sourceforge.pmd.lang.ast.test

import arrow.core.Either
import io.kotlintest.Matcher
import io.kotlintest.Result
import net.sourceforge.pmd.lang.ast.Node
import kotlin.test.assertFalse
import kotlin.test.assertTrue


/**
 * Wraps a node, providing easy access to [it]. Additional matching
 * methods are provided to match children.
 *
 * @param matcherPath List of types of the parents of this node, used to reconstruct a path for error messages
 * @param childMatchersAreIgnored Ignore calls to [child]
 *
 * @property it Wrapped node
 * @param <N> Type of the node
 */
class NWrapper<N : Node> private constructor(val it: N,
                                             private val matcherPath: List<Class<out Node>>,
                                             private val childMatchersAreIgnored: Boolean) {

    /** Index to which the next child matcher will apply. */
    private var nextChildMatcherIdx = 0

    private fun shiftChild(num: Int = 1): Node {

        checkChildExists(nextChildMatcherIdx)

        val ret = it.jjtGetChild(nextChildMatcherIdx)

        nextChildMatcherIdx += num
        return ret
    }


    private fun checkChildExists(childIdx: Int) =
            assertTrue(formatErrorMessage(matcherPath, "Node has fewer children than expected, child #$childIdx doesn't exist")) {
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
     * @param ignoreChildren If true, the number of children of the child is not asserted.
     *                       Calls to [child] in the [nodeSpec] throw an exception.
     * @param nodeSpec Sequence of assertions to carry out on the child node
     *
     * @param M Expected type of the child
     *
     * @throws AssertionError If the child is not of type [M], or fails the assertions of the [nodeSpec]
     * @return The child, if it passes all assertions, otherwise throws an exception
     */
    inline fun <reified M : Node> child(ignoreChildren: Boolean = false, noinline nodeSpec: NWrapper<M>.() -> Unit): M =
            childImpl(ignoreChildren, M::class.java) { nodeSpec(); it }

    /**
     * Specify that the next child will be tested against the assertions
     * defined by the lambda, and returns the return value of the lambda.
     *
     * This method asserts that the child exists, and that it is of the
     * required type [M]. The lambda is then executed on it. Subsequent
     * calls to this method at the same tree level will test the next
     * children.
     *
     * @param ignoreChildren If true, the number of children of the child is not asserted.
     *                       Calls to [child] in the [nodeSpec] throw an exception.
     * @param nodeSpec Sequence of assertions to carry out on the child node
     *
     * @param M Expected type of the child
     * @param R Return type of the call
     *
     * @throws AssertionError If the child is not of type [M], or fails the assertions of the [nodeSpec]
     * @return The return value of the lambda
     */
    inline fun <reified M : Node, R> childRet(ignoreChildren: Boolean = false, noinline nodeSpec: NWrapper<M>.() -> R): R =
            childImpl(ignoreChildren, M::class.java, nodeSpec)


    @PublishedApi
    internal fun <M : Node, R> childImpl(ignoreChildren: Boolean, childType: Class<M>, nodeSpec: NWrapper<M>.() -> R): R {
        if (!childMatchersAreIgnored)
            return executeWrapper(childType, shiftChild(), matcherPath, ignoreChildren, nodeSpec)
        else
            throw IllegalStateException(formatErrorMessage(matcherPath, "Calling child when ignoreChildren=true is forbidden"))
    }


    override fun toString(): String {
        return "NWrapper<${it.xPathNodeName}>"
    }


    companion object {

        private val <M : Node> Class<M>.nodeName
            get() =
                if (simpleName.startsWith("AST", ignoreCase = false))
                    simpleName.substring("AST".length)
                else simpleName

        private fun formatPath(matcherPath: List<Class<out Node>>) =
                when {
                    matcherPath.isEmpty() -> "<root>"
                    else -> matcherPath.joinToString(separator = "/", prefix = "/") { it.nodeName }
                }

        private fun formatErrorMessage(matcherPath: List<Class<out Node>>, message: String) =
                "At ${formatPath(matcherPath)}: $message"

        /**
         * Execute wrapper assertions on a node.
         *
         * @param childType Expected type of [toWrap]
         * @param toWrap Node on which to execute the assertions
         * @param matcherPath List of types of the parents of this node, used to reconstruct a path for error messages
         * @param ignoreChildrenMatchers Ignore the children matchers in [spec]
         * @param spec Assertions to carry out on [toWrap]
         *
         * @param M Expected type of [toWrap]
         * @param R Return type
         *
         * @throws AssertionError If some assertions fail
         * @return [toWrap], if it passes all assertions, otherwise throws an exception
         */
        @PublishedApi
        internal fun <M : Node, R> executeWrapper(childType: Class<M>,
                                                  toWrap: Node,
                                                  matcherPath: List<Class<out Node>>,
                                                  ignoreChildrenMatchers: Boolean,
                                                  spec: NWrapper<M>.() -> R): R {

            val nodeNameForMsg = when {
                matcherPath.isEmpty() -> "node"
                else -> "child #${toWrap.jjtGetChildIndex()}"
            }

            assertTrue(formatErrorMessage(matcherPath, "Expected $nodeNameForMsg to have type ${childType.nodeName}, actual ${toWrap.javaClass.nodeName}")) {
                childType.isInstance(toWrap)
            }

            val childPath = matcherPath + childType
            @Suppress("UNCHECKED_CAST")
            val m = toWrap as M

            val wrapper = NWrapper(m, childPath, ignoreChildrenMatchers)

            val ret: R = try {
                wrapper.spec()
            } catch (e: AssertionError) {
                if (e.message?.matches("At (/.*?|<root>):.*".toRegex()) == false) {
                    // the exception has no path, let's add one
                    throw AssertionError(formatErrorMessage(childPath, e.message ?: "No explanation provided"), e)
                }
                throw e
            }

            assertFalse(formatErrorMessage(childPath, "Wrong number of children, expected ${wrapper.nextChildMatcherIdx}, actual ${wrapper.it.numChildren}")) {
                !ignoreChildrenMatchers && wrapper.nextChildMatcherIdx != wrapper.it.numChildren
            }
            return ret
        }
    }
}


/**
 * Matcher for a node, using [NWrapper] to specify a subtree against which
 * the tested node will be tested.
 *
 * Use it with [io.kotlintest.should], e.g. `node should matchNode<ASTExpression> {}`.
 *
 * @param N Expected type of the node
 *
 * @param ignoreChildren If true, calls to [NWrapper.child] in the [nodeSpec] are forbidden.
 *                       The number of children of the child is not asserted.
 *
 * @param nodeSpec Sequence of assertions to carry out on the node, which can be referred to by [NWrapper.it].
 *                 Assertions may consist of [NWrapper.child] calls, which perform the same type of node
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
 *                    // Calls to "child" in the block are forbidden
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
 *
 *    // To get good error messages, it's important to define assertions
 *    // on the node that is supposed to verify them, so if it needs some
 *    // value from its children, you can go fetch that value in two ways:
 *    // * if you just need the child node, the child method already returns that
 *    // * if you need some more complex value, or to return some subchild, use childRet
 *
 *    catchStmt should matchStmt<ASTCatchStatement> {
 *       it.isMulticatchStatement shouldBe true
 *
 *       // The childRet method is a variant of child which can return anything.
 *       // Specify the return type as a type parameter
 *       val types = childRet<ASTFormalParameter, List<ASTType>> {
 *
 *           // The child method returns the child (strongly typed)
 *           val ioe = child<ASTType>(ignoreChildren = true) {
 *               it.type shouldBe IOException::class.java
 *           }
 *
 *           val aerr = child<ASTType>(ignoreChildren = true) {
 *               it.type shouldBe java.lang.AssertionError::class.java
 *           }
 *
 *           unspecifiedChild()
 *
 *           // You have to use the annotated return type syntax
 *           return@childRet listOf(ioe, aerr)
 *       }
 *
 *       // Here you can use the returned value to perform more assertions*
 *
 *       it.caughtExceptionTypeNodes.shouldContainExactly(types)
 *       it.caughtExceptionTypes.shouldContainExactly(types.map { it.type })
 *
 *       it.exceptionName shouldBe "e"
 *
 *       child<ASTBlock> { }
 *    }
 */
inline fun <reified N : Node> matchNode(ignoreChildren: Boolean = false, noinline nodeSpec: NWrapper<N>.() -> Unit) = object : Matcher<Node?> {
    override fun test(value: Node?): Result {
        if (value == null) {
            return Result(false, "Expecting the node not to be null", "")
        }

        val matchRes = try {
            Either.Right(NWrapper.executeWrapper(N::class.java, value, emptyList(), ignoreChildren, nodeSpec))
        } catch (e: AssertionError) {
            Either.Left(e)
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
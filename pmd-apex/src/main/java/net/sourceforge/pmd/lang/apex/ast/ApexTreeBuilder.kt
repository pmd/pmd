/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast

import net.sourceforge.pmd.annotation.InternalApi
import net.sourceforge.pmd.lang.apex.ApexParserOptions
import net.sourceforge.pmd.lang.ast.ParseException
import net.sourceforge.pmd.lang.ast.SourceCodePositioner

import com.google.summit.ast.CompilationUnit
import com.google.summit.ast.Identifier
import com.google.summit.ast.Node
import com.google.summit.ast.TypeRef
import com.google.summit.ast.declaration.ClassDeclaration
import com.google.summit.ast.declaration.EnumDeclaration
import com.google.summit.ast.declaration.InterfaceDeclaration
import com.google.summit.ast.declaration.MethodDeclaration
import com.google.summit.ast.declaration.TriggerDeclaration
import com.google.summit.ast.declaration.TypeDeclaration
import com.google.summit.ast.modifier.KeywordModifier
import com.google.summit.ast.modifier.KeywordModifier.Keyword
import com.google.summit.ast.modifier.Modifier

@Deprecated("internal")
@InternalApi
@Suppress("DEPRECATION")
class ApexTreeBuilder(val sourceCode: String, val parserOptions: ApexParserOptions) {
    private val sourceCodePositioner = SourceCodePositioner(sourceCode)

    /** Builds and returns an [ApexNode] AST corresponding to the given [root] node. */
    fun buildTree(root: CompilationUnit): ApexRootNode<TypeDeclaration> {
        // Build tree
        val result =
            build(root, parent = null) as? ApexRootNode<TypeDeclaration>
                ?: throw ParseException("Unable to build tree")

        // Call additional methods
        callAdditional(result)

        return result
    }

    /** Calls additional methods for each node in [root] using a post-order traversal. */
    private fun callAdditional(root: ApexNode<*>) =
        root.jjtAccept(
            object : ApexParserVisitorAdapter() {
                override fun visit(node: ApexNode<*>?, data: Any?): Any? =
                    super.visit(node, data).also {
                        if (node is AbstractApexNode) {
                            node.handleSourceCode(sourceCode)
                            node.calculateLineNumbers(sourceCodePositioner)
                        }
                    }
            },
            null
        )

    /** Builds an [ApexNode] wrapper for [node]. */
    private fun build(node: Node?, parent: ApexNode<*>?): ApexNode<*>? =
        when (node) {
            null -> null
            is CompilationUnit -> build(node.typeDeclaration, parent)
            is TypeDeclaration -> buildTypeDeclaration(node)
            is MethodDeclaration -> buildMethodDeclaration(node, parent)
            is Identifier,
            is KeywordModifier,
            is TypeRef -> null
            else -> {
                println("No adapter exists for type ${node::class.qualifiedName}")
                // TODO(b/239648780): temporary print
                null
            }
        }

    /** Builds an [ApexNode] wrapper for [node] and sets its parent to [parent]. */
    private fun buildAndSetParent(node: Node, parent: ApexNode<*>) =
        build(node, parent)?.also { it.setParent(parent) }

    /**
     * Builds an [ApexNode] wrapper for each [child][Node.getChildren] of [node] and sets its parent
     * to [parent].
     *
     * If [exclude] is provided, child nodes matching this predicate are not visited.
     */
    private fun buildChildren(
        node: Node,
        parent: ApexNode<*>,
        exclude: (Node) -> Boolean = { false } // exclude none by default
    ) = node.getChildren().filterNot(exclude).forEach { buildAndSetParent(it, parent) }

    /** Builds an [ApexRootNode] wrapper for the [TypeDeclaration] node. */
    private fun buildTypeDeclaration(node: TypeDeclaration) =
        when (node) {
            is ClassDeclaration ->
                ASTUserClass(node).apply {
                    buildModifiers(node.modifiers).also { it.setParent(this) }
                    buildChildren(node, parent = this, exclude = { it in node.modifiers })
                }
            is InterfaceDeclaration ->
                ASTUserInterface(node).apply {
                    buildModifiers(node.modifiers).also { it.setParent(this) }
                    buildChildren(node, parent = this, exclude = { it in node.modifiers })
                }
            is EnumDeclaration -> ASTUserEnum(node) // TODO(b/239648780): enum body is untranslated
            is TriggerDeclaration -> ASTUserTrigger(node) // TODO(b/239648780): visit children
        }

    /** Builds an [ASTMethod] wrapper for the [MethodDeclaration] node. */
    private fun buildMethodDeclaration(node: MethodDeclaration, parent: ApexNode<*>?) =
        when {
            node.isAnonymousInitializationCode() && !node.hasKeyword(Keyword.STATIC) ->
                build(node.body, parent)
            else -> {
                ASTMethod(node).apply {
                    buildModifiers(node.modifiers).also { it.setParent(this) }
                    buildChildren(node, parent = this, exclude = { it in node.modifiers })
                }
            }
        }

    /** Builds an [ASTModifierNode] wrapper for the list of [Modifier]s. */
    private fun buildModifiers(modifiers: List<Modifier>) =
        ASTModifierNode(modifiers).apply { modifiers.forEach { buildAndSetParent(it, parent = this) } }

    /**
     * If [parent] is not null, adds this [ApexNode] as a [child][ApexNode.jjtAddChild] and sets
     * [parent] as the [parent][ApexNode.jjtSetParent].
     */
    private fun ApexNode<*>.setParent(parent: ApexNode<*>?) {
        if (parent != null) {
            parent.jjtAddChild(this, parent.numChildren)
            this.jjtSetParent(parent)
        }
    }

    val suppressMap
        get() = emptyMap<Int, String>()
    // TODO(b/239648780)
}

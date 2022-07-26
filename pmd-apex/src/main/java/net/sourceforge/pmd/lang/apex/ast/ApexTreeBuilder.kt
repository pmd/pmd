/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast

import net.sourceforge.pmd.annotation.InternalApi
import net.sourceforge.pmd.lang.apex.ApexParserOptions
import net.sourceforge.pmd.lang.ast.ParseException
import net.sourceforge.pmd.lang.ast.SourceCodePositioner

import com.google.summit.ast.CompilationUnit
import com.google.summit.ast.Node
import com.google.summit.ast.declaration.ClassDeclaration
import com.google.summit.ast.declaration.EnumDeclaration
import com.google.summit.ast.declaration.InterfaceDeclaration
import com.google.summit.ast.declaration.TriggerDeclaration
import com.google.summit.ast.declaration.TypeDeclaration

@Deprecated("internal")
@InternalApi
@Suppress("DEPRECATION")
class ApexTreeBuilder(val sourceCode: String, val parserOptions: ApexParserOptions) {
    private val sourceCodePositioner = SourceCodePositioner(sourceCode)

    /** Builds and returns an [ApexNode] AST corresponding to the given [root] node. */
    fun buildTree(root: CompilationUnit): ApexRootNode<TypeDeclaration> =
        build(root, parent = null) as? ApexRootNode<TypeDeclaration>
            ?: throw ParseException("Unable to build tree")

    /**
     * Builds an [ApexNode] wrapper for [node].
     *
     * Sets the parent of the resulting [ApexNode] to [parent], if it's not `null`.
     */
    private fun build(node: Node?, parent: ApexNode<*>?): AbstractApexNode? {
        val wrapper: AbstractApexNode? =
            when (node) {
                null -> null
                is CompilationUnit -> build(node.typeDeclaration, parent)
                is TypeDeclaration -> buildTypeDeclaration(node)
                else -> {
                    println("No adapter exists for type ${node::class.qualifiedName}")
                    // TODO(b/239648780): temporary print
                    null
                }
            }

        wrapper?.setParent(parent)
        wrapper?.handleSourceCode(sourceCode)
        wrapper?.calculateLineNumbers(sourceCodePositioner)
        return wrapper
    }

    /** Builds an [ApexRootNode] wrapper for the [TypeDeclaration] node. */
    private fun buildTypeDeclaration(node: TypeDeclaration) =
        when (node) {
            is ClassDeclaration -> ASTUserClass(node)
            is InterfaceDeclaration -> ASTUserInterface(node)
            is EnumDeclaration -> ASTUserEnum(node)
            is TriggerDeclaration -> ASTUserTrigger(node)
        }

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

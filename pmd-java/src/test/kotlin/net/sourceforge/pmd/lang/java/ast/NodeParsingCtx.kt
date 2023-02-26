/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.java.JavaParsingHelper

/**
 * Describes a kind of node that can be found commonly in the same contexts.
 * This type defines some machinery to parse a string to this kind of node
 * without much ado by placing it in a specific parsing context.
 */
abstract class NodeParsingCtx<T : Node>(val constructName: String) {

    abstract fun getTemplate(construct: String, ctx: ParserTestCtx): String

    abstract fun retrieveNode(acu: ASTCompilationUnit): T

    /**
     * Parse the string in the context described by this object. The parsed node is usually
     * the child of the returned [T] node. Note that [parseAndFind] can save you some keystrokes
     * because it finds a descendant of the wanted type.
     *
     * @param construct The construct to parse
     *
     * @return A [T] whose child is the given statement
     *
     * @throws ParseException If the argument is no valid construct of this kind (mind the language version)
     */
    fun parseNode(construct: String, ctx: ParserTestCtx, parser: JavaParsingHelper = ctx.parser): T {
        val root = parser.parse(getTemplate(construct, ctx))

        return retrieveNode(root)
    }

    /**
     * Parse the string the context described by this object, and finds the first descendant of type [N].
     * The descendant is searched for by [findFirstNodeOnStraightLine], to prevent accidental
     * mis-selection of a node. In such a case, a [NoSuchElementException] is thrown, and you
     * should fix your test case.
     *
     * @param construct The construct to parse
     * @param N The type of node to find
     *
     * @return The first descendant of type [N] found in the parsed expression
     *
     * @throws NoSuchElementException If no node of type [N] is found by [findFirstNodeOnStraightLine]
     * @throws ParseException If the argument is no valid construct of this kind
     *
     */
    inline fun <reified N : Node> parseAndFind(construct: String, ctx: ParserTestCtx): N =
            parseNode(construct, ctx).findFirstNodeOnStraightLine(N::class.java)
                    ?: throw NoSuchElementException("No node of type ${N::class.java.simpleName} in the given $constructName:\n\t$construct")


    override fun toString(): String = "$constructName context"
}


/**
 * Finds the first descendant of type [N] of [this] node which is
 * accessible in a straight line. The descendant must be accessible
 * from the [this] on a path where each node has a single child.
 *
 * If one node has another child, the search is aborted and the method
 * returns null.
 */
fun <N : Node> Node.findFirstNodeOnStraightLine(klass: Class<N>): N? {
    return when {
        klass.isInstance(this) -> klass.cast(this)
        this.numChildren == 1 -> getChild(0).findFirstNodeOnStraightLine(klass)
        else -> null
    }
}


object ExpressionParsingCtx : NodeParsingCtx<ASTExpression>("expression") {

    override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            StatementParsingCtx.getTemplate("Object o = $construct;", ctx)

    override fun retrieveNode(acu: ASTCompilationUnit): ASTExpression =
            StatementParsingCtx.retrieveNode(acu)
                    .descendants(ASTExpression::class.java)
                    .firstOrThrow()
}

object RootParsingCtx : NodeParsingCtx<ASTCompilationUnit>("compilation unit") {

    override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            construct

    override fun retrieveNode(acu: ASTCompilationUnit): ASTCompilationUnit = acu
}

object ModuleDirectiveParsingContext : NodeParsingCtx<ASTModuleDirective>("module directive") {

    override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            """
                module test.module {
                    $construct
                }
            """.trimIndent()

    override fun retrieveNode(acu: ASTCompilationUnit): ASTModuleDirective =
        acu.descendants(ASTModuleDirective::class.java).firstOrThrow()
}

object StatementParsingCtx : NodeParsingCtx<ASTStatement>("statement") {

    override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            TypeBodyParsingCtx.getTemplate("  {\n    $construct\n  }", ctx)


    override fun retrieveNode(acu: ASTCompilationUnit): ASTStatement =
            TypeBodyParsingCtx.retrieveNode(acu)
                    .descendants(ASTBlock::class.java)
                    .firstOrThrow().firstChild as ASTStatement
}

object TypeBodyParsingCtx : NodeParsingCtx<ASTBodyDeclaration>("body declaration") {

    /**
     * Note: this code allows calling [ParserTestCtx.asIfIn]
     */
    override fun getTemplate(construct: String, ctx: ParserTestCtx): String {
        val source = ctx.fullSource
        return if (source != null) {
            source.substringBefore('{') + "{" + construct + source.substringAfter('{')
        } else """
${ctx.packageDecl}
${ctx.imports.joinToString(separator = "\n")}
${ctx.genClassHeader} {
$construct
}
                        """
    }

    override fun retrieveNode(acu: ASTCompilationUnit): ASTBodyDeclaration =
            acu.typeDeclarations.firstOrThrow().getDeclarations().firstOrThrow()
}

object TopLevelTypeDeclarationParsingCtx : NodeParsingCtx<ASTAnyTypeDeclaration>("top-level declaration") {

    override fun getTemplate(construct: String, ctx: ParserTestCtx): String = """
            ${ctx.imports.joinToString(separator = "\n")}
            $construct
            """.trimIndent()

    override fun retrieveNode(acu: ASTCompilationUnit): ASTAnyTypeDeclaration = acu.typeDeclarations.firstOrThrow()
}

object TypeParsingCtx : NodeParsingCtx<ASTType>("type") {
    override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            StatementParsingCtx.getTemplate(" Object f = ($construct) null;", ctx)

    override fun retrieveNode(acu: ASTCompilationUnit): ASTType =
            StatementParsingCtx.retrieveNode(acu)
                    .descendants(ASTCastExpression::class.java)
                    .first()!!
                    .children(ASTType::class.java)
                    .first()!!
}

object AnnotationParsingCtx : NodeParsingCtx<ASTAnnotation>("annotation") {
    override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            StatementParsingCtx.getTemplate("$construct Object f = null;", ctx)

    override fun retrieveNode(acu: ASTCompilationUnit): ASTAnnotation =
            StatementParsingCtx.retrieveNode(acu)
                    .descendants(ASTAnnotation::class.java).firstOrThrow()
}

object TypeParametersParsingCtx : NodeParsingCtx<ASTTypeParameters>("type parameters") {
    override fun getTemplate(construct: String, ctx: ParserTestCtx): String =
            TypeBodyParsingCtx.getTemplate("public $construct void f() {}", ctx)

    override fun retrieveNode(acu: ASTCompilationUnit) =
            TypeBodyParsingCtx.retrieveNode(acu)
                    .descendantsOrSelf()
                    .last(ASTMethodDeclaration::class.java)!!
                    .typeParameters!!
}

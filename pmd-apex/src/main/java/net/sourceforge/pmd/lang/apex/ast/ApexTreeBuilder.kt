/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast

import java.util.Optional

import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor
import net.sourceforge.pmd.lang.ast.Parser.ParserTask
import net.sourceforge.pmd.lang.ast.ParseException

import com.google.summit.ast.CompilationUnit
import com.google.summit.ast.Identifier
import com.google.summit.ast.Node
import com.google.summit.ast.SourceLocation
import com.google.summit.ast.TypeRef
import com.google.summit.ast.declaration.ClassDeclaration
import com.google.summit.ast.declaration.EnumDeclaration
import com.google.summit.ast.declaration.EnumValue
import com.google.summit.ast.declaration.FieldDeclaration
import com.google.summit.ast.declaration.FieldDeclarationGroup
import com.google.summit.ast.declaration.InterfaceDeclaration
import com.google.summit.ast.declaration.MethodDeclaration
import com.google.summit.ast.declaration.ParameterDeclaration
import com.google.summit.ast.declaration.PropertyDeclaration
import com.google.summit.ast.declaration.TriggerDeclaration
import com.google.summit.ast.declaration.TypeDeclaration
import com.google.summit.ast.declaration.VariableDeclaration
import com.google.summit.ast.declaration.VariableDeclarationGroup
import com.google.summit.ast.expression.ArrayExpression
import com.google.summit.ast.expression.AssignExpression
import com.google.summit.ast.expression.BinaryExpression
import com.google.summit.ast.expression.CallExpression
import com.google.summit.ast.expression.CastExpression
import com.google.summit.ast.expression.Expression
import com.google.summit.ast.expression.FieldExpression
import com.google.summit.ast.expression.LiteralExpression
import com.google.summit.ast.expression.NewExpression
import com.google.summit.ast.expression.SoqlExpression
import com.google.summit.ast.expression.SoslExpression
import com.google.summit.ast.expression.SoqlOrSoslBinding
import com.google.summit.ast.expression.SuperExpression
import com.google.summit.ast.expression.TernaryExpression
import com.google.summit.ast.expression.ThisExpression
import com.google.summit.ast.expression.TypeRefExpression
import com.google.summit.ast.expression.UnaryExpression
import com.google.summit.ast.expression.VariableExpression
import com.google.summit.ast.initializer.ConstructorInitializer
import com.google.summit.ast.initializer.MapInitializer
import com.google.summit.ast.initializer.SizedArrayInitializer
import com.google.summit.ast.initializer.ValuesInitializer
import com.google.summit.ast.modifier.AnnotationModifier
import com.google.summit.ast.modifier.ElementArgument
import com.google.summit.ast.modifier.ElementValue
import com.google.summit.ast.modifier.KeywordModifier
import com.google.summit.ast.modifier.KeywordModifier.Keyword
import com.google.summit.ast.modifier.Modifier
import com.google.summit.ast.statement.BreakStatement
import com.google.summit.ast.statement.CompoundStatement
import com.google.summit.ast.statement.ContinueStatement
import com.google.summit.ast.statement.DmlStatement
import com.google.summit.ast.statement.DoWhileLoopStatement
import com.google.summit.ast.statement.EnhancedForLoopStatement
import com.google.summit.ast.statement.ExpressionStatement
import com.google.summit.ast.statement.ForLoopStatement
import com.google.summit.ast.statement.IfStatement
import com.google.summit.ast.statement.ReturnStatement
import com.google.summit.ast.statement.RunAsStatement
import com.google.summit.ast.statement.Statement
import com.google.summit.ast.statement.SwitchStatement
import com.google.summit.ast.statement.ThrowStatement
import com.google.summit.ast.statement.TryStatement
import com.google.summit.ast.statement.VariableDeclarationStatement
import com.google.summit.ast.statement.WhileLoopStatement

import kotlin.reflect.KClass

class ApexTreeBuilder(private val task: ParserTask, private val proc: ApexLanguageProcessor) {
    private val sourceCode = task.textDocument
    private val commentBuilder = ApexCommentBuilder(sourceCode, proc.properties.suppressMarker)

    /** Builds and returns an [ASTApexFile] corresponding to the given [CompilationUnit]. */
    fun buildTree(compilationUnit: CompilationUnit): ASTApexFile {
        // Build tree
        val baseClass =
            build(compilationUnit, parent = null) as? BaseApexClass<*>
                ?: throw ParseException("Unable to build tree")
        val result = ASTApexFile(task, compilationUnit, commentBuilder.suppressMap, proc)
        baseClass.setParent(result)

        // Post-processing passes
        generateAdditional(result)
        postProcessTree(result)
        commentBuilder.addFormalComments()

        return result
    }

    /** Calls additional methods for each node in [root] using a post-order traversal. */
    private fun postProcessTree(root: AbstractApexNode) =
        root.acceptVisitor(
            object : ApexVisitorBase<Unit, Unit>() {
                override fun visitApexNode(node: ApexNode<*>?, data: Unit): Unit =
                    super.visitNode(node, data).also {
                        if (node is AbstractApexNode) {
                            node.calculateTextRegion(sourceCode)
                            when (node) {
                              is AbstractApexCommentContainerNode<*> ->
                                  node.setContainsComment(commentBuilder.containsComments(node))
                              is ASTUserInterface,
                              is ASTProperty,
                              is ASTUserClass,
                              is ASTFieldDeclaration,
                              is ASTMethod -> commentBuilder.buildFormalComment(node)
                            }
                        }
                    }
            },
            Unit
        )

    /** Builds an [ApexNode] wrapper for [node]. */
    private fun build(node: Node?, parent: AbstractApexNode?): AbstractApexNode? =
        when (node) {
            null -> null
            is CompilationUnit -> build(node.typeDeclaration, parent)
            is TypeDeclaration -> buildTypeDeclaration(node)
            is EnumValue -> buildEnumValue(node)
            is MethodDeclaration -> buildMethodDeclaration(node, parent)
            is PropertyDeclaration -> buildPropertyDeclaration(node)
            is FieldDeclarationGroup -> buildFieldDeclarationGroup(node)
            is FieldDeclaration -> buildFieldDeclaration(node)
            is CompoundStatement -> ASTBlockStatement(node).apply { buildChildren(node, parent = this) }
            is ExpressionStatement ->
                ASTExpressionStatement(node).apply { buildChildren(node, parent = this) }
            is AssignExpression ->
                ASTAssignmentExpression(node).apply { buildChildren(node, parent = this) }
            is ArrayExpression -> buildArrayExpression(node)
            is LiteralExpression ->
                ASTLiteralExpression(node).apply { buildChildren(node, parent = this) }
            is CastExpression -> ASTCastExpression(node).apply { buildChildren(node, parent = this) }
            is BinaryExpression -> buildBinaryExpression(node)
            is UnaryExpression -> buildUnaryExpression(node)
            is SuperExpression ->
                ASTSuperVariableExpression(node).apply { buildChildren(node, parent = this) }
            is ThisExpression ->
                ASTThisVariableExpression(node).apply { buildChildren(node, parent = this) }
            is TypeRefExpression ->
                ASTClassRefExpression(node).apply { buildChildren(node, parent = this) }
            is FieldExpression -> buildFieldExpression(node)
            is VariableExpression -> buildVariableExpression(node)
            is CallExpression -> buildCallExpression(node)
            is TernaryExpression -> buildTernaryExpression(node)
            is NewExpression -> build(node.initializer, parent)
            is SoqlExpression -> ASTSoqlExpression(node).apply { buildChildren(node, parent = this) }
            is SoslExpression -> ASTSoslExpression(node).apply { buildChildren(node, parent = this) }
            is SoqlOrSoslBinding -> ASTBindExpressions(node).apply { buildChildren(node, parent = this) }
            is ConstructorInitializer -> buildConstructorInitializer(node)
            is ValuesInitializer -> buildValuesInitializer(node)
            is MapInitializer -> buildMapInitializer(node)
            is SizedArrayInitializer -> buildSizedArrayInitializer(node)
            is DmlStatement -> buildDmlStatement(node)
            is IfStatement -> buildIfStatement(node)
            is VariableDeclarationStatement -> buildVariableDeclarationGroup(node.group)
            is VariableDeclarationGroup -> buildVariableDeclarationGroup(node)
            is VariableDeclaration -> buildVariableDeclaration(node)
            is EnhancedForLoopStatement -> buildEnhancedForLoopStatement(node)
            is DoWhileLoopStatement -> buildDoWhileLoopStatement(node)
            is WhileLoopStatement -> buildWhileLoopStatement(node)
            is ForLoopStatement -> buildForLoopStatement(node)
            is SwitchStatement -> ASTSwitchStatement(node).apply { buildChildren(node, parent = this) }
            is SwitchStatement.When -> buildSwitchWhen(node)
            is ReturnStatement -> ASTReturnStatement(node).apply { buildChildren(node, parent = this) }
            is RunAsStatement -> ASTRunAsBlockStatement(node).apply { buildChildren(node, parent = this) }
            is ThrowStatement -> ASTThrowStatement(node).apply { buildChildren(node, parent = this) }
            is TryStatement -> buildTryStatement(node)
            is TryStatement.CatchBlock -> buildCatchBlock(node)
            is BreakStatement -> ASTBreakStatement(node).apply { buildChildren(node, parent = this) }
            is ContinueStatement ->
                ASTContinueStatement(node).apply { buildChildren(node, parent = this) }
            is ParameterDeclaration -> buildParameterDeclaration(node)
            is AnnotationModifier -> ASTAnnotation(node).apply { buildChildren(node, parent = this) }
            is ElementArgument ->
                ASTAnnotationParameter(node).apply { buildChildren(node, parent = this) }
            is ElementValue,
            is Identifier,
            is KeywordModifier,
            is TypeRef -> null
            else -> {
                throw ParseException("No adapter exists for type ${node::class.qualifiedName}")
            }
        }

    /** Builds an [ApexNode] wrapper for [node] and sets its parent to [parent]. */
    private fun buildAndSetParent(node: Node?, parent: AbstractApexNode) =
        build(node, parent)?.also { it.setParent(parent) }

    /**
     * Builds an [ApexNode] wrapper for each [child][Node.getChildren] of [node] and sets its parent
     * to [parent].
     *
     * If [exclude] is provided, child nodes matching this predicate are not visited.
     */
    private fun buildChildren(
        node: Node,
        parent: AbstractApexNode,
        exclude: (Node) -> Boolean = { false } // exclude none by default
    ) = node.getChildren().filterNot(exclude).forEach { buildAndSetParent(it, parent) }

    /** Builds an [BaseApexClass] wrapper for the [TypeDeclaration] node. */
    private fun buildTypeDeclaration(node: TypeDeclaration) =
        when (node) {
            is ClassDeclaration -> ASTUserClass(node)
            is InterfaceDeclaration -> ASTUserInterface(node)
            is EnumDeclaration -> ASTUserEnum(node)
            is TriggerDeclaration -> ASTUserTrigger(node)
        }.apply {
            buildModifiers(node.modifiers).also { it.setParent(this) }
            if (node is TriggerDeclaration) {
                // 1. Create a synthetic "invoke" ASTMethod for the trigger body
                val invokeMethod = ASTMethod(
                  /* name= */ "invoke",
                  /* internalName= */ "<invoke>",
                  /* parameterTypes= */ emptyList(),
                  /* returnType= */ "void",
                 SourceLocation.UNKNOWN,
                ).also{ it.setParent(this) }
                // 2. Add the expected ASTModifier child node
                buildModifiers(emptyList()).also { it.setParent(invokeMethod) }
                // 3. Elide the body CompoundStatement->ASTBlockStatement
                node.body.forEach { buildAndSetParent(it, parent = invokeMethod as AbstractApexNode) }
            } else {
                buildChildren(node, parent = this, exclude = { it in node.modifiers })
            }
        }

    /** Builds an [ASTMethod] wrapper for the [MethodDeclaration] node. */
    private fun buildMethodDeclaration(node: MethodDeclaration, parent: AbstractApexNode?) =
        when {
            node.isAnonymousInitializationCode() && !node.hasKeyword(Keyword.STATIC) ->
                    build(node.body, parent)
            else -> {
                ASTMethod.fromNode(node).apply {
                    buildModifiers(
                        // Getters and setters default to property visibility
                        if (node.modifiers.isEmpty() && parent is ASTProperty) {
                            parent.node.modifiers
                        } else {
                            node.modifiers
                        }).also { it.setParent(this) }
                    buildChildren(node, parent = this, exclude = { it in node.modifiers })
                }
            }
        }


    /** Builds an [ASTProperty] wrapper for the [PropertyDeclaration] node. */
    private fun buildPropertyDeclaration(node: PropertyDeclaration) =
        ASTProperty(node).apply {
            buildModifiers(node.modifiers).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it in node.modifiers })
        }

    /** Builds an [ASTFieldDeclarationStatements] wrapper for the [FieldDeclarationGroup] node. */
    private fun buildFieldDeclarationGroup(node: FieldDeclarationGroup) =
        ASTFieldDeclarationStatements(node).apply {
            buildModifiers(node.modifiers).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it in node.modifiers })
        }

    /** Builds an [ASTField] wrapper for the [EnumValue] node. */
    private fun buildEnumValue(node: EnumValue) =
        ASTField(node.parent as EnumDeclaration, node.id)

    private fun buildFieldDeclaration(node: FieldDeclaration) =
        ASTFieldDeclaration(node).apply {
            buildChildren(node, parent = this)

            ASTVariableExpression(node.id)
                .apply {
                    buildReferenceExpression(components = emptyList(), receiver = null, ReferenceType.NONE)
                        .also { it.setParent(this) }
                }
                .also { it.setParent(this) }
        }

    /**
     * Builds an [ASTArrayStoreExpression] or [ASTArrayLoadExpression] wrapper for the
     * [ArrayExpression] node.
     */
    private fun buildArrayExpression(node: ArrayExpression) =
        if ((node.parent as? AssignExpression)?.target == node) {
            ASTArrayStoreExpression(node)
        } else {
            ASTArrayLoadExpression(node)
        }
            .apply { buildChildren(node, parent = this) }

    /**
     * Builds an [ASTBinaryExpression], [ASTBooleanExpression], or [ASTInstanceOfExpression] wrapper
     * for the [BinaryExpression] node.
     */
    private fun buildBinaryExpression(node: BinaryExpression) =
        when (node.op) {
            BinaryExpression.Operator.INSTANCEOF -> ASTInstanceOfExpression(node)
            BinaryExpression.Operator.GREATER_THAN_OR_EQUAL,
            BinaryExpression.Operator.GREATER_THAN,
            BinaryExpression.Operator.LESS_THAN,
            BinaryExpression.Operator.LESS_THAN_OR_EQUAL,
            BinaryExpression.Operator.EQUAL,
            BinaryExpression.Operator.NOT_EQUAL,
            BinaryExpression.Operator.ALTERNATIVE_NOT_EQUAL,
            BinaryExpression.Operator.EXACTLY_EQUAL,
            BinaryExpression.Operator.EXACTLY_NOT_EQUAL,
            BinaryExpression.Operator.LOGICAL_AND,
            BinaryExpression.Operator.LOGICAL_OR -> ASTBooleanExpression(node)
            BinaryExpression.Operator.ADDITION,
            BinaryExpression.Operator.SUBTRACTION,
            BinaryExpression.Operator.MULTIPLICATION,
            BinaryExpression.Operator.DIVISION,
            BinaryExpression.Operator.LEFT_SHIFT,
            BinaryExpression.Operator.RIGHT_SHIFT_SIGNED,
            BinaryExpression.Operator.RIGHT_SHIFT_UNSIGNED,
            BinaryExpression.Operator.BITWISE_AND,
            BinaryExpression.Operator.BITWISE_OR,
            BinaryExpression.Operator.BITWISE_XOR,
            BinaryExpression.Operator.NULL_COALESCING -> ASTBinaryExpression(node)
        }.apply { buildChildren(node, parent = this) }

    /**
     * Builds an [ASTPrefixExpression] or [ASTPostfixExpression] wrapper for the [UnaryExpression]
     * node.
     */
    private fun buildUnaryExpression(node: UnaryExpression) =
        when (node.op) {
            UnaryExpression.Operator.PLUS,
            UnaryExpression.Operator.NEGATION,
            UnaryExpression.Operator.LOGICAL_COMPLEMENT,
            UnaryExpression.Operator.BITWISE_NOT,
            UnaryExpression.Operator.PRE_INCREMENT,
            UnaryExpression.Operator.PRE_DECREMENT,
            -> ASTPrefixExpression(node)
            UnaryExpression.Operator.POST_INCREMENT,
            UnaryExpression.Operator.POST_DECREMENT,
            -> ASTPostfixExpression(node)
        }.apply { buildChildren(node, parent = this) }

    /**
     * Builds an [ASTVariableExpression] or [ASTTriggerVariableExpression] wrapper for the
     * [FieldExpression] node.
     */
    private fun buildFieldExpression(node: FieldExpression) =
        if (
            node.obj is VariableExpression &&
            (node.obj as VariableExpression).id.string.lowercase() == "trigger"
        ) {
            ASTTriggerVariableExpression(node)
        } else {
            val (receiver, components, isSafe) = flattenExpression(node)
            ASTVariableExpression(components.last()).apply {
                buildReferenceExpression(
                    components.dropLast(1),
                    receiver,
                    referenceTypeOf(expr = node),
                    isSafe
                )
                    .also { it.setParent(this) }
            }
        }

    /** Builds an [ASTVariableExpression] wrapper for the [VariableExpression] node. */
    private fun buildVariableExpression(node: VariableExpression): ASTVariableExpression {
        val (receiver, components, isSafe) = flattenExpression(node)
        return ASTVariableExpression(components.last()).apply {
            buildReferenceExpression(
                components.dropLast(1),
                receiver,
                referenceTypeOf(expr = node),
                isSafe
            )
                .also { it.setParent(this) }
        }
    }

    /**
     * Builds an [ASTMethodCallExpression], [ASTThisMethodCallExpression], or
     * [ASTSuperMethodCallExpression] wrapper for the [CallExpression] node.
     */
    private fun buildCallExpression(node: CallExpression) =
        when (node.id.string.lowercase()) {
            "this" -> ASTThisMethodCallExpression(node).apply { buildChildren(node, parent = this) }
            "super" -> ASTSuperMethodCallExpression(node).apply { buildChildren(node, parent = this) }
            else -> {
                val (receiver, components, isSafe) = flattenExpression(node.receiver)
                ASTMethodCallExpression(node, components).apply {
                    buildReferenceExpression(components, receiver, ReferenceType.METHOD, isSafe || node.isSafe).also {
                        it.setParent(this)
                    }
                    buildChildren(node, parent = this, exclude = { it == node.receiver })
                }
            }
        }

    /**
     * Result of [flattenExpression].
     *
     * @param remainder remaining [Expression] that could not be flattened
     * @param components list of extracted [Identifier]s
     * @param isSafe whether a safe [access][FieldExpression.isSafe] or [call][CallExpression.isSafe]
     * was found
     */
    private data class FlatExpression(
        val remainder: Expression?,
        val components: List<Identifier>,
        val isSafe: Boolean
    )

    /**
     * Attempts to flatten an [Expression] tree containing variable references into a list of
     * [Identifier]s. Applicable nodes are [FieldExpression] and [VariableExpression].
     */
    private fun flattenExpression(
        node: Expression?,
        components: List<Identifier> = emptyList()
    ): FlatExpression =
        when (node) {
            is FieldExpression ->
                if (node.isSafe) {
                    // Don't flatten a safe access
                    FlatExpression(
                        remainder = node.obj,
                        components = listOf(node.field) + components,
                        isSafe = true
                    )
                } else {
                    // Extract node.field and continue flattening
                    flattenExpression(node = node.obj, components = listOf(node.field) + components)
                }
            is VariableExpression ->
                // Extract node.id and stop
                FlatExpression(remainder = null, components = listOf(node.id) + components, isSafe = false)
            else ->
                // Can't flatten
                FlatExpression(remainder = node, components, isSafe = false)
        }

    /**
     * Builds an [ASTReferenceExpression] or [ASTEmptyReferenceExpression] from [components].
     *
     * @param components the [Identifier]s in this reference expression
     * @param receiver the node that is being accessed
     * @param isSafe whether this is a safe access
     */
    private fun buildReferenceExpression(
        components: List<Identifier>,
        receiver: Node?,
        referenceType: ReferenceType,
        isSafe: Boolean = false
    ) =
        if (receiver == null && components.isEmpty()) {
            ASTEmptyReferenceExpression()
        } else {
            ASTReferenceExpression(components, referenceType, isSafe)
        }
            .apply { buildAndSetParent(receiver, parent = this) }

    /** Determines the [ReferenceType] of an [Expression]. */
    private fun referenceTypeOf(expr: Expression) =
        if ((expr.parent as? AssignExpression)?.target == expr) {
            ReferenceType.STORE
        } else {
            ReferenceType.LOAD
        }

    /** Builds an [ASTTernaryExpression] wrapper for the [TernaryExpression]. */
    private fun buildTernaryExpression(node: TernaryExpression) =
        ASTTernaryExpression(node).apply {
            buildCondition(node.condition).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it == node.condition })
        }

    /**
     * Builds an [ASTNewListInitExpression], [ASTNewMapInitExpression], [ASTNewSetInitExpression],
     * [ASTNewKeyValueObjectExpression], or [ASTNewObjectExpression] wrapper for the
     * [ConstructorInitializer].
     */
    private fun buildConstructorInitializer(node: ConstructorInitializer) =
        when (node.type.components.first().id.string.lowercase()) {
            "list" -> ASTNewListInitExpression(node)
            "map" -> ASTNewMapInitExpression(node)
            "set" -> ASTNewSetInitExpression(node)
            else -> {
                // Object initializer
                if (node.args.isNotEmpty() && node.args.first() is AssignExpression) {
                    // Named arguments
                    ASTNewKeyValueObjectExpression(node)
                } else {
                    // Unnamed arguments
                    ASTNewObjectExpression(node)
                }
            }
        }.apply { buildChildren(node, parent = this) }

    /**
     * Builds an [ASTNewListLiteralExpression], [ASTNewMapLiteralExpression], or
     * [ASTNewSetLiteralExpression] wrapper for the [ValuesInitializer].
     */
    private fun buildValuesInitializer(node: ValuesInitializer) =
        when (node.type.components.first().id.string.lowercase()) {
            "list" -> ASTNewListLiteralExpression(node)
            "map" -> ASTNewMapLiteralExpression(node)
            "set" -> ASTNewSetLiteralExpression(node)
            else -> ASTNewListLiteralExpression(node) // Array
        }.apply { buildChildren(node, parent = this) }

    /** Builds an [ASTNewMapLiteralExpression] wrapper for the [MapInitializer]. */
    private fun buildMapInitializer(node: MapInitializer) =
        ASTNewMapLiteralExpression(node).apply {
            /** Builds an [ASTMapEntryNode] for the [map entry][entry]. */
            fun buildMapEntry(entry: Pair<Expression, Expression>) =
                ASTMapEntryNode(entry.first, entry.second).apply {
                    buildAndSetParent(entry.first, parent = this)
                    buildAndSetParent(entry.second, parent = this)
                }

            node.pairs.forEach { pair -> buildMapEntry(pair).also { it.setParent(this) } }
        }

    /** Builds an [ASTNewListInitExpression] wrapper for the [SizedArrayInitializer]. */
    private fun buildSizedArrayInitializer(node: SizedArrayInitializer) =
        ASTNewListInitExpression(node).apply { buildChildren(node, parent = this) }

    /** Builds an [ApexNode] wrapper for the [DmlStatement]. */
    private fun buildDmlStatement(node: DmlStatement) =
        when (node) {
            is DmlStatement.Insert -> ASTDmlInsertStatement(node)
            is DmlStatement.Update -> ASTDmlUpdateStatement(node)
            is DmlStatement.Delete -> ASTDmlDeleteStatement(node)
            is DmlStatement.Undelete -> ASTDmlUndeleteStatement(node)
            is DmlStatement.Upsert -> ASTDmlUpsertStatement(node)
            is DmlStatement.Merge -> ASTDmlMergeStatement(node)
        }.apply { buildChildren(node, parent = this) }

    /** Wraps the body of a control statement with an [ASTBlockStatement] if it isn't already one. */
    private fun wrapBody(body: Statement, parent: AbstractApexNode) =
        when (body) {
            is CompoundStatement -> build(body, parent) as ASTBlockStatement
            else -> ASTBlockStatement(body).apply { buildAndSetParent(body, parent = this) }
        }

    /** Builds an [ASTIfElseBlockStatement] wrapper for the [IfStatement]. */
    private fun buildIfStatement(node: IfStatement): ASTIfElseBlockStatement {
        val (ifBlocks, elseBlock) = flattenIfStatement(node)

        /** Builds an [ASTIfBlockStatement] wrapper for the [if block][IfStatement]. */
        fun buildIfBlock(ifBlock: IfStatement) =
            ASTIfBlockStatement(ifBlock).apply {
                buildCondition(ifBlock.condition).also { it.setParent(this) }
                wrapBody(ifBlock.thenStatement, parent = this).also { it.setParent(this) }
            }

        return ASTIfElseBlockStatement(node, elseBlock != null).apply {
            ifBlocks.forEach { ifBlock -> buildIfBlock(ifBlock).also { it.setParent(this) } }
            if (elseBlock != null) {
                wrapBody(elseBlock, parent = this).also { it.setParent(this) }
            }
        }
    }

    /** Result of [flattenIfStatement]. */
    private data class FlatIfStatement(val ifBlocks: List<IfStatement>, val elseBlock: Statement?)

    /** Flattens an [IfStatement] into a list of [IfStatement]s. */
    private fun flattenIfStatement(
        node: Statement?,
        ifBlocks: List<IfStatement> = emptyList()
    ): FlatIfStatement =
        when (node) {
            is IfStatement ->
                // Extract node and continue flattening
                flattenIfStatement(node = node.elseStatement, ifBlocks = ifBlocks + node)
            else ->
                // Can't flatten
                FlatIfStatement(ifBlocks, elseBlock = node)
        }

    /** Builds an [ASTVariableDeclarationStatements] for the [VariableDeclarationGroup]. */
    private fun buildVariableDeclarationGroup(node: VariableDeclarationGroup) =
        ASTVariableDeclarationStatements(node).apply {
            buildModifiers(node.modifiers).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it in node.modifiers })
        }

    /** Builds an [ASTVariableDeclaration] wrapper for the [VariableDeclaration]. */
    private fun buildVariableDeclaration(node: VariableDeclaration) =
        ASTVariableDeclaration(node).apply {
            buildChildren(node, parent = this)

            ASTVariableExpression(node.id)
                .apply {
                    buildReferenceExpression(components = emptyList(), receiver = null, ReferenceType.NONE)
                        .also { it.setParent(this) }
                }
                .also { it.setParent(this) }
        }

    /** Builds an [ASTForEachStatement] wrapper for the [EnhancedForLoopStatement]. */
    private fun buildEnhancedForLoopStatement(node: EnhancedForLoopStatement) =
        ASTForEachStatement(node).apply {
            buildVariableDeclarationGroup(node.element).also { it.setParent(this) }

            if (node.element.declarations.size != 1) {
              throw ParseException("Expected enhanced-for to declare a single variable")
            }
            ASTVariableExpression(node.element.declarations.first().id)
                .apply {
                    buildReferenceExpression(components = emptyList(), receiver = null, ReferenceType.NONE)
                        .also { it.setParent(this) }
                }
                .also { it.setParent(this) }

            wrapBody(node.body, parent = this).also { it.setParent(this) }

            buildChildren(
                node,
                parent = this,
                exclude = { it == node.element || it == node.body }
            )
        }

    /** Builds an [ASTDoLoopStatement] wrapper for the [DoWhileLoopStatement]. */
    private fun buildDoWhileLoopStatement(node: DoWhileLoopStatement) =
        ASTDoLoopStatement(node).apply {
            buildCondition(node.condition).also { it.setParent(this) }
            wrapBody(node.body, parent = this).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it == node.condition || it == node.body })
        }

    /** Builds an [ASTWhileLoopStatement] wrapper for the [WhileLoopStatement]. */
    private fun buildWhileLoopStatement(node: WhileLoopStatement) =
        ASTWhileLoopStatement(node).apply {
            buildCondition(node.condition).also { it.setParent(this) }
            wrapBody(node.body, parent = this).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it == node.condition || it == node.body })
        }

    /** Builds an [ASTForEachStatement] wrapper for the [ForLoopStatement]. */
    private fun buildForLoopStatement(node: ForLoopStatement) =
        ASTForLoopStatement(node).apply {
            fun buildInitialization(expr: Expression) =
                ASTExpression(expr).apply { buildAndSetParent(expr, parent = this) }

            node.declarationGroup?.let{ group ->
                buildVariableDeclarationGroup(group).also { it.setParent(this) }
            }
            node.condition?.let{ condition ->
                buildCondition(condition).also { it.setParent(this) }
            }
            node.initializations.forEach { expr -> buildInitialization(expr).also { it.setParent(this) } }

            wrapBody(node.body, parent = this).also { it.setParent(this) }

            buildChildren(
                node,
                parent = this,
                exclude = {
                    it == node.declarationGroup ||
                        it == node.condition ||
                        it in node.initializations ||
                        it == node.body
                }
            )
        }

    /**
     * Builds an [ASTValueWhenBlock], [ASTTypeWhenBlock], or [ASTElseWhenBlock] wrapper for the
     * [SwitchStatement.When].
     */
    private fun buildSwitchWhen(node: SwitchStatement.When) =
        when (node) {
            is SwitchStatement.WhenValue ->
                ASTValueWhenBlock(node).apply {
                    node.values.forEach { value ->
                        when (value) {
                            is LiteralExpression,
                            is UnaryExpression /* negative */ ->
                                ASTLiteralCase(value).apply { buildAndSetParent(value, parent = this) }
                            is VariableExpression -> ASTIdentifierCase(value)
                            else -> throw ParseException("Invalid when value type")
                        }.also { it.setParent(this) }
                    }

                    buildChildren(node, parent = this, exclude = { it in node.values })
                }
            is SwitchStatement.WhenType ->
                ASTTypeWhenBlock(node).apply { buildChildren(node, parent = this) }
            is SwitchStatement.WhenElse ->
                ASTElseWhenBlock(node).apply { buildChildren(node, parent = this) }
        }

    /** Builds an [ASTTryCatchFinallyBlockStatement] wrapper for the [TryStatement]. */
    private fun buildTryStatement(node: TryStatement) =
        ASTTryCatchFinallyBlockStatement(node).apply {
            buildAndSetParent(node.body, parent = this)
            buildChildren(node, parent = this, exclude = { it == node.body })
        }

    /** Builds an [ASTCatchBlockStatement] wrapper for the [TryStatement.CatchBlock]. */
    private fun buildCatchBlock(node: TryStatement.CatchBlock) =
        ASTCatchBlockStatement(node).apply {
            buildChildren(node, parent = this, exclude = { it == node.exception })
        }

    /** Builds an [ASTParameter] wrapper for the [ParameterDeclaration]. */
    private fun buildParameterDeclaration(node: ParameterDeclaration) =
        ASTParameter(node).apply {
            buildModifiers(node.modifiers).also { it.setParent(this) }
            buildChildren(node, parent = this, exclude = { it in node.modifiers })
        }

    /** Builds an [ASTStandardCondition] wrapper for the [condition]. */
    private fun buildCondition(condition: Node) =
        ASTStandardCondition(condition).apply { buildAndSetParent(condition, this) }

    /** Builds an [ASTModifierNode] wrapper for the list of [Modifier]s. */
    private fun buildModifiers(modifiers: List<Modifier>) =
        ASTModifierNode(modifiers).apply { modifiers.forEach { buildAndSetParent(it, parent = this) } }

    /** Generates additional nodes for the [root] node. */
    private fun generateAdditional(root: AbstractApexNode) {
        // Generate fields
        findDescendants(root, nodeType = ASTFieldDeclarationStatements::class).forEach { node ->
            generateFields(node)
        }

        findDescendants(root, nodeType = ASTProperty::class).forEach { node -> generateFields(node) }

        // Sort resulting nodes
        findDescendants(root, nodeType = BaseApexClass::class).forEach { node ->
            sortUserClassChildren(node)
        }
    }

    /**
      * Sort children of [BaseApexClass] (ASTUserClass, ASTUserTrigger, ...) in historical order.
      *
      * This sorts [ASTField] nodes immediately after [ASTModifierNode] nodes at
      * the start of the ordered children.
      */
    private fun sortUserClassChildren(node: BaseApexClass<*>) {
      val children = ArrayList(node.children().toList())

      children.sortBy{ when (it) {
          is ASTModifierNode -> 1
          is ASTField -> 2
          else -> 3
        }
      }

      for(i in 0 until node.getNumChildren()) {
        node.setChild(children[i] as AbstractApexNode, i)
      }
    }

    /** Returns all descendants of [root] of type [nodeType], including [root]. */
    private inline fun <reified T : AbstractApexNode> findDescendants(
        root: AbstractApexNode,
        nodeType: KClass<T>
    ): List<T> =
        root.descendants(nodeType.java).crossFindBoundaries().toList() + (if (root is T) listOf(root) else emptyList())

    /** Generates [ASTField] nodes for the [ASTFieldDeclarationStatements]. */
    private fun generateFields(node: ASTFieldDeclarationStatements) {
        val parent = if (node.parent is BaseApexClass<*>) {
            node.parent as BaseApexClass<*>
        } else if (node.parent is ASTMethod && (node.parent as ASTMethod).isTriggerBlock) {
            node.parent.parent as BaseApexClass<*>
        } else {
            throw IllegalStateException("Unexpected apex tree - field declaration $node cannot appear hear")
        }

        node.node.declarations
            .map { decl ->
                ASTField(decl.type, decl.id, Optional.ofNullable(decl.initializer)).apply {
                    buildModifiers(decl.modifiers).also { it.setParent(this) }
                }
            }
            .forEach { field -> field.setParent(parent) }
    }

    /** Generates [ASTField] nodes for the [ASTProperty]. */
    private fun generateFields(node: ASTProperty) {
        val field =
            ASTField(node.node.type, node.node.id, Optional.empty()).apply {
                buildModifiers(node.node.modifiers).also { it.setParent(this) }
            }
        field.setParent(node)
    }

    /**
     * If [parent] is not null, adds this [ApexNode] as a [child][AbstractApexNode.addChild] and sets
     * [parent] as the [parent][AbstractApexNode.setParent].
     */
    private fun AbstractApexNode.setParent(parent: AbstractApexNode?) {
        parent?.addChild(this, parent.numChildren)
    }
}

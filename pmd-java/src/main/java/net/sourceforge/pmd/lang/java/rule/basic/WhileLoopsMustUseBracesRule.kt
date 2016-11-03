package net.sourceforge.pmd.lang.java.rule.basic

/**
 * Created by Waqas on 30/10/2016.
 */
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.java.ast.ASTBlock
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule

@Suppress("unused")
class WhileLoopsMustUseBracesRule : AbstractJavaRule() {
    override fun visit(node: ASTWhileStatement, data: Any): Any? {
        println("checking for curly braces around While")
        val firstStmt = node.jjtGetChild(1)
        if (!hasBlockAsFirstChild(firstStmt)) {
            addViolation(data, node)
        }

        println("after checking Rule, calling super with: $node, data: $data")
        return super.visit(node, data)
    }

    private fun hasBlockAsFirstChild(node: Node): Boolean {
        return node.jjtGetNumChildren() != 0 && node.jjtGetChild(0) is ASTBlock
    }
}
package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.java.ParserTstUtil


const val defaultJavaVersion = "11"

inline fun <reified N : Node> parseExpression(expr: String, javaVersion: String = defaultJavaVersion): N =
        parseAstExpression(expr, javaVersion).getFirstDescendantOfType(N::class.java)

fun parseAstExpression(expr: String, javaVersion: String = defaultJavaVersion): ASTExpression {

    val source = """
        class Foo {
            {
              Object o = $expr;
            }
        }
    """.trimIndent()

    val acu = ParserTstUtil.parseAndTypeResolveJava(javaVersion, source)

    return acu.getFirstDescendantOfType(ASTVariableInitializer::class.java).jjtGetChild(0) as ASTExpression
}


inline fun <reified N : Node> parseStatement(stmt: String, javaVersion: String = defaultJavaVersion): N =
        parseStatement(stmt, javaVersion).getFirstChildOfType(N::class.java)


fun parseStatement(statement: String, javaVersion: String = defaultJavaVersion): ASTBlockStatement {

    // place the param in a statement parsing context
    val source = """
            class Foo {
               {
                 $statement
               }
            }
        """.trimIndent()

    val root = ParserTstUtil.parseAndTypeResolveJava(javaVersion, source)

    return root.getFirstDescendantOfType(ASTBlockStatement::class.java)
}

// also need e.g. parseDeclaration

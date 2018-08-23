package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.Node

/** Extension methods to make the Node API more Kotlin-like */


val Node.numChildren: Int
    get() = this.jjtGetNumChildren()

val Node.childIndex: Int
    get() = this.jjtGetChildIndex()

val Node.parent: Node?
    get() = this.jjtGetParent()


fun Node.getChild(i: Int) = jjtGetChild(i)

fun Node.safeGetChild(i: Int): Node? = when {
    i < numChildren -> jjtGetChild(i)
    else -> null
}


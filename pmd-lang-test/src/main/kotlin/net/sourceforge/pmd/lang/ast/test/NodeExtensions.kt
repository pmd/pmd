package net.sourceforge.pmd.lang.ast.test

import net.sourceforge.pmd.lang.ast.Node


/** Extension methods to make the Node API more Kotlin-like */

// kotlin converts getters of java types into property accessors
// but it doesn't recognise jjtGet* methods as getters

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


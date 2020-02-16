/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import net.sourceforge.pmd.lang.ast.Node


/** Extension methods to make the Node API more Kotlin-like */

// kotlin converts getters of java types into property accessors
// but it doesn't recognise jjtGet* methods as getters

fun Node.safeGetChild(i: Int): Node? = when {
    i < numChildren -> jjtGetChild(i)
    else -> null
}


/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.rule.xpath.Attribute
import net.sourceforge.pmd.util.treeexport.TextTreeRenderer
import org.apache.commons.lang3.StringEscapeUtils

/**
 * Prints just the structure, like so:
 *
 *           └── LocalVariableDeclaration
 *               ├── Type
 *               │   └── PrimitiveType
 *               └── VariableDeclarator
 *                   ├── VariableDeclaratorId
 *                   └── VariableInitializer
 *                       └── 1 child not shown
 *
 */
val SimpleNodePrinter = TextTreeRenderer(true, -1)


open class RelevantAttributePrinter : BaseNodeAttributePrinter() {

    private val Ignored = setOf("BeginLine", "EndLine", "BeginColumn", "EndColumn", "FindBoundary", "SingleLine")

    override fun ignoreAttribute(node: Node, attribute: Attribute): Boolean =
            Ignored.contains(attribute.name) || attribute.name == "Image" && attribute.value == null

}

/**
 * Base attribute printer, subclass to filter attributes.
 */
open class BaseNodeAttributePrinter : TextTreeRenderer(true, -1) {

    data class AttributeInfo(val name: String, val value: Any?)

    protected open fun ignoreAttribute(node: Node, attribute: Attribute): Boolean = true

    protected open fun fillAttributes(node: Node, result: MutableList<AttributeInfo>) {
        node.xPathAttributesIterator
                .asSequence()
                .filterNot { ignoreAttribute(node, it) }
                .map { AttributeInfo(it.name, it.value?.toString()) }
                .forEach { result += it }
    }


    override fun appendNodeInfoLn(out: Appendable, node: Node) {
        out.append(node.xPathNodeName)

        val attrs = mutableListOf<AttributeInfo>().also { fillAttributes(node, it) }

        // sort to get deterministic results
        attrs.sortBy { it.name }

        attrs.joinTo(buffer = out, prefix = "[", postfix = "]") {
            "@${it.name} = ${valueToString(it.value)}"
        }

        out.append("\n")
    }

    protected open fun valueToString(value: Any?): String? {
        return when (value) {
            is String -> "\"" + StringEscapeUtils.unescapeJava(value) + "\""
            is Char -> '\''.toString() + value.toString().replace("'".toRegex(), "\\'") + '\''.toString()
            is Enum<*> -> value.enumDeclaringClass.simpleName + "." + value.name
            is Class<*> -> value.canonicalName?.let { "$it.class" }
            is Number, is Boolean, null -> value.toString()
            else -> null
        }
    }

    private val Enum<*>.enumDeclaringClass: Class<*>
        get() = this.javaClass.let {
            when {
                it.isEnum -> it
                else -> it.enclosingClass.takeIf { it.isEnum }
                        ?: throw IllegalStateException()
            }
        }

}


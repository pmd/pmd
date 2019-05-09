/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.test

import com.github.oowekyala.treeutils.printers.SimpleTreePrinter
import net.sourceforge.pmd.lang.ast.Node
import net.sourceforge.pmd.lang.ast.xpath.Attribute
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
object SimpleNodePrinter : SimpleTreePrinter<Node>(NodeTreeLikeAdapter, UnicodeStrings)

/**
 * Prints all the XPath attributes of the node.
 */
object FullAttributePrinter : BaseNodeAttributePrinter()


open class RelevantAttributePrinter(stringConfig: StringConfig = UnicodeStrings) : BaseNodeAttributePrinter(stringConfig) {

    private val Ignored = setOf("BeginLine", "EndLine", "BeginColumn", "EndColumn", "FindBoundary", "SingleLine")

    override fun ignoreAttribute(node: Node, attribute: Attribute): Boolean =
            Ignored.contains(attribute.name) || attribute.name == "Image" && attribute.value == null

}

/**
 * Base attribute printer, subclass to filter attributes.
 */
open class BaseNodeAttributePrinter(stringConfig: StringConfig = UnicodeStrings) : SimpleTreePrinter<Node>(NodeTreeLikeAdapter, stringConfig) {

    protected open fun ignoreAttribute(node: Node, attribute: Attribute): Boolean = true

    override fun StringBuilder.appendSingleNode(node: Node): StringBuilder {

        append(node.xPathNodeName)

        return node.xPathAttributesIterator
                .asSequence()
                // sort to get deterministic results
                .sortedBy { it.name }
                .filterNot { ignoreAttribute(node, it) }
                .joinTo(buffer = this, prefix = "[", postfix = "]") {
                    "@${it.name} = ${valueToString(it.value)}"
                }
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


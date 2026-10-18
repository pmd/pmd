package net.sourceforge.pmd.lang.kotlin.rule.xpath.typeis

import java.io.Serializable

class Foo2() {
    // Inferred type net.sourceforge.pmd.lang.kotlin.rule.xpath.typeis.Simple -- typeIs('java.io.Serializable') should match
    val myValue = Simple("Hello")

    open fun doSomething(map: Map<*, *>): Int {
        return map.size
    }
}

class Simple(
    val value: String
) : Serializable {
    private val serialVersionUID = 1L
}

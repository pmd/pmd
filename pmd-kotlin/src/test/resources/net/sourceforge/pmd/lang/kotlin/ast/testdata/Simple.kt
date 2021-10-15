package net.sourceforge.pmd.lang.kotlin.ast.testdata

class Simple {
    private val name = "Simple"
    fun info() = "This is $name class"
}

fun main() {
    val s = Simple()
    println(s)
    println(s.info())
}
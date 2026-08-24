class StringEquivalence {
    // typeIs('kotlin.String') AND typeIs('java.lang.String') should both match
    val message: String = "world"
    fun greet(): String = "hello"

    // should NOT match String typeIs
    val count: Int = 42
}

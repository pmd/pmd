class StringEquivalence {
    // typeIs('kotlin.String') AND typeIs('java.lang.String') should both match
    val message: String = "world"
    fun greet(): String = "hello"

    // should NOT match String typeIs
    val count: Int = 42

    // typeIsExactly('kotlin.String') must match despite the nullable marker
    val nickname: String? = null
}

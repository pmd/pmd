class NullableTypes {
    // nullable generic type -- isNullable() should return true
    val items: List<String>? = null

    // non-nullable generic type -- isNullable() should return false
    val names: List<String> = listOf()

    // nullable return type -- isNullable() should return true
    fun findItem(): String? = null

    // non-nullable return type -- isNullable() should return false
    fun getItem(): String = "hello"

    // nullable simple type -- isNullable() should return true
    val tag: String? = null

    // nullable parameter -- isNullable() should return true
    fun process(input: String?) {}

    // non-nullable parameter -- isNullable() should return false
    fun transform(input: String) {}
}

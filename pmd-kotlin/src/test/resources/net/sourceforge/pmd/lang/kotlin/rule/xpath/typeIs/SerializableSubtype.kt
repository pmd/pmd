package nl.stokpop.kotlin

import java.io.Serializable

class SerializableSubtype(val value: String) : Serializable

class SerializableHolder {
    // typeIs('java.io.Serializable') should match via subtype hierarchy
    val item: SerializableSubtype = SerializableSubtype("hello")

    // typeIsExactly('java.io.Serializable') should NOT match (wrong exact type)
    val item2: SerializableSubtype = SerializableSubtype("world")

    // typeIsExactly('nl.stokpop.kotlin.SerializableSubtype') should match
    val item3: SerializableSubtype = SerializableSubtype("!")
}

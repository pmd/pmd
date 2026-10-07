// A locally-defined class used within the same file: the type-mapper resolves it from source.
package app.local

class LocalClass(val value: String)

fun useLocal(): LocalClass {
    return LocalClass("hello")
}

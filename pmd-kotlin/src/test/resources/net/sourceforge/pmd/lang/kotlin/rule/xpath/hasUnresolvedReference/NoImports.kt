// A file with no imports at all -- hasUnresolvedReference() should not fire.
package app.local

fun greet(name: String): String = "Hello, $name"

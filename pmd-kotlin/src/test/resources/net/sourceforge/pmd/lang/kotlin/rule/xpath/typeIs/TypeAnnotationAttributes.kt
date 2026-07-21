package nl.stokpop.test

import java.util.Calendar

@Deprecated("use Foo instead")
class TypeAnnotationAttributes {

    @Deprecated("use newProcess instead")
    fun process(cal: Calendar): String {
        try {
            return cal.time.toString()
        } catch (e: IllegalArgumentException) {
            return "error"
        }
    }
}

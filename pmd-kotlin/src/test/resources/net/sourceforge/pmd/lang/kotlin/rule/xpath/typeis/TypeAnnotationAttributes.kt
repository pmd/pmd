package net.sourceforge.pmd.lang.kotlin.rule.xpath.typeis

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

import java.io.IOException
import java.io.Serializable
import java.util.Calendar

class ParameterTypes {

    // FunctionValueParameter: 'cal' should match typeIs('java.util.Calendar')
    fun processCalendar(cal: Calendar): String {   // line 8
        return cal.toString()
    }

    // FunctionValueParameter: 'name' should NOT match typeIs('java.util.Calendar')
    fun processString(name: String): String {      // line 13
        return name
    }

    // CatchBlock: should match typeIs('java.io.IOException')
    fun readFile(): String {
        try {
            return "ok"
        } catch (e: IOException) {                 // line 21
            return "error"
        }
    }

    // CatchBlock: should NOT match typeIs('java.io.IOException')
    fun rethrow(): String {
        try {
            return "ok"
        } catch (e: IllegalArgumentException) {    // line 30
            return "error"
        }
    }
}

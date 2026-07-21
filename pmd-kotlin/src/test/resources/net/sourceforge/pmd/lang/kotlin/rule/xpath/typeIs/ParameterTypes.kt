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

    // ForStatement: items are Calendar -- should match typeIs('java.util.Calendar')
    fun processList(items: List<Calendar>) {
        for (item in items) {                      // line 37
            item.time
        }
    }

    // ForStatement: loop over strings -- should NOT match typeIs('java.util.Calendar')
    fun processStrings(items: List<String>) {
        for (s in items) {                         // line 44
            println(s)
        }
    }
}

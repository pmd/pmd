import java.util.Calendar
import java.util.Date

class CalendarUsage {
    // typeIs('java.util.Calendar') should match both
    val meeting: Calendar = Calendar.getInstance()
    fun getDeadline(): Calendar = Calendar.getInstance()

    // should NOT match Calendar typeIs
    val name: String = "hello"
    fun getName(): String = name
}

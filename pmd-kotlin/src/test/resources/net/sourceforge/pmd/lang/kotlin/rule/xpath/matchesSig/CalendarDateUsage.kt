import java.util.Calendar

class CalendarDateUsage {
    fun badGetCurrentTime(): java.util.Date {
        val cal = Calendar.getInstance()
        return cal.time
    }

    fun goodJavaTime(): java.time.LocalDateTime {
        return java.time.LocalDateTime.now()
    }
}

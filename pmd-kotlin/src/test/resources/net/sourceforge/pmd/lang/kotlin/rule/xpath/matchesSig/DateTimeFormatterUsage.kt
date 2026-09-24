import java.time.format.DateTimeFormatter

class DateTimeFormatterUsage {
    // matchesSig for DateTimeFormatter.ofPattern should match this
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // should NOT match - different method
    fun isoDate(): DateTimeFormatter = DateTimeFormatter.ISO_DATE
}

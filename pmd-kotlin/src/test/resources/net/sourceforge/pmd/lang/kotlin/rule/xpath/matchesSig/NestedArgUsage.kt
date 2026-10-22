import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NestedArgUsage {
    // DateTimeFormatter.ofPattern nested as argument to LocalDate.parse — should produce exactly 1 violation
    fun mapRaw(raw: String): LocalDate =
        LocalDate.parse(raw, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

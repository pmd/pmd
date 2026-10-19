import java.util.regex.Pattern

class PatternUsage {
    // matchesSig for Pattern.matches should match this
    fun check(input: String): Boolean = Pattern.matches("[a-z]+", input)

    // should NOT match Pattern.matches - different method
    fun trimIt(s: String): String = s.trim()
}

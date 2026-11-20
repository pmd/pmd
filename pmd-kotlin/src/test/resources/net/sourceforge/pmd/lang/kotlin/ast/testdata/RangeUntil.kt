package example

object Ranges {
    fun sum(n: Int): Int {
        var total = 0
        for (i in 0..<n) {
            total += i
        }
        return total
    }

    fun openEnded(n: Int): IntRange = 0..<n

    fun closed(n: Int): IntRange = 0..n

    fun checked(x: Int, n: Int) {
        require(x in 0..<n)
    }

    // Line break after the operator (grammar allows NL* after RANGE_UNTIL)
    fun multiLine(n: Int): IntRange = 0..<
            n

    // Inside a string template (lexed in a mode pushed from string mode)
    fun template(n: Int): String = "range: ${0..<n}"

    // Precedence: additive binds tighter, so this is 0..<(n + 1)
    fun precedence(n: Int): IntRange = 0..<n + 1

    // Spaces around the operator and a non-trivial right-hand side
    fun spaced(list: List<Int>): IntRange = 0 ..< list.size
}

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
}

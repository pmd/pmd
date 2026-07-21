class ListUsage {
    // matchesSig('kotlin.collections.List#size()') and matchesSig('java.util.List#size()') should match
    fun sizes(items: List<String>): Int = items.size

    // should NOT match List#size - different receiver
    fun stringLen(s: String): Int = s.length
}

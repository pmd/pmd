class PrintStackTraceUsage {
    fun bad() {
        try {
            throw RuntimeException("test")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun good() {
        try {
            throw RuntimeException("test")
        } catch (e: Exception) {
            throw RuntimeException("wrapped", e)
        }
    }
}

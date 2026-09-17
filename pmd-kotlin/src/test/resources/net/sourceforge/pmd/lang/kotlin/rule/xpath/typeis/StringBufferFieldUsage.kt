class StringBufferFieldUsage {
    private val sb: StringBuffer = StringBuffer()
    private val builder: StringBuilder = StringBuilder()

    fun good() {
        val local: StringBuilder = StringBuilder()
    }
}

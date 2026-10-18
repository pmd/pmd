class SystemPrintlnUsage {
    fun bad() {
        System.out.println("debug")
        System.err.println("error")
    }

    fun good(value: Int) {
        val result = value * 2
    }
}

class Foo {
    fun foo() {
        val a = 1
        val b = 2
        val requestedData =
            $$$"""{
              "sum": "$$$$${a + b}"
            }
            """
    }
}


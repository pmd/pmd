class Foo {
    fun foo() {
        val myRef = "x"
        val requestedData =
            $$$"""{
              "a": "$$$$myRef"
            }
            """
    }
}


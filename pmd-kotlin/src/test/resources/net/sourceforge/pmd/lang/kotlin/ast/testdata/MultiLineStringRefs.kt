class Foo {
    fun foo() {
        val productName = "carrot"
        val requestedData =
            $$$"""{
              "currency": "$",
              "enteredAmount": "42.45 $$",
              "$$serviceField": "none",
              "product": "$$$productName"
            }
            """
    }
}


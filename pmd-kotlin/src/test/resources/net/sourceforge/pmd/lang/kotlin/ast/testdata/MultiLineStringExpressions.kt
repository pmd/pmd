class Foo {
    fun foo() {
        val productName = "carrot"
        val requestedData =
            $$$"""{
              "currency": "$",
              "enteredAmount": "42.45 $$",
              "$${serviceField.length()}": "none",
              "product": "$$${productName + "-fixed-postfix"}"
            }
            """
    }
}


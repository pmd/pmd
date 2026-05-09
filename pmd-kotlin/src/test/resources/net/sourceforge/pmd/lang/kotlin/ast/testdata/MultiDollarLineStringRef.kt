class Foo {
    fun foo() {
        val myVar = "hello"
        val s1 = "normal $myVar"
        val s2 = $$"$$myVar and literal $end"
        val s3 = $$"$${myVar.length} chars"
    }
}

class A { fun a() {} }
class B { fun b() {} }

fun demo(a: A, b: B) {
    a.apply outer@{
        b.apply inner@{
            this.b()           // B
            this@outer.a()     // A
            this@inner.b()     // B (explicit)
        }
    }
}
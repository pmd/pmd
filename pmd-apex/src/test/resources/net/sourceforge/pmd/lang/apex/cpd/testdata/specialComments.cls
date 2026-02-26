// CPD-OFF
public class Foo {
    public void bar() {
        Integer a = 1 + 2;
        a += 1;
        a++;
        Decimal d = 3.14;
        d *= 2;
        Boolean b = !(d == 2.0 || d >= 1.0 && d <= 2.0);
    }
// CPD-ON
}

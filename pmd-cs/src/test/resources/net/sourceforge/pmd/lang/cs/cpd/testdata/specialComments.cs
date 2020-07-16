// CPD-OFF
class Foo {
  void bar() {
    int a = 1 >> 2;
    a += 1;
    a++;
    a /= 3e2;
    float f = -3.1;
    f *= 2;
    bool b = ! (f == 2.0 || f >= 1.0 && f <= 2.0)
  }
// CPD-ON
}
public class UnusedPrivateMethod4 {
 private void foo() {}
 private void foo(String baz) {}
 public void bar() {
  foo();
 }
}
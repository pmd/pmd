public class AvoidReassigningParameters5 {

 private class Foo {
  public String bar;
 }

 private void foo(String bar) {
  Foo f = new Foo();
  f.bar = bar;
 }
}
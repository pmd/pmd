public class UnusedPrivateMethod5 {
 private void foo(String[] args) {}
 public static void main(String[] args) {
  UnusedPrivateMethod5 u = new UnusedPrivateMethod5();
  u.foo(args); 
 }
}
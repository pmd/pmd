public class UnusedPrivateMethod3 {
 public void bar() {
  new Runnable() {
   public void run() {
    foo();
   }
  };
 }

 private void foo() {}
}
